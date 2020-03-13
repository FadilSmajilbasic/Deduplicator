package samt.smajilbasic.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;

import samt.smajilbasic.Resources;
import samt.smajilbasic.communication.Client;

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
  private final int POLLING_DELAY = 500;
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
  private Object statusMonitor = new Object();

  /**
   * The ScanView constructor.
   */
  public ScanView() {
    client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);

    if (client != null) {
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
      buttonsForm.setResponsiveSteps(new ResponsiveStep("1em", 1));
      form.add(infoLayout, 2);
      form.add(buttonsForm);

      add(form, progressBar);

      createStatusThread();
      updateStatus(false, 0, Calendar.getInstance().getTime().getTime(), 0f);
    }

  }

  /**
   * The resumeScan method resumes a paused scan.
   */
  private void resumeScan() {
    HttpStatus resp = client.post("scan/resume", null).getStatusCode();
    if (resp.equals(HttpStatus.OK)) {
      Notification.show("Scan resumend", Resources.NOTIFICATION_LENGTH, Position.TOP_END);
      pauseButton.setVisible(true);
      resumeButton.setVisible(false);
      paused = false;
      statusMonitor.notifyAll();
    } else {
      Notification.show("Scan not running", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
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
  public void updateStatus(boolean status, Integer filesScanned, Long dateStarted, float progress) {
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    Calendar cal = Calendar.getInstance();
    if (dateStarted != null) {
      cal.setTimeInMillis(dateStarted);
      scanStartedLabel.setValue(dateFormat.format(cal.getTime()));
    }
    statusLabel.setValue((status ? "scanning" : "not running"));
    if (status) {
      pauseButton.setVisible(true);
      resumeButton.setVisible(true);
      stopButton.setVisible(true);
    } else {
      pauseButton.setVisible(false);
      resumeButton.setVisible(false);
      stopButton.setVisible(false);
    }
    if (filesScanned != null) {
      filesScannedLabel.setValue(String.valueOf(filesScanned));
    }
    totalFilesLabel.setValue(String.valueOf((int) (((float) filesScanned) / progress)));
    timeLeftLabel.setValue("--");
    progressBar.setValue(progress);

  }

  /**
   * The recreateStatusThread creates a new statusThread after it gets
   * interrupted.
   */
  private void createStatusThread() {
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
            if (response.get("message") == null) {

              Command command = new Command() {
                @Override
                public void execute() {
                  updateStatus(true, Integer.parseInt(response.get("fileCount").toString()),
                      Long.parseLong(response.get("timestamp").toString()),
                      Float.parseFloat(response.get("progress").toString()));
                }
              };
              System.out.println("Got status" + scanProgress);
              scanProgress = Float.parseFloat(response.get("progress").toString());
              getUI().get().access(command);
            } else {
              Command com = new Command() {
                @Override
                public void execute() {
                  Notification.show(response.get("message").toString(), Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                      .addThemeVariants(NotificationVariant.LUMO_ERROR);
                  updateStatus(false, 0, Calendar.getInstance().getTime().getTime(), 0f);
                }
              };
              getUI().get().access(com);

              this.interrupt();
            }
            synchronized (this) {
              this.wait(POLLING_DELAY);
            }
          }

        } catch (InterruptedException ie) {
          Notification.show("Status update interrupted", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
              .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } finally {
          Command com = new Command() {
            @Override
            public void execute() {
              Notification.show("Scan finished", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                  .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
              updateStatus(false, 0, null, 0f);
            }
          };
          getUI().get().access(com);
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
      Notification.show("Scan paused", Resources.NOTIFICATION_LENGTH, Position.TOP_END);
      statusThread.interrupt();
      pauseButton.setVisible(false);
      resumeButton.setVisible(true);
      paused = true;
    } else {
      Notification.show("Scan not running", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
          .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

  }

  /**
   * The stopScan method stops the scan and the statusThread.
   */
  private void stopScan() {
    HttpStatus resp = client.post("scan/stop", null).getStatusCode();
    if (resp.equals(HttpStatus.OK)) {
      Notification.show("Scan stopped", Resources.NOTIFICATION_LENGTH, Position.TOP_END);
      statusThread.interrupt();
    } else {
      Notification.show("Scan not running", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
          .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
  }

  /**
   * The startScan method start a scan and a new statusThread
   */
  private void startScan() {
    HttpStatus resp = client.startScan();

    if (resp == HttpStatus.OK) {
      Notification.show("Scan started", Resources.NOTIFICATION_LENGTH, Position.TOP_END);
      createStatusThread();
      statusThread.start();
    } else if (resp == HttpStatus.ALREADY_REPORTED) {
      Notification.show("Scan already running", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
          .addThemeVariants(NotificationVariant.LUMO_ERROR);
    } else if (resp == HttpStatus.INTERNAL_SERVER_ERROR) {
      Notification.show("Unable to start scan", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
          .addThemeVariants(NotificationVariant.LUMO_ERROR);

    }
  }

}