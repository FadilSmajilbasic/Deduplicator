package samt.smajilbasic.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
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
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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

/**
 * ReportView
 */
@Route(value = "reports", layout = MainLayout.class)
@PageTitle(value = "Deduplicator - Report")
@CssImport(value = "./styles/report-view.css")
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
            this.setSizeFull();
            Select<String> reportSelect = new Select<String>();
            reportSelect.setItems(getReports());

            reportSelect.addValueChangeListener(event -> {
                updateGrid(event.getValue());
            });
            Button button = new Button();
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.setIcon(VaadinIcon.INFO.create());

            button.addClickListener(event -> {
                Dialog dialog = new Dialog();
                dialog.add(new Label("report info"));
                dialog.open();
            });
            Div buttonContainer = new Div();
            buttonContainer.add(button);
            buttonContainer.setWidth("fit-content");
            FormLayout form = new FormLayout();
            form.setResponsiveSteps(new ResponsiveStep("0em", 2));
            form.add(reportSelect, buttonContainer);
            Div container = new Div();
            container.add(form);
            container.setWidth("80%");

            duplicatesGrid = new Grid<Grid<GlobalPath>>();
            duplicatesGrid.setSizeFull();
            add(container, duplicatesGrid);
        }
    }

    /**
     * Class that describes the duplicate object returned by the API when querying
     * all duplicates of a report.
     */
    private static final class MinimalDuplicate {
        private String hash;
        private String size;
        private String count;

        /**
         * @return the hash
         */
        public String getHash() {
            return hash;
        }

        /**
         * @param hash the hash to set
         */
        public void setHash(String hash) {
            this.hash = hash;
        }

        /**
         * @return the size
         */
        public String getSize() {
            return size;
        }

        /**
         * @param size the size to set
         */
        public void setSize(String size) {
            this.size = size;
        }

        /**
         * @return the count
         */
        public String getCount() {
            return count;
        }

        /**
         * @param count the count to set
         */
        public void setCount(String count) {
            this.count = count;
        }

    }

    private void updateGrid(String value) {

        if (!value.isEmpty() && value.length() > 0) {
            System.out.println("path: " + value.split(":")[0]);
            ResponseEntity<String> response = client.get("report/duplicate/" + value.split(":")[0] + "/");

            if (response != null) {
                try {
                    JSONObject[] array = Utils.getArray((JSONArray) parser.parse(response.getBody()));
                    List<MinimalDuplicate> duplicates = new ArrayList<MinimalDuplicate>();
                    List<Grid<GlobalPath>> insideGrids = new ArrayList<Grid<GlobalPath>>();

                    for (int i = 0; i < array.length; i++) {
                        try {
                            MinimalDuplicate duplicate = new MinimalDuplicate();
                            System.out.println("dup: " + array[i]);
                            duplicate.setHash(array[i].get("hash").toString());
                            duplicate.setSize(array[i].get("size").toString());
                            duplicate.setCount(array[i].get("count").toString());
                            duplicates.add(duplicate);
                            Grid<GlobalPath> insideGrid = new Grid<GlobalPath>();
                            insideGrid.setItems(getPathsFromDuplicate(value, duplicate.getHash()));
                            insideGrid.addColumn(GlobalPath::getPath).setHeader("Path").setFlexGrow(2);
                            insideGrid.addColumn(GlobalPath::getDateFormatted).setHeader("Date added").setFlexGrow(1);
                            insideGrids.add(insideGrid);

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    Iterator<MinimalDuplicate> duplicatesIterator = duplicates.iterator();

                    System.out.println("size: " + duplicates.size());
                    duplicatesGrid.removeAllColumns();
                    duplicatesGrid.addColumn(new ComponentRenderer<>((grid -> {
                        MinimalDuplicate dup = duplicatesIterator.next();
                        Label hashLabel = new Label("Duplicate: " + dup.getHash());
                        Label sizeLabel = new Label("Size: " + dup.getSize());
                        Label countLabel = new Label("Count: " + dup.getCount());

                        hashLabel.setClassName("duplicate-header-label");
                        sizeLabel.setClassName("duplicate-header-label");
                        countLabel.setClassName("duplicate-header-label");

                        FlexLayout hLayout = new FlexLayout(hashLabel, sizeLabel, countLabel);
                        hLayout.setFlexGrow(1, hashLabel);
                        hLayout.setFlexGrow(1, sizeLabel);
                        hLayout.setFlexGrow(1, countLabel);

                        hLayout.setClassName("duplicate-header");
                        hLayout.setSizeFull();
                        VerticalLayout vLayout = new VerticalLayout();
                        vLayout.add(hLayout);
                        vLayout.add(grid);
                        vLayout.setSizeFull();

                        return vLayout;

                    }))).setHeader("Duplicates");
                    duplicatesGrid.setItems(insideGrids);

                } catch (ParseException pe) {
                    Notification.show("Unable to read status", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show("Unable to retrieve reports", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
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
                        System.out.println("Path: " + encoder.getObjectMapper()
                                .readValue(jsonObject.toJSONString(), GlobalPath.class).getPath());
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
                        cal.setTimeInMillis(Long.parseLong((String) jsonObject.get("start")));
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
