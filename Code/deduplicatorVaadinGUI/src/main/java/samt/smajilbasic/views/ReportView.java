package samt.smajilbasic.views;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.apache.tomcat.jni.Global;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

import org.vaadin.filesystemdataprovider.FilesystemData;
import org.vaadin.filesystemdataprovider.FilesystemDataProvider;
import samt.smajilbasic.ActionType;
import samt.smajilbasic.Resources;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.entity.Action;
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

    private List<GlobalPath> actions = new ArrayList<GlobalPath>();
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
            Button infoButton = new Button();
            infoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            infoButton.setIcon(VaadinIcon.INFO.create());

            infoButton.addClickListener(event -> {
                Dialog dialog = new Dialog();
                dialog.add(new Label("report info"));
                dialog.open();
            });
            Button applyButton = new Button("Apply selected changes", event -> {
                checkActions();
            });
            applyButton.setMinWidth("");

            VerticalLayout verticalLayout = new VerticalLayout();
            FormLayout formButtonsLayout = new FormLayout(reportSelect, applyButton, infoButton);
            formButtonsLayout.setWidthFull();
            verticalLayout.add(formButtonsLayout);
            verticalLayout.setAlignItems(Alignment.START);
            formButtonsLayout.setResponsiveSteps(new ResponsiveStep(Resources.SIZE_MOBILE_S,1),new ResponsiveStep(Resources.SIZE_MOBILE_M,2));

            duplicatesGrid = new Grid<Grid<GlobalPath>>();
            duplicatesGrid.setSizeFull();
            add(verticalLayout, duplicatesGrid);
            setMinWidth(Resources.SIZE_MOBILE_S);
        }
    }

    private void checkActions() {
        actions = new ArrayList<GlobalPath>();
        ListDataProvider<Grid<GlobalPath>> insideGrids = (ListDataProvider<Grid<GlobalPath>>) duplicatesGrid
                .getDataProvider();

        Iterator<Grid<GlobalPath>> insideGridsIterator = insideGrids.getItems().iterator();

        while (insideGridsIterator.hasNext()) {
            Grid<GlobalPath> insideGrid = insideGridsIterator.next();
            ListDataProvider<GlobalPath> provider = (ListDataProvider<GlobalPath>) insideGrid.getDataProvider();

            Iterator<GlobalPath> items = provider.getItems().iterator();

            while (items.hasNext()) {
                GlobalPath item = items.next();
                Action action = item.getAction();
                if (action != null) {
                    if (action.getType() != null) {
                        try {
                            System.out.println("Item: " + item.getPath());
                            System.out.println("Action: " + encoder.getObjectMapper().writeValueAsString(action));
                            actions.add(item);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        Dialog applyDialog = new Dialog();
        VerticalLayout applyDialogLayout = new VerticalLayout();
        applyDialogLayout.setSizeFull();

        DatePicker datePicker = new DatePicker();
        TimePicker timePicker = new TimePicker();

        LocalDateTime ldt = LocalDateTime.now();
        ldt = ldt.plusMinutes(1);
        datePicker.setValue(ldt.toLocalDate());
        timePicker.setValue(ldt.toLocalTime());

        timePicker.setLocale(Locale.GERMAN);
        datePicker.setLocale(Locale.GERMAN);

        timePicker.setMin(LocalTime.now().toString());

        ValueChangeListener listener = new ValueChangeListener<ValueChangeEvent<?>>() {

            @Override
            public void valueChanged(ValueChangeEvent<?> event) {
                LocalDateTime inputs = LocalDateTime.of(datePicker.getValue(), timePicker.getValue());
                if (inputs.isBefore(LocalDateTime.now()) && !inputs.isEqual(LocalDateTime.now())) {
                    Notification.show("Date and time can't be in the past", Resources.NOTIFICATION_LENGTH,
                            Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    datePicker.setValue(LocalDate.now());
                    timePicker.setValue(LocalTime.now());
                }
            }
        };

        datePicker.addValueChangeListener(listener);
        timePicker.addValueChangeListener(listener);

        HorizontalLayout pickerLayout = new HorizontalLayout(datePicker, timePicker);

        Button applyDialogConfirmButton = new Button("Confirm", event -> {

            client.addActions(LocalDateTime.of(datePicker.getValue(), timePicker.getValue()), actions);
            applyDialog.close();
        });

        applyDialog.add(pickerLayout, applyDialogConfirmButton);
        applyDialog.open();
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

                    List<JSONObject> arrayList = new ArrayList<JSONObject>();
                    for (JSONObject item : array) {
                        arrayList.add(item);
                    }
                    Iterator<JSONObject> iterator = arrayList.iterator();

                    while (iterator.hasNext()) {
                        try {
                            JSONObject obj = iterator.next();
                            MinimalDuplicate duplicate = new MinimalDuplicate();
                            System.out.println("dup: " + obj);
                            duplicate.setHash(obj.get("hash").toString());
                            duplicate.setSize(obj.get("size").toString());
                            duplicate.setCount(obj.get("count").toString());
                            duplicates.add(duplicate);
                            Grid<GlobalPath> insideGrid = new Grid<GlobalPath>();
                            insideGrid.setItems(getPathsFromDuplicate(value, duplicate.getHash()));
                            insideGrid.addColumn(GlobalPath::getPath).setHeader("Path").setFlexGrow(2);
                            insideGrid.addColumn(GlobalPath::getDateFormatted).setHeader("Date modified")
                                    .setFlexGrow(1);

                            insideGrid.addColumn(new ComponentRenderer<>(item -> {
                                FlexLayout buttonLayout = new FlexLayout();
                                Button deleteButton = new Button("Delete");
                                Button ignoreButton = new Button("Ignore");
                                Button moveButton = new Button("Move");
                                deleteButton.setClassName("inside-grid-button");
                                ignoreButton.setClassName("inside-grid-button");

                                moveButton.setClassName("inside-grid-button");

                                Action action = new Action();
                                deleteButton.addClickListener(event -> {
                                    Notification.show("Delete: " + item.getPath());
                                    deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                    ignoreButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                    moveButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                    item.getAction().setType(ActionType.DELETE);

                                });
                                moveButton.addClickListener(event -> {
                                    Notification.show("Move: " + item.getPath());
                                    openFileSelect(item);
                                    deleteButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                    ignoreButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                    moveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                                });
                                ignoreButton.addClickListener(event -> {
                                    Notification.show("Ignore: " + item.getPath());
                                    deleteButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                    ignoreButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                    moveButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                    item.getAction().setType(ActionType.IGNORE);

                                });
                                item.setAction(action);
                                buttonLayout.add();
                                buttonLayout.add(deleteButton, ignoreButton, moveButton);
                                return buttonLayout;
                            })).setHeader("Manage").setFlexGrow(1);
                            insideGrids.add(insideGrid);

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    Iterator<MinimalDuplicate> duplicatesIterator = duplicates.iterator();

                    int duplicatesBufferSize = 0;
                    if (duplicates.size() > 10) {
                        duplicatesBufferSize = 10;
                    }

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
                        hLayout.setWidthFull();
                        VerticalLayout vLayout = new VerticalLayout();
                        vLayout.add(hLayout);
                        vLayout.add(grid);
                        vLayout.setWidthFull();

                        return vLayout;

                    }))).setHeader("Duplicates");
                    duplicatesGrid.setItems(insideGrids);
                    duplicatesGrid.setSizeFull();

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
        ObjectMapper objectMapper = encoder.getObjectMapper();
        if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                JSONObject[] array = Utils.getArray((JSONArray) parser.parse(response.getBody()));
                List<GlobalPath> paths = new ArrayList<GlobalPath>();

                for (JSONObject jsonObject : array) {
                    try {
                        paths.add(objectMapper.readValue(jsonObject.toJSONString(), GlobalPath.class));
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

    /**
     * Method that opens a dialog pop-up with a file browser.
     */
    private void openFileSelect(GlobalPath item) {
        FilesystemData rootData = new FilesystemData(File.listRoots()[0], false);
        FilesystemDataProvider fileSystem = new FilesystemDataProvider(rootData);
        Dialog dialog = new Dialog();
        TreeGrid<File> fileBrowser = new TreeGrid<>();
        fileBrowser.setItems(rootData.getChildren(File.listRoots()[0]));
        fileBrowser.setDataProvider(fileSystem);
        fileBrowser.addSelectionListener(event -> {
            Optional<File> selected = event.getFirstSelectedItem();
            if (selected.get().isDirectory() && selected.get().canWrite()) {
                Action action = item.getAction();
                action.setNewPath(selected.get().getAbsolutePath());
                action.setType(ActionType.MOVE);
                dialog.close();
            } else {
                Notification.show("Path selected is not a directory or it is not writeable",
                        Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        fileBrowser.addHierarchyColumn(File::getAbsolutePath).setHeader("Path");

        VerticalLayout layout = new VerticalLayout();
        layout.add(new Label("Select a folder"), fileBrowser);
        layout.setMinWidth("50em");
        layout.setAlignItems(Alignment.CENTER);
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.add(layout);
        dialog.open();

    }

}
