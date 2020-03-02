package samt.smajilbasic.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

import samt.smajilbasic.Resources;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.entity.GlobalPath;
import samt.smajilbasic.entity.Report;

/**
 * ReportView
 */
@Route(value = "reports", layout = MainLayout.class)
@PageTitle(value = "Deduplicator - Report")
public class ReportView extends VerticalLayout {

    public static final String VIEW_NAME = "Reports";
    /**
     * The parser used to read the JSON response from the server when updating the
     * status.
     */
    private JSONParser parser = new JSONParser();
    /**
     * The HTTP/HTTPS client used for the communication.
     */
    private Client client;
    /**
     * The encoder used to map the return values from the server to a GlobalPath
     * object.
     */
    private Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();

    private Grid<Grid<GlobalPath>> duplicatesGrid;

    public ReportView() {
        client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);

        if (client != null) {

            Select<String> reportSelect = new Select<String>();
            reportSelect.setItems(getReports());

            reportSelect.addValueChangeListener(event -> {
                updateGrid(event.getValue());
            });
            Button button = new Button();
            button.setIcon(VaadinIcon.INFO.create());

            button.addClickListener(event -> {
                Dialog dialog = new Dialog();
                dialog.add(new Label("report info"));
                dialog.open();
            });

            FormLayout form = new FormLayout();
            form.setResponsiveSteps(new ResponsiveStep("0em", 2));
            form.add(reportSelect, button);
            Div container = new Div();
            container.add(form);
            container.setWidth("80%");

            duplicatesGrid = new Grid<Grid<GlobalPath>>();

            add(container, duplicatesGrid);
        }

    }

    private void updateGrid(String value) {

        if (!value.isEmpty() && value.length() > 0) {
            System.out.println("path: " + value.split(":")[0]);
            ResponseEntity<String> response = client.get("report/duplicate/" + value.split(":")[0]);

            if (response != null) {
                try {
                    JSONObject[] array = Utils.getArray((JSONArray) parser.parse(response.getBody()));
                    List<JSONObject> duplicates = new ArrayList<JSONObject>();
                    List<Grid<GlobalPath>> insideGrids = new ArrayList<Grid<GlobalPath>>();
                    for (int i = 0; i < array.length; i++) {
                        try {
                            duplicates.add(array[i]);
                            Grid<GlobalPath> insideGrid = new Grid<>();

                            insideGrid.setItems(getPathsFromDuplicate(value, array[i].get("hash").toString()));

                            insideGrid.addColumn(GlobalPath::getPath).setHeader("Path").setFlexGrow(1);
                            insideGrid.addColumn(GlobalPath::getDateFormatted).setHeader("Date added").setFlexGrow(0);
                            insideGrid.addColumn(GlobalPath::isignoreFile).setHeader("Ignored").setFlexGrow(0);

                            insideGrids.add(insideGrid);

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    duplicatesGrid.setItems(insideGrids);
                } catch (ParseException pe) {
                    Notification.show("Unable to read status", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
            Notification.show("Unable to retrieve reports", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private List<GlobalPath> getPathsFromDuplicate(String report, String hash) {
        ResponseEntity<String> response = client.get("report/duplicate/" + report.split(":")[0] + "/" + hash);

        if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                JSONObject[] array = Utils.getArray((JSONArray) parser.parse(response.getBody()));
                List<GlobalPath> paths = new ArrayList<GlobalPath>();

                for (JSONObject jsonObject : array) {
                    try {
                        paths.add(encoder.getObjectMapper().readValue(jsonObject.toJSONString(), GlobalPath.class));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                return paths;

            } catch (ParseException pe) {
                Notification
                        .show("Unable to read paths from duplicate", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return null;
            }
        } else {
            Notification
                    .show("Unable to retrieve paths from duplicate", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return null;
        }
    }

    private List<String> getReports() {
        ResponseEntity<String> response = client.get("report/all/reduced");

        if (response != null) {
            try {
                JSONObject[] array = Utils.getArray((JSONArray) parser.parse(response.getBody()));
                List<String> reports = new ArrayList<String>();

                for (JSONObject jsonObject : array) {
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(Long.parseLong((String)jsonObject.get("start")));
                        reports.add(String.format("%s: %s", jsonObject.get("id"), dateFormat.format(cal.getTime())));
                    } catch (Exception e) {
                        System.out.println("Excception report: " + e.getLocalizedMessage());
                    }
                }
                return reports;

            } catch (ParseException pe) {
                Notification.show("Unable to read status", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return null;
            }
        }
        Notification.show("Unable to retrieve reports", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        return null;
    }
}
