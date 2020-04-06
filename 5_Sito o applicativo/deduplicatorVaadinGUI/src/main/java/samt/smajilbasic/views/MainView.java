package samt.smajilbasic.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.shared.ui.Transport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.model.Resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * MainView
 */
@PWA(name = "Deduplicator GUI", shortName = "Deduplicator", description = "Deduplicator GUI to control the deduplicator service.", enableInstallPrompt = true)
@Route(value = "", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@PageTitle(value = "Deduplicator - Home")
public class MainView extends VerticalLayout {

    public static final String VIEW_NAME = "Home";
    private Client client;

    /**
     * The parser used to read the JSON response from the server when updating the
     * status.
     */
    private JSONParser parser = new JSONParser();

	public MainView() {
        this.setSizeFull();
        this.setAlignItems(Alignment.CENTER);
        add(new Label("Welcome to Main View"),new Label("Select a section from the side menu"));
        setMinWidth(Resources.SIZE_MOBILE_S);
        client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);
        UI ui = UI.getCurrent();
        ui.getPushConfiguration().setPushMode(PushMode.MANUAL);
        ui.getPushConfiguration().setTransport(Transport.WEBSOCKET);
        Command updateTable = (Command) () -> {
            if (client != null) {
                ReportView report = new ReportView(true);
                report.setSizeFull();
                add(report);
            }
        };
        Button lastReportButton = new Button("Load last report",event -> ui.access(updateTable));
        lastReportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(lastReportButton);
    }


}