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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.shared.ui.Transport;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import samt.smajilbasic.configuration.ConfigProperties;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.authentication.AccessControlFactory;
import samt.smajilbasic.communication.Client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * DashboardView
 */
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle(value = "Deduplicator - Dashboard")
public class DashboardView extends FormLayout {

    public static final String VIEW_NAME = "Dashboard";
    private Client client;

    @Autowired
    private ConfigProperties props;

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
            Button clearLogsButton = new Button("Clear logs", event -> clearLogs());
            Button changeUsernameButton = new Button("Change username", event -> changeUsername());
            Button downloadLogsButton = new Button("Download logs");
//            Button changeMYSQLButton = new Button("Change MYSQL database credentials");
            Button changeLogFileLocationButton = new Button("Change log file location");

            FormLayout rightSide = new FormLayout(new Label("Refresh interval: "));
            leftSide.add(changePasswordButton, clearLogsButton, changeUsernameButton, downloadLogsButton, changeLogFileLocationButton);
            setMinWidth(Resources.SIZE_MOBILE_S);
            add(leftSide, rightSide);
        }
    }

    private void changeUsername() {
        dialog.removeAll();
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        PasswordField passwordField = new PasswordField("Password");
        TextField usernameField = new TextField("New username");

        passwordField.setRequired(true);
        usernameField.setRequired(true);

        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        usernameField.setValueChangeMode(ValueChangeMode.EAGER);

        ComponentEventListener<KeyUpEvent> listener = new ComponentEventListener<KeyUpEvent>() {

            @Override
            public void onComponentEvent(KeyUpEvent event) {
                if (usernameField.getValue().length() >= Resources.USERNAME_LENGTH) {
                    usernameField.setInvalid(false);
                    usernameField.setErrorMessage("");
                } else {
                    usernameField.setErrorMessage("Username needs be at least " + Resources.USERNAME_LENGTH + " characters long");
                    usernameField.setInvalid(true);
                }
            }
        };

        Button changeUsernameButton = new Button("Change password", event -> {
            if (passwordField.getValue().equals(client.getPassword())) {
                passwordField.setInvalid(false);
                if (!usernameField.isInvalid()) {
                    ResponseEntity<String> response = client.updateUsername(usernameField.getValue());
                    if (response != null) {
                        if (response.getStatusCode().equals(HttpStatus.OK)) {
                            Notification.show("Successfully updated the username ", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                            Logger.getGlobal().log(Level.INFO, "Successfully updated the username ");
                            logOutLoadingDialog();
                        } else {
                            Notification.show("Unable to update password - Error: " + response.getStatusCode(), Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                            Logger.getGlobal().log(Level.SEVERE, "Unable to update password - Error: " + response.getStatusCode() + "\nError body: " + response.getBody());

                        }
                    } else {
                        Notification.show("Unable to update username - Unable to get response", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        Logger.getGlobal().log(Level.SEVERE, "Unable to update username - Unable to get response");
                    }
                } else {
                    Notification.show("Username invalid", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    Logger.getGlobal().log(Level.WARNING, "Username invalid");
                }
            } else {
                passwordField.setErrorMessage("Password invalid");
                passwordField.setInvalid(true);
                Logger.getGlobal().log(Level.WARNING, "Password value is invalid");
            }
        });

        layout.add(usernameField, passwordField, changeUsernameButton);
        dialog.add(layout);

    }

    private void clearLogs() {
        String prop = props.getLogPath();
        System.out.println("fileLocation: " + prop);
        try {
            PrintWriter writer = new PrintWriter(prop);
            writer.print("");
            writer.close();
            Logger.getGlobal().log(Level.INFO, "Log file cleared");
            Notification.show("Log file cleared", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END);
        } catch (FileNotFoundException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Unable to clear file - file does not exist");
        }
    }


    private Dialog dialog = new Dialog();

    private void changePassword() {
        dialog.removeAll();
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        PasswordField passwordField = new PasswordField("Old password");
        PasswordField newPasswordField = new PasswordField("New password");
        PasswordField repeatedPasswordField = new PasswordField("Repeat new password");

        passwordField.setRequired(true);
        newPasswordField.setRequired(true);
        repeatedPasswordField.setRequired(true);

        newPasswordField.setMinLength(Resources.PASSWORD_LENGTH);

        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        newPasswordField.setValueChangeMode(ValueChangeMode.EAGER);
        repeatedPasswordField.setValueChangeMode(ValueChangeMode.EAGER);

        Button changePasswordButton = new Button("Change password", event -> {
            if (passwordField.getValue().equals(client.getPassword())) {
                if (!passwordField.isInvalid() && !newPasswordField.isInvalid() && !repeatedPasswordField.isInvalid()) {
                    if (newPasswordField.getValue().length() < Resources.PASSWORD_LENGTH) {
                        ResponseEntity<String> response = client.updatePassword(newPasswordField.getValue());

                        if (response != null) {
                            if (response.getStatusCode().equals(HttpStatus.OK)) {
                                Notification.show("Successfully updated the password ", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                Logger.getGlobal().log(Level.INFO, "Successfully updated the password ");
                                logOutLoadingDialog();
                            } else {
                                Notification.show("Unable to update password - Error: " + response.getStatusCode(), Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                                Logger.getGlobal().log(Level.SEVERE, "Unable to update password - Error: " + response.getStatusCode() + "\nError body: " + response.getBody());
                            }
                        } else {
                            Notification.show("Unable to update password - Unable to get response", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                            Logger.getGlobal().log(Level.SEVERE, "Unable to update password - Unable to get response");

                        }
                    } else {
                        Notification.show("Unable to update password - password length ", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        Logger.getGlobal().log(Level.SEVERE, "Unable to update password - Unable to get response");
                    }
                } else {
                    Notification.show("All fields must be valid", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    Logger.getGlobal().log(Level.SEVERE, "Unable to update password - All fields must be valid");

                }
            } else {
                passwordField.setErrorMessage("Old value invalid");
                passwordField.setInvalid(true);
                Logger.getGlobal().log(Level.WARNING, "Unable to update password - old password value is invalid");
            }
        });

        Button cancelButton = new Button("Cancel", event -> {
            dialog.close();
        });


        ComponentEventListener<KeyUpEvent> listener = new ComponentEventListener<KeyUpEvent>() {
            @Override
            public void onComponentEvent(KeyUpEvent event) {
                if (newPasswordField.getValue().equals(passwordField.getValue())) {
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

    public void logOutLoadingDialog() {
        ProgressBar bar = new ProgressBar(0, 5, 0);
        dialog.close();
        dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();
        layout.add(new Label("You are about to be logged out."), new Label("Login with new credentials"), bar);
        dialog.add(layout);

        dialog.open();
        UI ui = UI.getCurrent();
        ui.getPushConfiguration().setPushMode(PushMode.MANUAL);
        ui.getPushConfiguration().setTransport(Transport.WEBSOCKET);
        Command command = (Command) () -> {
            bar.setValue(bar.getValue() + 1);
            ui.push();
        };

        Command closeDialog = (Command) () -> {
            AccessControlFactory.getInstance().createAccessControl().signOut();
            dialog.close();
        };

        Thread countdown = new Thread(() -> {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 5; ) {
                if (System.currentTimeMillis() - start > 1000) {
                    ui.access(command);
                    start = System.currentTimeMillis();
                    i++;
                }
            }
            ui.access(closeDialog);
        });

        countdown.start();
    }
}