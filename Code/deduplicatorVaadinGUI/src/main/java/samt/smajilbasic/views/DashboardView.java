package samt.smajilbasic.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.shared.ui.Transport;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.vaadin.filesystemdataprovider.FilesystemData;
import org.vaadin.filesystemdataprovider.FilesystemDataProvider;
import org.vaadin.olli.FileDownloadWrapper;
import samt.smajilbasic.logger.MyLogger;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.authentication.AccessControlFactory;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.properties.Settings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Handler;
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

    /**
     * The application context
     */
    @Autowired
    ApplicationContext context;
    /**
     * Defines the default root path for the fileBrowser.
     */
    private File root = new File(String.valueOf(File.listRoots()[0]));

    private String newLogPath;

    private Dialog dialog = new Dialog();

    private Settings settings = new Settings();

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
            Button addUser = new Button("Add user", event -> addUser());

            if (client.getUsername().equalsIgnoreCase("admin")) {
                changeUsernameButton.setEnabled(false);
            }

            File logPath = new File(settings.getLogPath());
            File[] files = logPath.listFiles();
            Arrays.sort(files);
            List<File> fileList = new ArrayList<File>(Arrays.asList(files));
            fileList.removeIf(file -> (!(file.getName().startsWith("log.") && file.getName().endsWith(".txt"))));

            FileDownloadWrapper buttonWrapper = null;
            if (fileList.size() == 0) {
                Logger.getGlobal().log(Level.SEVERE, "No log files found");
                Notification.show("No log files found", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                clearLogsButton.setEnabled(false);
                downloadLogsButton.setEnabled(false);
            } else {
                buttonWrapper = new FileDownloadWrapper(fileList.get(fileList.size() - 1).getName(), new File(String.valueOf(fileList.get(fileList.size() - 1))));
                buttonWrapper.wrapComponent(downloadLogsButton);
                clearLogsButton.setEnabled(true);
                downloadLogsButton.setEnabled(true);
            }

            Button changeLogFileLocationButton = new Button("Change log file location", event -> openFileSelect());

            FormLayout rightSide = new FormLayout();

            NumberField refreshIntervalField = new NumberField("Refresh interval in ms:");

            refreshIntervalField.setStep(100d);
            refreshIntervalField.setMin(0d);
            refreshIntervalField.setMax(10000d);
            refreshIntervalField.setHasControls(true);

            refreshIntervalField.addValueChangeListener(event ->
            {
                if (event.getValue() > 0) {
                    settings.setRefreshInterval(event.getValue().intValue());
                }
            });


            NumberField notificationLength = new NumberField("Notification duration in  ms:");

            notificationLength.setStep(100d);
            notificationLength.setMin(0d);
            notificationLength.setMax(5000d);
            notificationLength.setHasControls(true);

            notificationLength.addValueChangeListener(event ->
            {
                if (event.getValue() > 0) {
                    settings.setNotificationLength(event.getValue().intValue());
                }
            });


            try {
                int refreshIntProp = settings.getRefreshInterval();
                refreshIntervalField.setValue((double) refreshIntProp);
            } catch (NumberFormatException nfe) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to read refresh interval property value");
                Notification.show("Unable to read refresh interval property value", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                refreshIntervalField.setValue((double) Resources.REFRESH_INTERVAL);
            }

            try {
                int notificationIntProp = settings.getNotificationLength();
                notificationLength.setValue((double) notificationIntProp);
            } catch (NumberFormatException nfe) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to read notification length property value");
                Notification.show("Unable to read notification length property value", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                refreshIntervalField.setValue((double) Resources.NOTIFICATION_LENGTH);
            }

            rightSide.add(refreshIntervalField, notificationLength);
            leftSide.add(changePasswordButton, clearLogsButton, changeUsernameButton, addUser);

            if (buttonWrapper != null)
                leftSide.add(buttonWrapper);
            leftSide.add(changeLogFileLocationButton);
            setMinWidth(Resources.SIZE_MOBILE_S);
            add(leftSide, rightSide);
        }
    }

    private void addUser() {
        dialog.removeAll();
        dialog.setCloseOnOutsideClick(true);
        dialog.setCloseOnEsc(true);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        TextField usernameField = new TextField("Username");
        PasswordField newPasswordField = new PasswordField("Password");
        PasswordField repeatedPasswordField = new PasswordField("Repeat password");

        newPasswordField.setRequired(true);
        usernameField.setRequired(true);
        repeatedPasswordField.setRequired(true);

        repeatedPasswordField.setValueChangeMode(ValueChangeMode.EAGER);
        newPasswordField.setValueChangeMode(ValueChangeMode.EAGER);
        usernameField.setValueChangeMode(ValueChangeMode.EAGER);

        Button addUserButton = new Button("Add user ", event -> {

            if (!usernameField.isInvalid()) {
                if (!newPasswordField.isInvalid()) {
                    if (!repeatedPasswordField.isInvalid()) {
                        ResponseEntity<String> response = client.addUser(usernameField.getValue(), newPasswordField.getValue());
                        if (response != null) {
                            if (response.getStatusCode().equals(HttpStatus.OK)) {
                                Notification.show("Successfully added user ", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                Logger.getGlobal().log(Level.INFO, "Successfully updated the username ");
                                dialog.close();
                            } else {
                                Notification.show("Unable to add user - Error: " + response.getStatusCode(), settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                                Logger.getGlobal().log(Level.SEVERE, "Unable to update password - Error: " + response.getStatusCode() + "\nError body: " + response.getBody());
                            }
                        } else {
                            Notification.show("Unable to add user - Unable to get response", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                            Logger.getGlobal().log(Level.SEVERE, "Unable to update username - Unable to get response");
                        }
                    }
                }
            }
        });

        ComponentEventListener<KeyUpEvent> listener = (ComponentEventListener<KeyUpEvent>) event -> {
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

            if (usernameField.getValue().length() >= Resources.USERNAME_LENGTH) {
                usernameField.setInvalid(false);
                usernameField.setErrorMessage("");
            } else {
                usernameField.setErrorMessage("Username needs be at least " + Resources.USERNAME_LENGTH + " characters long");
                usernameField.setInvalid(true);
            }

        };
        usernameField.addKeyUpListener(listener);
        newPasswordField.addKeyUpListener(listener);
        repeatedPasswordField.addKeyUpListener(listener);
        Button cancelButton = new Button("Cancel", event -> {
            dialog.close();
        });


        layout.add(usernameField, newPasswordField, repeatedPasswordField, new HorizontalLayout(cancelButton, addUserButton));
        dialog.add(layout);
        dialog.open();

    }

    private void changeUsername() {
        dialog.removeAll();
        dialog.setCloseOnOutsideClick(true);
        dialog.setCloseOnEsc(true);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        PasswordField passwordField = new PasswordField("Password");
        TextField usernameField = new TextField("New username");

        passwordField.setRequired(true);
        usernameField.setRequired(true);

        usernameField.setValueChangeMode(ValueChangeMode.EAGER);

        usernameField.addKeyUpListener((ComponentEventListener<KeyUpEvent>) event -> {
            if (usernameField.getValue().length() >= Resources.USERNAME_LENGTH) {
                usernameField.setInvalid(false);
                usernameField.setErrorMessage("");
            } else {
                usernameField.setErrorMessage("Username needs be at least " + Resources.USERNAME_LENGTH + " characters long");
                usernameField.setInvalid(true);
            }
        });

        Button changeUsernameButton = new Button("Change username", event -> {
            if (passwordField.getValue().equals(client.getPassword())) {
                passwordField.setInvalid(false);
                if (!usernameField.isInvalid()) {
                    ResponseEntity<String> response = client.updateUsername(usernameField.getValue());
                    if (response != null) {
                        if (response.getStatusCode().equals(HttpStatus.OK)) {
                            Notification.show("Successfully updated the username ", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                            Logger.getGlobal().log(Level.INFO, "Successfully updated the username ");
                            logOutLoadingDialog();
                        } else {
                            try {
                                JSONParser parser = new JSONParser();
                                JSONObject resp = (JSONObject) parser.parse(response.getBody().split(" : ")[1].replace("[", "").replace("]", ""));
                                Notification.show("Unable to update username - " + resp.get("message"), settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                                Logger.getGlobal().log(Level.SEVERE, "Unable to update password - " + resp.get("message"));
                            } catch (ParseException pe) {
                                Notification.show("Unable to update username - Error: " + response.getStatusCode() + " Message: " + response.getBody(), settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                                Logger.getGlobal().log(Level.SEVERE, "Unable to update password - Error: " + response.getStatusCode() + "\nError body: " + response.getBody());

                            }
                        }
                    } else {
                        Notification.show("Unable to update username - Unable to get response", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        Logger.getGlobal().log(Level.SEVERE, "Unable to update username - Unable to get response");
                    }
                } else {
                    Notification.show("Username invalid", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    Logger.getGlobal().log(Level.WARNING, "Username invalid");
                }
            } else {
                passwordField.setErrorMessage("Password invalid");
                passwordField.setInvalid(true);
                Logger.getGlobal().log(Level.WARNING, "Password value is invalid");
            }
        });

        Button cancel = new Button("Cancel", event -> dialog.close());

        layout.add(usernameField, passwordField, new HorizontalLayout(changeUsernameButton, cancel));
        dialog.add(layout);
        dialog.open();
    }

    private void clearLogs() {
        File logPath = new File(settings.getLogPath());
        File[] files = logPath.listFiles();
        Arrays.sort(files);
        List<File> fileList = new ArrayList<File>(Arrays.asList(files));
        fileList.removeIf(file -> (!(file.getName().startsWith("log.") && file.getName().endsWith(".txt"))));
        try {
            if (fileList != null) {
                if (fileList.size() > 0) {
                    FileWriter writer = new FileWriter(fileList.get(fileList.size() - 1), StandardCharsets.UTF_8, false);
                    writer.write("");
                    writer.flush();
                    writer.close();
                    Logger.getGlobal().log(Level.INFO, "Log file cleared");
                    Notification.show("Log file cleared", settings.getNotificationLength(), Notification.Position.TOP_END);
                } else {
                    Logger.getGlobal().log(Level.SEVERE, "Unable to clear file - file does not exist");
                    Notification.show("Unable to clear file - file does not exist", settings.getNotificationLength(), Notification.Position.TOP_END);
                }
            } else {
                Logger.getGlobal().log(Level.SEVERE, "Unable to clear file - log path invalid");
                Notification.show("Unable to clear file - log path invalid", settings.getNotificationLength(), Notification.Position.TOP_END);
            }
        } catch (FileNotFoundException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Unable to clear file - file does not exist");
            Notification.show("Unable to clear file - file does not exist", settings.getNotificationLength(), Notification.Position.TOP_END);

        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Unable to clear file - IO error while writing");
            Notification.show("Unable to clear file - IO error while writing", settings.getNotificationLength(), Notification.Position.TOP_END);

        }
    }


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
                    if (newPasswordField.getValue().length() >= Resources.PASSWORD_LENGTH) {
                        ResponseEntity<String> response = client.updatePassword(newPasswordField.getValue());

                        if (response != null) {
                            if (response.getStatusCode().equals(HttpStatus.OK)) {
                                Notification.show("Successfully updated the password ", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                Logger.getGlobal().log(Level.INFO, "Successfully updated the password ");
                                logOutLoadingDialog();
                            } else {
                                Notification.show("Unable to update password - Error: " + response.getStatusCode(), settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                                Logger.getGlobal().log(Level.SEVERE, "Unable to update password - Error: " + response.getStatusCode() + "\nError body: " + response.getBody());
                            }
                        } else {
                            Notification.show("Unable to update password - Unable to get response", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                            Logger.getGlobal().log(Level.SEVERE, "Unable to update password - Unable to get response");

                        }
                    } else {
                        Notification.show("Unable to update password - new password is not long enough ", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        Logger.getGlobal().log(Level.SEVERE, "Unable to update password - new password is not long enough");
                    }
                } else {
                    Notification.show("All fields must be valid", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
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


        ComponentEventListener<KeyUpEvent> listener = (ComponentEventListener<KeyUpEvent>) event -> {
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


    private void openFileSelect() {

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FilesystemData rootData = new FilesystemData(root, false);
        FilesystemDataProvider fileSystem = new FilesystemDataProvider(rootData);

        TreeGrid<File> fileBrowser = new TreeGrid<>();
        fileBrowser.setItems(rootData.getChildren(root));
        fileBrowser.setDataProvider(fileSystem);
        fileBrowser.addSelectionListener(event -> {
            Optional<File> selected = event.getFirstSelectedItem();
            selected.ifPresent(file -> {
                if (file.isDirectory()) {
                    newLogPath = file.getAbsolutePath();
                } else {
                    Notification.show("Path selected is not a directory");
                }
            });

        });

        fileBrowser.addHierarchyColumn(File::getName).setHeader("Path");
        fileBrowser.setSelectionMode(Grid.SelectionMode.SINGLE);
        Button confirmButton = new Button("Close", button -> {
            Notification.show("New log path set as " + newLogPath, settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            File file = fileBrowser.getSelectedItems().iterator().next();

            if (file.isDirectory() && Files.isWritable(Paths.get(file.getPath()))) {
                newLogPath = file.getAbsolutePath();
                updateLogger();
                dialog.close();
            } else {
                Notification.show("Path selected is not a directory");
            }
        });

        VerticalLayout layout = new VerticalLayout();
        layout.add(new Label("Select file or folder"), fileBrowser, confirmButton);
        layout.setMinWidth("50em");
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.setCloseOnOutsideClick(false);
        dialog.add(layout);
        dialog.open();
    }

    private void updateLogger() {

        for (Handler handler : Logger.getGlobal().getHandlers()) {
            handler.close();
        }
        settings.setLogPath(newLogPath);
        MyLogger logger = new MyLogger();
        try {
            logger.setup();
        } catch (IOException ioe) {
            Notification.show("New log path set as " + newLogPath, settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);

        }
    }
}