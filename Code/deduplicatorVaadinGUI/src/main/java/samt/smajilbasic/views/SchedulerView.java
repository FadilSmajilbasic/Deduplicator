package samt.smajilbasic.views;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.properties.Settings;

/**
 * SchedulerView
 */
@Route(value = "scheduler", layout = MainLayout.class)
@PageTitle(value = "Deduplicator - Scheduler")
@CssImport("./styles/radio-buttons-vertical.css")
public class SchedulerView extends VerticalLayout {

    public static final String VIEW_NAME = "Scheduler";

    /**
     * The HTTP/HTTPS client used for the communication.
     */
    private Client client;
    private int weekNumber = 0;
    private int monthNumber = 0;
    private Settings settings = new Settings();

    public SchedulerView() {
        super();
        client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);

        if (client != null) {
            DatePicker datePicker = new DatePicker("Start date");
            TimePicker timePicker = new TimePicker("Start time");

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
                        LocalDateTime inputs = LocalDateTime.of(datePicker.getValue(), timePicker.getValue());
                        if (inputs.isBefore(LocalDateTime.now()) && !inputs.isEqual(LocalDateTime.now())) {
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

                ResponseEntity<String> response = client.insertScheduledScan(LocalDateTime.of(datePicker.getValue(), timePicker.getValue()), weekNumber, monthNumber, radioButtonGroup.getValue());
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
            });
            pickerSideLayouts.add(radioButtonGroup, weekRadioButtonGroup, monthlyDatePicker);

            add(pickerLayout, pickerSideLayouts, button);
            setMinWidth(Resources.SIZE_MOBILE_S);
        }
    }
}
