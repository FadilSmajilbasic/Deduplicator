package samt.smajilbasic.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

import org.springframework.lang.Nullable;
import samt.smajilbasic.entity.MinimalDuplicate;
import samt.smajilbasic.model.DuplicateGrid;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.entity.Action;
import samt.smajilbasic.entity.GlobalPath;
import samt.smajilbasic.service.DuplicateGridService;

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

    private Grid<DuplicateGrid> duplicatesGrid;

    private Select<String> reportSelect;
    private boolean forMainView = false;
    public ReportView(){
        this(false);
    }

    public ReportView(boolean forMainView) {
        this.forMainView = forMainView;
        client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);
        duplicatesGrid = new Grid<DuplicateGrid>();
        duplicatesGrid.setSizeFull();
        if (client != null) {
            this.setSizeFull();
            VerticalLayout verticalLayout = new VerticalLayout();
            if(!forMainView) {
                reportSelect = new Select<String>();
                reportSelect.setItems(getReports());

                reportSelect.addValueChangeListener(event -> {
                    updateGrid(event.getValue());
                });
                Button infoButton = new Button();
                infoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                infoButton.setIcon(VaadinIcon.INFO.create());

                infoButton.addClickListener(event -> {
                    getReportInfo();
                });
                Button applyButton = new Button("Apply selected changes", event -> {
                    checkActions();
                });
                applyButton.setMinWidth("");
                FormLayout formButtonsLayout = new FormLayout(reportSelect, applyButton, infoButton);
                formButtonsLayout.setResponsiveSteps(new ResponsiveStep(Resources.SIZE_MOBILE_S, 1), new ResponsiveStep(Resources.SIZE_MOBILE_M, 2));
                formButtonsLayout.setWidthFull();
                verticalLayout.add(formButtonsLayout);
                verticalLayout.setAlignItems(Alignment.START);

            }else{
                updateGrid(getLastReportId());
            }
            add(verticalLayout, duplicatesGrid);
            setMinWidth(Resources.SIZE_MOBILE_S);
        }
    }


    private void checkActions() {
        actions = new ArrayList<GlobalPath>();
        ListDataProvider<DuplicateGrid> insideGrids = (ListDataProvider<DuplicateGrid>) duplicatesGrid
            .getDataProvider();

        Iterator<DuplicateGrid> insideGridsIterator = insideGrids.getItems().iterator();

        while (insideGridsIterator.hasNext()) {
            DuplicateGrid insideGrid = insideGridsIterator.next();
            ListDataProvider<GlobalPath> provider = (ListDataProvider<GlobalPath>) insideGrid.getItem().getDataProvider();

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


    private void updateGrid(String reportSelectValue) {

        String reportId = forMainView?reportSelectValue:reportSelect.getValue().split(":")[0];
        if (reportId != null) {
            if (!reportId.isBlank()) {
                duplicateGridService = new DuplicateGridService(reportId, client);
                DataProvider<DuplicateGrid, Void> dataProvider = DataProvider.fromCallbacks(
                    query -> {
                        int offset = query.getOffset();
                        int limit = query.getLimit();
                        System.out.println("offset: " + offset + " and limit " + limit);
                        List<DuplicateGrid> duplicates = getDuplicateGridService().fetchInsideGrids(offset, limit);
                        return duplicates.stream();
                    }, query -> getDuplicateGridService().getTotalDuplicatesCount());
                duplicatesGrid.setDataProvider(dataProvider);


                duplicatesGrid.addColumn(new ComponentRenderer<VerticalLayout, DuplicateGrid>((grid -> {
                    MinimalDuplicate dup = grid.getMinimalDuplicate();
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
                    vLayout.add(grid.getItem());
                    vLayout.setWidthFull();
                    return vLayout;
                }))).setHeader("Duplicates");

                duplicatesGrid.setSizeFull();
            } else {
                Notification.show("Unable to retrieve reports", Resources.NOTIFICATION_LENGTH, Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

        } else {
            System.out.println("Empty value in select");
        }

    }

    private DuplicateGridService duplicateGridService;

    private DuplicateGridService getDuplicateGridService() {
        return duplicateGridService;
    }


    private void getReportInfo() {
        Dialog dialog = new Dialog();
        VerticalLayout verticalLayout = new VerticalLayout();

        Label durationLabel = new Label();
        Label dateStartLabel = new Label();

        Label filesScannedLabel = new Label();

        Label averageDuplicateCountLabel = new Label();

        Label userLabel = new Label();

        Label idLabel = new Label();

        verticalLayout.add(durationLabel, dateStartLabel, filesScannedLabel, averageDuplicateCountLabel, userLabel, idLabel);

        dialog.add(verticalLayout);
        dialog.open();
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

    private String getLastReportId() {
        ResponseEntity<String> response = client.get("report/");

        if (response != null) {
            try {
                JSONObject report = (JSONObject)parser.parse(response.getBody());

                return String.valueOf(report.get("id"));

            } catch (ParseException pe) {
                Notification.show("Unable to get last report id", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return null;
            }
        }else {
            Notification.show("Unable to get last report", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return null;
        }
    }


}
