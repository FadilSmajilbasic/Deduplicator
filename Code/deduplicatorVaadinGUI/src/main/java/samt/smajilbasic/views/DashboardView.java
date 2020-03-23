package samt.smajilbasic.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.shared.ui.Transport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.authentication.AccessControlFactory;
import samt.smajilbasic.communication.Client;


/**
 * DashboardView
 */
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle(value = "Deduplicator - Dashboard")
public class DashboardView extends FormLayout {

    public static final String VIEW_NAME = "Dashboard";
    private Client client;

    public DashboardView() {
        client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);

        if (client != null) {
            setResponsiveSteps(new ResponsiveStep(Resources.SIZE_MOBILE_S, 1), new ResponsiveStep(Resources.SIZE_MOBILE_M, 2));
            VerticalLayout leftSide = new VerticalLayout(new Label("User: " + AccessControlFactory.getInstance().createAccessControl().getName()));
            FormLayout leftSideFormLayout = new FormLayout();
            leftSideFormLayout.setResponsiveSteps(new ResponsiveStep(Resources.SIZE_MOBILE_S, 1), new ResponsiveStep(Resources.SIZE_MOBILE_M, 2));
            Button changePasswordButton = new Button("Change password", event -> {
                changePassword();
            });
            Button clearLogsButton = new Button("Clear logs");
            Button changeUsernameButton = new Button("Change username");
            Button downloadLogsButton = new Button("Download logs");
            Button changeMYSQLButton = new Button("Change MYSQL database credentials");
            Button changeLogFileLocationButton = new Button("Change log file location");

            FormLayout rightSide = new FormLayout(new Label("Refresh interval: "));
            leftSide.add(changePasswordButton, clearLogsButton, changeUsernameButton, downloadLogsButton, changeMYSQLButton, changeLogFileLocationButton);
            setMinWidth(Resources.SIZE_MOBILE_S);
            add(leftSide, rightSide);
        }
    }

    private Dialog dialog = new Dialog();

    private void changePassword() {
        dialog.removeAll();
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        PasswordField passwordField = new PasswordField("Old password");
        PasswordField newPasswordField = new PasswordField("New password");
        PasswordField repeatedPasswordField = new PasswordField("Repeat new password");

        passwordField.setRequired(true);
        newPasswordField.setRequired(true);
        repeatedPasswordField.setRequired(true);

        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        newPasswordField.setValueChangeMode(ValueChangeMode.EAGER);
        repeatedPasswordField.setValueChangeMode(ValueChangeMode.EAGER);

        Button changePasswordButton = new Button("Change password", event -> {
            if (passwordField.getValue().equals(client.getPassword())) {
                if (!passwordField.isInvalid() && !newPasswordField.isInvalid() && !repeatedPasswordField.isInvalid()) {

                    ResponseEntity<String> response = client.updatePassword(newPasswordField.getValue());

                    if (response != null) {
                        if (response.getStatusCode().equals(HttpStatus.OK)) {
                            Notification.show("Successfully updated the password ", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                            ProgressBar bar = new ProgressBar(0, 5, 0);
                            dialog.close();
                            layout.removeAll();
                            dialog = new Dialog();

                            layout.add(new Label("You are about to be logged out."), new Label("Login with new credentials"), bar);
                            dialog.add(layout);

                            dialog.open();
                            UI ui = UI.getCurrent();
                            ui.getPushConfiguration().setPushMode(PushMode.MANUAL);
                            ui.getPushConfiguration().setTransport(Transport.WEBSOCKET);
                            Command command = (Command) () ->{
                                bar.setValue(bar.getValue() + 1);
                                ui.push();
                            };

                            Command closeDialog = (Command) () -> {
                                System.out.println("close command");
                                AccessControlFactory.getInstance().createAccessControl().signOut();
                                dialog.close();
                            };

                            Thread countdown = new Thread(() -> {
                                long start = System.currentTimeMillis();
                                for (int i = 0; i < 5;) {
                                    if (System.currentTimeMillis() - start > 1000) {
                                        System.out.println("Update");
                                        ui.access(command);
                                        start = System.currentTimeMillis();
                                        i++;
                                    }
                                }
                                ui.access(closeDialog);
                            });

                            countdown.start();

                        } else {
                            System.err.println("Error: " + response.getStatusCode());
                            System.err.println("Error body: " + response.getBody());
                            Notification.show("Unable to update password - Unknown error", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    } else {
                        Notification.show("Unable to update password - Unknown error", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                } else {
                    Notification.show("All fields must be valid", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                passwordField.setErrorMessage("Old value invalid");
                passwordField.setInvalid(true);
            }
        });

        Button cancelButton = new Button("Cancel", event -> {
            dialog.close();
        });


        ComponentEventListener<KeyUpEvent> listener = new ComponentEventListener<KeyUpEvent>() {
            @Override
            public void onComponentEvent(KeyUpEvent event) {
                if (newPasswordField.getValue().equals(passwordField.getValue())) {
                    System.out.println("new: " + newPasswordField.getValue() + " old: " + passwordField.getValue());
                    if (newPasswordField.getErrorMessage() != null) {
                        newPasswordField.setInvalid(true);
                        newPasswordField.setErrorMessage("New password is same as old password");
                    }
                } else {
                    newPasswordField.setInvalid(false);
                    newPasswordField.setErrorMessage("");
                }

                if (!newPasswordField.getValue().isBlank()) {
                    if (newPasswordField.getValue().length() >= Resources.PASSWORD_LENGTH) {
                        if (!newPasswordField.getValue().equals(repeatedPasswordField.getValue())) {
                            if (repeatedPasswordField.getErrorMessage() != null) {
                                repeatedPasswordField.setInvalid(true);
                                repeatedPasswordField.setErrorMessage("Passwords don't match");
                            }
                        } else {
                            repeatedPasswordField.setInvalid(false);
                            repeatedPasswordField.setErrorMessage("");
                        }
                    } else {
                        if (newPasswordField.getErrorMessage() != null) {
                            newPasswordField.setInvalid(true);
                            newPasswordField.setErrorMessage("Password should be at least " + Resources.PASSWORD_LENGTH + " characters long");
                        }
                    }
                }


            }

        };
        passwordField.addKeyUpListener(listener);
        newPasswordField.addKeyUpListener(listener);
        repeatedPasswordField.addKeyUpListener(listener);

        layout.add(passwordField, newPasswordField, repeatedPasswordField, new HorizontalLayout(cancelButton, changePasswordButton));
        dialog.add(layout);
        dialog.open();

    }
}