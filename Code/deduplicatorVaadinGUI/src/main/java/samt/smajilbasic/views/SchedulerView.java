package samt.smajilbasic.views;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Locale;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.ui.CheckBox;

import com.vaadin.ui.CheckBoxGroup;
import samt.smajilbasic.Resources;
import samt.smajilbasic.communication.Client;

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
    private int weekNumber;
    private int monthNumber;

    public SchedulerView() {
        super();
        client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);

        if (client != null) {
            DatePicker datePicker = new DatePicker();
            TimePicker timePicker = new TimePicker();

            datePicker.setValue(LocalDate.now());
            timePicker.setValue(LocalTime.now());

            timePicker.setLocale(Locale.getDefault());
            datePicker.setLocale(Locale.getDefault());

            ValueChangeListener listener = new ValueChangeListener<ValueChangeEvent<?>>() {

                @Override
                public void valueChanged(ValueChangeEvent<?> event) {
                    if (datePicker.getValue().isBefore(LocalDate.now()) && !datePicker.getValue().isEqual(LocalDate.now()) ) {
                        Notification.show("Date can't be in the past", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        datePicker.setValue(LocalDate.now());
                    } else {
                        if (timePicker.getValue().isBefore(LocalTime.now()) && !timePicker.getValue().isAfter(LocalTime.now()) ) {
                            Notification.show("Time can't be in the past", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            timePicker.setValue(LocalTime.now());
                        }
                    }
                    System.out.println("Value changed");
                }
            };

            datePicker.addValueChangeListener(listener);
            timePicker.addValueChangeListener(listener);

            HorizontalLayout pickerLayout = new HorizontalLayout(datePicker, timePicker);

            RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<String>();
            radioButtonGroup.setLabel("Select repetition frequency");

            RadioButtonGroup<String> weekRadioButtonGroup = new RadioButtonGroup<String>();
            weekRadioButtonGroup.setVisible(false);


            String[] weekdayNames = DateFormatSymbols.getInstance().getWeekdays();
            weekRadioButtonGroup.setItems(weekdayNames);

            radioButtonGroup.setItems("One off", "Daily", "Weekly", "Monthly");
            radioButtonGroup.setValue("One off");


            radioButtonGroup.addValueChangeListener(event -> {
                        weekRadioButtonGroup.setVisible(false);
                        if (event != null) {
                            if (radioButtonGroup.getItemPosition(event.getValue()) == 2) {
                                weekRadioButtonGroup.setVisible(true);
                            } else {

                            }

                        }
                    }
            );


            weekRadioButtonGroup.addValueChangeListener(event -> {
                weekNumber = weekNumber & (1 << weekRadioButtonGroup.getItemPosition(event.getValue()));

                System.out.println("Week Number: " + weekNumber);
            });


            Button button = new Button("Set scan schedule", event -> {

//                client.insertSchedule(LocalDateTime.of(datePicker.getValue(), timePicker.getValue()),
//                        radioButtonGroup.getValue());
            });
            add(datePicker, timePicker,radioButtonGroup,weekRadioButtonGroup,button);
        }
    }
}
// * @param monthly parametro da passare se lo {@link Scheduler} deve essere
// * eseguito mensilmente.
// * @param weekly parametro da passare se lo {@link Scheduler} deve essere
// * eseguito settimanalmente.
// * @param repeated parmetro da passare se lo scheduler dovrà essere ripetuto,
// * valori: "true" o "false"
// * @param timeStart la data e ora dalla quale partirà l'esecuzione formato
// * timestamp -&gt; Long
// * @return lo scheduler inserito oppure messaggio d'errore
// */
// @PutMapping()
// public Object insert(@RequestParam String monthly, @RequestParam String
// weekly, @RequestParam String repeated,
// @RequestParam String timeStart) {
