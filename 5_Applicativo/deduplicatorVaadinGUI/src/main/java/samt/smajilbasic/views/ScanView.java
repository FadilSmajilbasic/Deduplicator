package samt.smajilbasic.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;

import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.shared.ui.Transport;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.properties.Settings;

/**
 * ScanView is the view to manage the views.
 */
@Route(value = "scan", layout = MainLayout.class)
@PageTitle(value = "Deduplicator - Scan")
@CssImport(value = "./styles/scan-input.css", themeFor = "vaadin-text-field")
public class ScanView extends VerticalLayout {

    private static final long serialVersionUID = 7985454510058866003L;

    public static String VIEW_NAME = "Scan";

    /**
     * The HTTP/HTTPS client.
     */
    private Client client;

    /**
     * The label for the scan status.
     */
    private TextField statusLabel = new TextField("Status");
    /**
     * The label for the number of files scanned.
     */
    private TextField filesScannedLabel = new TextField("Files scanned");
    /**
     * The label for the total number of files.
     */
    private TextField totalFilesLabel = new TextField("Total Files");
    /**
     * The label for the date and time that the scan started.
     */
    private TextField scanStartedLabel = new TextField("Start Date");
    /**
     * The label for the time left to completion of the scan.
     */
    private TextField timeLeftLabel = new TextField("Time Left");
    /**
     * The progress bar that displays the scan progress
     */
    private ProgressBar progressBar = new ProgressBar(0d, 1d);
    /**
     * The progress of the scan rappresented as a float, the value goes from 0 to 1
     */
    private float scanProgress = 0f;
    /**
     * The delay at which the statusThread will poll the scan status.
     */
    private final int POLLING_DELAY;
    /**
     * The thread that polls the scan status data.
     */
    private Thread statusThread;

    /**
     * The button that starts a scan.
     */
    private Button scanButton;
    /**
     * The button that stop the ongoing scan.
     */
    private Button stopButton;
    /**
     * The button that pauses an ongoing scan
     */
    private Button pauseButton;
    /**
     * The button that resumes a paused scan.
     */
    private Button resumeButton;
    /**
     * The flag that indicates that the statusThread needs to pause.
     */
    private boolean paused = false;

    /**
     * The monitor that the statusThread waits onto. When the statusThread needs to
     * resume the program calls the notifyAll method of this monitor.
     */
    private final Object statusMonitor = new Object();

    private final UI ui;
    Settings props = new Settings();
    private Settings settings = new Settings();

