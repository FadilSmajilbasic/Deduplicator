package samt.smajilbasic.views;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.Dial;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import samt.smajilbasic.entity.GlobalPath;
import samt.smajilbasic.entity.Scheduler;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.model.Utils;
import samt.smajilbasic.properties.Settings;

/**
 * SchedulerView
 */
@Route(value = "scheduler", layout = MainLayout.class)
@PageTitle(value = "Deduplicator - Scheduler")
@CssImport("./styles/radio-buttons-vertical.css")
public class SchedulerView extends VerticalLayout {

    /**
     * The name of the view
     */
    public static final String VIEW_NAME = "Scheduler";

    /**
     * The HTTP/HTTPS client used for the communication.
     */
    private Client client;
    private Integer weekNumber;
    private Integer monthNumber;
    private Settings settings = new Settings();
    private DatePicker datePicker = new DatePicker("Start date");
    private TimePicker timePicker = new TimePicker("Start time");

    /**
     * The encoder used to map the return values from the server to a GlobalPath
     * object.
     */
    private Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();

    public SchedulerView() {
        super();
        client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);

        if (client != null) {

            LocalDateTime ldt = LocalDateTime.now();
            ldt = ldt.plusMinutes(1);
            datePicker.setValue(ldt.toLocalDate());
            timePicker.setValue(ldt.toLocalTime());


            timePicker.setLocale(Locale.GERMAN);
            datePicker.setLocale(Locale.GERMAN);

            timePicker.setMin(LocalTime.now().toString());

            ValueChangeListener<ValueChangeEvent<?>> listener = new ValueChangeListener<ValueChangeEvent<?>>() {

                @Override
                public void valueChanged(ValueChangeEvent<?> event) {
                    if (!timePicker.getValue().toString().isBlank() && !datePicker.getValue().toString().isBlank()) {
                        if (!checkDateTimeValues(LocalDateTime.of(datePicker.getValue(), timePicker.getValue()))) {
                            Notification.show("Date and time can't be in the past", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                            datePicker.setValue(LocalDate.now());
                            timePicker.setValue(LocalTime.now());
                        }
                    } else {
                        Notification.show("Date and time can't be empty", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                }
            };

            datePicker.addValueChangeListener(listener);
            timePicker.addValueChangeListener(listener);

            FormLayout pickerLayout = new FormLayout(datePicker, timePicker);
            pickerLayout.setResponsiveSteps(new ResponsiveStep(Resources.SIZE_MOBILE_S, 1), new ResponsiveStep(Resources.SIZE_MOBILE_M, 2));
            RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<String>();
            radioButtonGroup.setLabel("Select repetition frequency");

            RadioButtonGroup<String> weekRadioButtonGroup = new RadioButtonGroup<String>();
            weekRadioButtonGroup.setVisible(false);

            String[] weekdayNames = DateFormatSymbols.getInstance().getWeekdays();
            weekdayNames = Arrays.copyOfRange(weekdayNames, 1, weekdayNames.length);
            weekRadioButtonGroup.setItems(weekdayNames);

            radioButtonGroup.setItems("One off", "Daily", "Weekly", "Monthly");
            radioButtonGroup.setValue("One off");

            DatePicker monthlyDatePicker = new DatePicker();

            monthlyDatePicker.setMin(LocalDate.now().withDayOfMonth(1));
            monthlyDatePicker.setMax(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
            monthlyDatePicker.setVisible(false);
            monthlyDatePicker.setLabel("Select day of month");
            monthlyDatePicker.setLocale(Locale.GERMAN);

            HorizontalLayout pickerSideLayouts = new HorizontalLayout();

            radioButtonGroup.addValueChangeListener(event -> {
                    weekRadioButtonGroup.setVisible(false);
                    monthlyDatePicker.setVisible(false);
                    monthNumber = null;
                    weekNumber = null;
                    if (event != null) {
                        switch (radioButtonGroup.getItemPosition(event.getValue())) {
                            case 2:
                                weekRadioButtonGroup.setVisible(true);
                                break;
                            case 3:
                                monthlyDatePicker.setVisible(true);
                                break;
                        }
                    }
                }
            );

            monthlyDatePicker.addValueChangeListener(event -> {
                monthNumber = (1 << (event.getValue().getDayOfMonth() - 1));
                System.out.println("Month Number: " + monthNumber);
            });

            weekRadioButtonGroup.addValueChangeListener(event -> {
                weekNumber = (1 << weekRadioButtonGroup.getItemPosition(event.getValue()));
            });

            Button button = new Button("Set scan schedule", event -> {
                if (checkDateTimeValues(LocalDateTime.of(datePicker.getValue(), timePicker.getValue()))) {
                    ResponseEntity<String> response = client.insertScheduledScan(LocalDateTime.of(datePicker.getValue(), timePicker.getValue()), (weekNumber != null ? weekNumber : "").toString(), (monthNumber != null ? monthNumber : "").toString(), radioButtonGroup.getValue());
                    if (response != null) {
                        if (response.getStatusCode().equals(HttpStatus.OK)) {
                            Logger.getGlobal().log(Level.INFO, "Schedule added successfully");
                            Notification.show("Schedule added successfully", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        } else {
                            Logger.getGlobal().log(Level.SEVERE, "Unable to add scheduler ");
                            Notification.show("Unable to add scheduler ", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    } else {
                        Logger.getGlobal().log(Level.SEVERE, "Unable to add scheduler ");
                        Notification.show("Unable to add scheduler ", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                } else {
                    Notification.show("Date and time can't be in the past", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    datePicker.setValue(LocalDate.now());
                    timePicker.setValue(LocalTime.now());
                }
            });

            FormLayout form = new FormLayout();
            form.setSizeFull();

            pickerSideLayouts.add(radioButtonGroup, weekRadioButtonGroup, monthlyDatePicker);
            Button deleteScheduleButton = new Button("Delete schedule", event -> deleteScheduler());
            add(pickerLayout, pickerSideLayouts, new HorizontalLayout(button, deleteScheduleButton));
            setMinWidth(Resources.SIZE_MOBILE_S);
        }
    }


    public void deleteScheduler() {
        Dialog dialog = new Dialog();
        ResponseEntity<String> response = client.get("scheduler/");
        JSONParser parser = new JSONParser();
        Grid<Scheduler> reportGrid = new Grid<Scheduler>();
        if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                JSONObject[] array = Utils.getArray((JSONArray) parser.parse(response.getBody()));
                List<Scheduler> schedulers = new ArrayList<>();
                for (JSONObject jsonObject : array) {
                    try {
                        schedulers.add(encoder.getObjectMapper().readValue(
                            jsonObject.toJSONString(), Scheduler.class));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                reportGrid.setItems(schedulers);
                reportGrid.setClassName("inside-grid");
                reportGrid.addColumn(Scheduler::getSchedulerId).setHeader("Id");
                reportGrid.addColumn(Scheduler::getMonthlyFormatted).setHeader("Monthly repetition");
                reportGrid.addColumn(Scheduler::getWeeklyFormatted).setHeader("Weekly repetition");
                reportGrid.addColumn(Scheduler::isRepeated).setHeader("Is repeated");
                reportGrid.addColumn(Scheduler::getTimeStartFormatted).setHeader("Date start");
                reportGrid.addColumn(Scheduler::getExecutionCounter).setHeader("Execution counter");
                reportGrid.addColumn(Scheduler::isScheduled).setHeader("Is already scheduled");
                reportGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
                reportGrid.asSingleSelect().addValueChangeListener(event -> {
                    if (event.getValue() != null) {
                        Dialog deleteDialog = new Dialog();
                        VerticalLayout vLayout = new VerticalLayout();
                        Label title = new Label("Are you sure you want to delete the selected scheduler");
                        Button deleteButton = new Button("Delete", e -> {
                            ResponseEntity<String> responseEntity = client.delete("scheduler/" + event.getValue().getSchedulerId(), null);
                            if (responseEntity != null) {
                                Logger.getGlobal().log(Level.SEVERE, "Successfully deleted scheduler : " + event.getValue().getSchedulerId());
                                Notification.show("Successfully deleted scheduler : " + event.getValue().getSchedulerId(), settings.getNotificationLength(),
                                    Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                deleteDialog.close();
                                dialog.close();
                            } else {
                                Logger.getGlobal().log(Level.SEVERE, "Unable to delete scheduler : " + event.getValue().getSchedulerId());
                                Notification.show("Unable to delete scheduler - unknown error", settings.getNotificationLength(),
                                    Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                                deleteDialog.close();
                            }

                        });

                        Button cancelButton = new Button("Cancel", cancelEvent -> {
                            deleteDialog.close();
                        });
                        vLayout.add(title, deleteButton, cancelButton);
                        deleteDialog.add(vLayout);
                        deleteDialog.open();
                    }

                });
                Button closeButton = new Button("Close", e -> dialog.close());
                VerticalLayout verticalLayout = new VerticalLayout(reportGrid, closeButton);
                verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
                verticalLayout.setMinWidth("50em");
                dialog.add(verticalLayout);
                dialog.open();

            } catch (ParseException pe) {
                Notification.show("Unable to retrieve paths: " + pe.getMessage(), settings.getNotificationLength(),
                    Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("Unable to retrieve paths", settings.getNotificationLength(), Notification.Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public boolean checkDateTimeValues(LocalDateTime input) {
        return !(input.isBefore(LocalDateTime.now()) && !input.isEqual(LocalDateTime.now()));
    }
}
