package samt.smajilbasic.views;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Calendar;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.ui.CheckBox;

import samt.smajilbasic.Resources;
import samt.smajilbasic.communication.Client;

/**
 * SchedulerView
 */
@Route(value = "scheduler", layout = MainLayout.class)
@PageTitle(value = "Deduplicator - Scheduler")
public class SchedulerView extends VerticalLayout {

    public static final String VIEW_NAME = "Scheduler";

    /**
     * The HTTP/HTTPS client used for the communication.
     */
    private Client client;

    public SchedulerView() {
        super();
        client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);

        if (client != null) {
            Calendar calendar = Calendar.getInstance();
            DatePicker datePicker = new DatePicker(LocalDate.now());
            TimePicker timePicker = new TimePicker(LocalTime.now());

            ValueChangeListener listener = new ValueChangeListener<ValueChangeEvent<?>>() {

                @Override
                public void valueChanged(ValueChangeEvent<?> event) {
                    if (datePicker.getValue().isBefore(LocalDate.now())) {
                        Notification.show("Date can't be in the past").addThemeVariants(NotificationVariant.LUMO_ERROR);
                        datePicker.setValue(LocalDate.now());
                    } else {
                        if (timePicker.getValue().isBefore(LocalTime.now())) {
                            Notification.show("Time can't be in the past")
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            timePicker.setValue(LocalTime.now());
                        }
                    }
                }
            };

            datePicker.addValueChangeListener(listener);
            timePicker.addValueChangeListener(listener);

            RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<String>();

            radioButtonGroup.setItems("One off", "Daily", "Weekly", "Monthly");

            Button button = new Button("Set scan schedule", event -> {
                client.insertSchedule(LocalDateTime.of(datePicker.getValue(), timePicker.getValue()),
                        radioButtonGroup.getValue());
            });

            add(datePicker, radioButtonGroup, button);
        }
    }
}

// * @param monthly parametro da passare se lo {@link Scheduler} deve essere
// * eseguito mensilmente.
// * @param weekly parametro da passare se lo {@link Scheduler} deve essere
// * eseguito settimanalmente.
// * @param repeated parametro da passare se lo scheduler dovrà essere ripetuto,
// * valori: "true" o "false"
// * @param timeStart la data e ora dalla quale partirà l'esecuzione formato
// * timestamp -&gt; Long
// * @return lo scheduler inserito oppure messaggio d'errore
// */
// @PutMapping()
// public Object insert(@RequestParam String monthly, @RequestParam String
// weekly, @RequestParam String repeated,
// @RequestParam String timeStart) {
