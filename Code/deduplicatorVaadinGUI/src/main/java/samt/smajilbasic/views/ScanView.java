package samt.smajilbasic.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;

import samt.smajilbasic.communication.Client;

/**
 * ScanView
 */
@Route(value = "scan", registerAtStartup = true)
public class ScanView extends BaseView {

    private Client client;

    public ScanView() {
        super();
        if (UI.getCurrent().getSession().getAttribute(LoginView.CLIENT_STRING) == null) {
            UI.getCurrent().getPage().setLocation("login/");
        } else {
            client = (Client) UI.getCurrent().getSession().getAttribute(LoginView.CLIENT_STRING);

            Button scanButton = new Button("Start scan", e -> startScan());
            Button stopButton = new Button("Stop scan", e -> stopScan());
            Button pauseButton = new Button("Pause scan", e -> pauseScan());

            Label statusLabel = new Label();
            Label fileScannedLabel = new Label();
            Label totalFilesLabel = new Label();
            Label scanStartedLabel = new Label();
            Label timeLeftLabel = new Label();

            ProgressBar progressBar = new ProgressBar(0d, 1d);
            FormLayout form = new FormLayout();
            form.setResponsiveSteps(new ResponsiveStep("25em", 1), new ResponsiveStep("32em", 2));
            VerticalLayout infoLayout = new VerticalLayout();

            infoLayout.add(statusLabel, fileScannedLabel, totalFilesLabel,
            scanStartedLabel, timeLeftLabel);
            
            form.add(infoLayout, scanButton, stopButton, pauseButton);
            add(form, progressBar);
        }

    }

    private Object pauseScan() {
        return null;
    }

    private Object stopScan() {
        return null;
    }

    private Object startScan() {
        return null;
    }

}