    /**
     * The ScanView constructor.
     */
    public ScanView() {
        POLLING_DELAY = props.getRefreshInterval();

        client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);
        ui = UI.getCurrent();
        if (client != null) {
            ui.getPushConfiguration().setPushMode(PushMode.MANUAL);
            ui.getPushConfiguration().setTransport(Transport.WEBSOCKET);

            scanButton = new Button("Start scan", e -> startScan());
            stopButton = new Button("Stop scan", e -> stopScan());
            pauseButton = new Button("Pause scan", e -> pauseScan());
            resumeButton = new Button("Resume scan", e -> resumeScan());

            FormLayout form = new FormLayout();
            form.setResponsiveSteps(new ResponsiveStep("10em", 1), new ResponsiveStep("32em", 3));
            VerticalLayout infoLayout = new VerticalLayout();

            statusLabel.setReadOnly(true);
            filesScannedLabel.setReadOnly(true);
            totalFilesLabel.setReadOnly(true);
            scanStartedLabel.setReadOnly(true);
            timeLeftLabel.setReadOnly(true);

            statusLabel.setClassName("custom-input");
            filesScannedLabel.setClassName("custom-input");
            totalFilesLabel.setClassName("custom-input");
            scanStartedLabel.setClassName("custom-input");
            timeLeftLabel.setClassName("custom-input");

            infoLayout.add(statusLabel, filesScannedLabel, totalFilesLabel, scanStartedLabel, timeLeftLabel);
            FormLayout buttonsForm = new FormLayout(scanButton, stopButton, pauseButton, resumeButton);
            buttonsForm.setResponsiveSteps(new ResponsiveStep("1", 1));
            form.add(infoLayout, 2);
            form.add(buttonsForm);

            add(progressBar,form );

            setMinWidth(Resources.SIZE_MOBILE_S);
            updateStatus(false, 0, 0, Calendar.getInstance().getTime().getTime(), 0f, 0);

        }
    }

    /**
     * The resumeScan method resumes a paused scan.
     */
    private void resumeScan() {
        HttpStatus resp = client.post("scan/resume", null).getStatusCode();
        if (resp.equals(HttpStatus.OK)) {
            Notification.show("Scan resumed", settings.getNotificationLength(), Position.TOP_END);
            pauseButton.setEnabled(true);
            resumeButton.setEnabled(false);
            UI.getCurrent().push();
            paused = false;
            synchronized (statusMonitor) {
                statusMonitor.notifyAll();
            }
        } else {
            Notification.show("Scan not running", settings.getNotificationLength(), Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

    }

    /**
     * The updateStatus method changes the values of the status labels.
     *
     * @param status       a boolean telling wether the scan is running or not.
     * @param filesScanned the number of files scanned.
     * @param dateStarted  the date and time of the start time of the scan,
     *                     formatted as a timestamp
     * @param progress     the progress of the scan written as a float
     */
    public void updateStatus(boolean status, int totalFiles, Integer filesScanned, Long dateStarted, float progress,
                             int timeLeft) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Calendar cal = Calendar.getInstance();
        if (dateStarted != null) {
            cal.setTimeInMillis(dateStarted);
            scanStartedLabel.setValue(dateFormat.format(cal.getTime()));
        }
        statusLabel.setValue((status ? "scanning" : "not running"));
        if (status) {
            pauseButton.setEnabled(true);
            resumeButton.setEnabled(true);
            stopButton.setEnabled(true);
        } else {
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(false);
            stopButton.setEnabled(false);
        }
        UI.getCurrent().push();
        if (filesScanned != null) {
            filesScannedLabel.setValue(String.valueOf(filesScanned));
        }
        totalFilesLabel.setValue(String.valueOf(totalFiles));
        timeLeftLabel.setValue(timeLeft + " sec.");
        progressBar.setValue(progress);

    }

    private int filesScannedOld = 0;

    /**
     * The createStatusThread creates a new statusThread.
     *
     * @param silent parameter definig wether the executuion should be silent or not.
     */
    private void createStatusThread(boolean silent) {
        statusThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted() && scanProgress < 1f && scanProgress != -1f && scanProgress >= 0f) {
                        synchronized (statusMonitor) {
                            if (paused) {
                                statusMonitor.wait();
                            }
                        }
                        JSONObject response = client.getStatus();
                        if (response != null) {
                            if (response.get("message") == null) {
                                int filesScanned = Integer.parseInt(response.get("fileCount").toString());
                                int filesScannedDelta = filesScanned - filesScannedOld;
                                int totFiles = Integer.parseInt(response.get("totalFiles").toString());
                                float pace = ((float) POLLING_DELAY) / (float) filesScannedDelta;
                                float timeLeft = ((totFiles - filesScanned) * pace) / 1000;

                                Command command = (Command) () -> {
                                    updateStatus(true, totFiles, filesScanned,
                                        Long.parseLong(response.get("timestamp").toString()),
                                        Float.parseFloat(response.get("progress").toString()), (int) timeLeft);
                                    ui.push();
                                };

                                scanProgress = Float.parseFloat(response.get("progress").toString());
                                ui.access(command);
                                filesScannedOld = filesScanned;
                                synchronized (this) {
                                    this.wait(POLLING_DELAY);
                                }
                            } else {
                                Command finalCommand = (Command) () -> {
                                    Logger.getGlobal().log(Level.INFO, "Scan is not running");

                                    if (!silent)
                                        Notification
                                            .show(response.get("message").toString(),
                                                settings.getNotificationLength(), Position.TOP_END)
                                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                    updateStatus(false, 0, 0, Calendar.getInstance().getTime().getTime(), 0f, 0);
                                    ui.push();
                                };
                                ui.access(finalCommand);
                                this.interrupt();
                            }
                        } else {
                            ui.access(() -> {
                                Logger.getGlobal().log(Level.INFO, "Scan is not running");
                                if (!silent)
                                    Notification.show("Scan is not running", settings.getNotificationLength(),
                                        Position.TOP_END);
                                updateStatus(false, 0, 0, Calendar.getInstance().getTime().getTime(), 0f, 0);
                                ui.push();
                            });
                            this.interrupt();
                        }
                    }
                } catch (InterruptedException ie) {
                    ui.access(() -> {
                        if (!silent)
                            Notification.show("Status update interrupted", settings.getNotificationLength(),
                                Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        Logger.getGlobal().log(Level.INFO, "Status update interrupted");
                        ui.push();
                    });
                } finally {

                    ui.access(() -> {
                        if (!silent)
                            Notification.show("Scan finished", settings.getNotificationLength(), Position.TOP_END)
                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        Logger.getGlobal().log(Level.INFO, "Scan finished");
                        updateStatus(false, 0, 0, null, 0f, 0);
                        ui.push();
                    });
                }
            }
        };
    }

    /**
     * The pauseScan method pauses the scan and the statusThread.
     */
    private void pauseScan() {
        HttpStatus resp = client.post("scan/pause", null).getStatusCode();
        if (resp.equals(HttpStatus.OK)) {
            Notification.show("Scan paused", settings.getNotificationLength(), Position.TOP_END);
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(true);
            UI.getCurrent().push();
            paused = true;
        } else {
            Notification.show("Scan not running", settings.getNotificationLength(), Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * The stopScan method stops the scan and the statusThread.
     */
    private void stopScan() {
        HttpStatus resp = client.post("scan/stop", null).getStatusCode();
        if (resp.equals(HttpStatus.OK)) {
            if (statusThread != null) {
                Notification.show("Scan stopped", settings.getNotificationLength(), Position.TOP_END);
                statusThread.interrupt();
                statusThread = null;
            }
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(false);
            UI.getCurrent().push();
        } else {
            Notification.show("Scan not running", settings.getNotificationLength(), Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * The startScan method start a scan and a new statusThread
     */
    private void startScan() {
        ResponseEntity<String> resp = client.post("scan/start/", null);
        paused = false;
        if (resp != null) {
            if (resp.getStatusCode().equals(HttpStatus.OK)) {
                Notification.show("Scan started", settings.getNotificationLength(), Position.TOP_END);
                createStatusThread(false);
                statusThread.start();
            } else if (resp.getStatusCode().equals(HttpStatus.ALREADY_REPORTED)) {
                Notification.show("Scan already running", settings.getNotificationLength(), Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                createStatusThread(false);
                statusThread.start();
            } else if (resp.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                Notification.show("Unable to start scan - " + resp.getBody(), settings.getNotificationLength(), Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);

            }
        } else {
            Notification.show("Unable to start scan", settings.getNotificationLength(), Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        stopScan();
    }
}