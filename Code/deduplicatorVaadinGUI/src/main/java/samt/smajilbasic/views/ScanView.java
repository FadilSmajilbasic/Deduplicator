package samt.smajilbasic.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import com.vaadin.navigator.Navigator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import samt.smajilbasic.communication.Client;

/**
 * ScanView
 */
@Route(value = "scan", registerAtStartup = true)
public class ScanView extends BaseView {

  private static final long serialVersionUID = 7985454510058866003L;
  private Client client;

  private Label statusLabel = new Label();
  private Label fileScannedLabel = new Label();
  private Label totalFilesLabel = new Label();
  private Label scanStartedLabel = new Label();
  private Label timeLeftLabel = new Label();
  private ProgressBar progressBar = new ProgressBar(0d, 1d);
  private float scanProgress = 0;
  private final int POLLING_DELAY = 200;
  private Thread statusThread;

  private Button scanButton;
  private Button stopButton;
  private Button pauseButton;
  private Button resumeButton;
  private boolean paused = false;
  private Object statusMonitor = new Object();

  public ScanView() {
    super();
    if (UI.getCurrent().getSession().getAttribute(LoginView.CLIENT_STRING) == null) {
      UI.getCurrent().getPage().setLocation("login/");
    } else {
      client = (Client) UI.getCurrent().getSession().getAttribute(LoginView.CLIENT_STRING);

      scanButton = new Button("Start scan", e -> startScan());
      stopButton = new Button("Stop scan", e -> stopScan());
      pauseButton = new Button("Pause scan", e -> pauseScan());
      resumeButton = new Button("Resume scan", e -> resumeScan());

      FormLayout form = new FormLayout();
      form.setResponsiveSteps(new ResponsiveStep("10em", 1), new ResponsiveStep("32em", 3));
      VerticalLayout infoLayout = new VerticalLayout();

      infoLayout.add(statusLabel, fileScannedLabel, totalFilesLabel, scanStartedLabel, timeLeftLabel);
      FormLayout buttonsForm = new FormLayout(scanButton, stopButton, pauseButton, resumeButton);
      buttonsForm.setResponsiveSteps(new ResponsiveStep("1em", 1));
      form.add(infoLayout, 2);
      form.add(buttonsForm);

      add(form, progressBar);

      recreateStatusThread();
      statusThread.start();
    }

  }

  private void resumeScan() {
    HttpStatus resp = client.post("scan/pause", null).getStatusCode();
    if (resp.equals(HttpStatus.OK)) {
      Notification.show("Scan resumend", NOTIFICATION_LENGTH, Position.TOP_END);
      pauseButton.setVisible(true);
      resumeButton.setVisible(false);
      paused = false;
      statusMonitor.notifyAll();
    } else {
      Notification.show("Scan not running", NOTIFICATION_LENGTH, Position.TOP_END)
          .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    

  }

  public void updateStatus(boolean status, String filesScanned, long dateStarted, float progress) {
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(dateStarted);

    statusLabel.setText("Status:\t" + (status ? "scanning" : "not running"));
    fileScannedLabel.setText("Files scanned:\t" + filesScanned);
    totalFilesLabel.setText("Total files:\t" + (int) (Float.parseFloat(filesScanned) / progress));
    scanStartedLabel.setText("Scan started:\t" + dateFormat.format(cal.getTime()));

    timeLeftLabel.setText("Status:\t" + (status ? "scanning" : "not running"));
    scanProgress = progress;
    progressBar.setValue(progress);
  }

  private void recreateStatusThread() {
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
              updateStatus(true, response.get("fileCount").toString(),
                  Long.parseLong(response.get("timestamp").toString()),
                  Float.parseFloat(response.get("progress").toString()));

            } else {
              Notification.show(response.get("message").toString(), NOTIFICATION_LENGTH, Position.TOP_END)
                  .addThemeVariants(NotificationVariant.LUMO_ERROR);
              updateStatus(false, Calendar.getInstance().getTime().toString(), 0l, 0f);
              this.interrupt();
            }
            synchronized (this) {
              this.wait(POLLING_DELAY);
            }
          }
        } catch (InterruptedException ie) {
          Notification.show("Status update interrupted", NOTIFICATION_LENGTH, Position.TOP_END)
              .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
      }
    };
  }

  private void pauseScan() {
    HttpStatus resp = client.post("scan/pause", null).getStatusCode();
    if (resp.equals(HttpStatus.OK)) {
      Notification.show("Scan paused", NOTIFICATION_LENGTH, Position.TOP_END);
      statusThread.interrupt();
      pauseButton.setVisible(false);
      resumeButton.setVisible(true);
      paused = true;

    } else {
      Notification.show("Scan not running", NOTIFICATION_LENGTH, Position.TOP_END)
          .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

  }

  private void stopScan() {
    HttpStatus resp = client.post("scan/stop", null).getStatusCode();
    if (resp.equals(HttpStatus.OK)) {
      Notification.show("Scan stopped", NOTIFICATION_LENGTH, Position.TOP_END);
      statusThread.interrupt();
    } else {
      Notification.show("Scan not running", NOTIFICATION_LENGTH, Position.TOP_END)
          .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
  }

  private void startScan() {
    HttpStatus resp = client.startScan();

    if (resp == HttpStatus.OK) {
      Notification.show("Scan started", NOTIFICATION_LENGTH, Position.TOP_END);
      recreateStatusThread();
      statusThread.start();
    } else if (resp == HttpStatus.ALREADY_REPORTED) {
      Notification.show("Scan already running", NOTIFICATION_LENGTH, Position.TOP_END)
          .addThemeVariants(NotificationVariant.LUMO_ERROR);
    } else if (resp == HttpStatus.INTERNAL_SERVER_ERROR) {
      Notification.show("Unable to start scan", NOTIFICATION_LENGTH, Position.TOP_END)
          .addThemeVariants(NotificationVariant.LUMO_ERROR);

    }
  }

}