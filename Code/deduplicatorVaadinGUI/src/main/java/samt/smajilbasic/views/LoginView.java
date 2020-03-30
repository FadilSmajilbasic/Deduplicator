package samt.smajilbasic.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Component;
import samt.smajilbasic.logger.MyLogger;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.model.Validator;
import samt.smajilbasic.authentication.AccessControl;
import samt.smajilbasic.authentication.AccessControlFactory;
import samt.smajilbasic.communication.Client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Login view that the user uses to authenticate to the deduplicator
 * service.
 */
@Route(value = "login")
@PageTitle(value = "Deduplicator - Login")
@CssImport(value = "./styles/login-form-style.css")
public class LoginView extends VerticalLayout {

    private static final long serialVersionUID = 4944489863331319773L;

    @Autowired
    ApplicationContext context;

    /**
     * The HTTP/HTTPS client uset for the authentication.
     */
    private Client client;
    /**
     * Flag used to switch between the advanced and basic view.
     */
    private boolean defaultView = true;
    /**
     * The text field for the host IP.
     */
    private TextField hostTextField;
    /**
     * The text field for the host port.
     */
    private NumberField portTextField;
    /**
     * The button that switches between the views.
     */
    private Button advancedViewButton;

    private Upload certificateUpload;
    private MemoryBuffer buffer = new MemoryBuffer();

    private FormLayout form;

    AccessControl accessControl;

    /**
     * Base constructor of the LoginView class. It creates the LoginView GUI with
     * all of it's elements.
     */
    public LoginView() {
        setSizeFull();
        accessControl = AccessControlFactory.getInstance().createAccessControl();
        hostTextField = new TextField("Host");
        portTextField = new NumberField("Port");
        TextField usernameTextField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        usernameTextField.setRequired(true);
        passwordField.setRequired(true);
        hostTextField.setRequired(true);
        portTextField.setRequiredIndicatorVisible(true);

        advancedViewButton = new Button("Advanced View", event -> {
            toggleView();
        });
        advancedViewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        certificateUpload = new Upload(buffer);
        certificateUpload.addSucceededListener(event -> {
            Notification.show("File uploaded successfully", new Resources().getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            writeCertificate(buffer.getInputStream());
        });
        certificateUpload.setUploadButton(new Button("Upload pkcs12 certificate"));
        certificateUpload.setAcceptedFileTypes("application/x-pkcs12");

        portTextField.setStep(1);
        portTextField.setMin(1);
        portTextField.setMax(65535);
        portTextField.setValue(8443d);
        hostTextField.setValue("localhost");
        portTextField.setHasControls(true);
        usernameTextField.setValue("admin");
        passwordField.setValue("administrator");

        Button button = new Button("Login", e -> tryLogin(hostTextField.getValue(), portTextField.getValue().intValue(),
            usernameTextField.getValue(), passwordField.getValue()));

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        button.addClickShortcut(Key.ENTER);

        form = new FormLayout();
        passwordField.getStyle().set("margin-left", "0px");
        form.setResponsiveSteps(new ResponsiveStep("10em", 1), new ResponsiveStep("20em", 2));
        form.add(usernameTextField, passwordField);
        form.setColspan(certificateUpload,2);
        Div container = new Div(form);
        container.setWidth("50%");
        setAlignItems(Alignment.CENTER);

        add(container, button, advancedViewButton);
        setMinWidth(Resources.SIZE_MOBILE_S);
    }

    /**
     * The toggleView method switches between the advanced and regular login view.
     * By switching to the advanced view it exposes the host and port fields.
     */
    private void toggleView() {
        if (!defaultView) {
            form.remove(hostTextField, portTextField, certificateUpload);

        } else {
            form.add(hostTextField, portTextField, certificateUpload);
        }
        advancedViewButton.setText(defaultView ? "Advanced View" : "Basic View");
        defaultView = !defaultView;
    }

    /**
     * The tyLogin method attempts to authenticate the {@link Client} to the
     * deduplicator service.
     *
     * @param host the host IP to conenct to.
     * @param port the port to connect to.
     * @param user the username to try to authenticate with.
     * @param pass the user's password to try to authenticate with.
     */
    private void tryLogin(String host, int port, String user, String pass) {

        if (!user.isBlank()) {
            if (Validator.isValidIP(host) || host.equals("localhost")) {
                if (port > 0 && port <= 65535) {
                    client = (Client) context.getBean("connectionClient");
                    if(client.init(user, pass)) {
                        HttpStatus resp = client.isAuthenticated(host, port);

                        switch (resp) {
                            case OK:
                                Logger.getGlobal().log(Level.INFO, "User signed in successfully");
                                accessControl.signedIn(user, client);
                                UI.getCurrent().navigate("");
                                break;
                            case UNAUTHORIZED:
                                Notification.show("Invalid credentials", new Resources().getNotificationLength(), Notification.Position.TOP_END)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                Logger.getGlobal().log(Level.WARNING, "Invalid credentials");
                                break;
                            case SERVICE_UNAVAILABLE:
                                Notification
                                    .show("Server not reachable", new Resources().getNotificationLength(), Notification.Position.TOP_END)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                Logger.getGlobal().log(Level.SEVERE, "Server not reachable");
                                break;
                            case EXPECTATION_FAILED:
                                Notification.show("Host not registered as an alias in the certificate, try uploading a new certificate", new Resources().getNotificationLength(), Notification.Position.TOP_END)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                Logger.getGlobal().log(Level.SEVERE, "Host not registered as an alias in the certificate");
                                break;
                            default:
                                Notification.show("Unknown error occured", new Resources().getNotificationLength(), Notification.Position.TOP_END)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                Logger.getGlobal().log(Level.SEVERE, "Unknown error occured");
                                break;
                        }
                    }else{
                        Notification.show("Unable to initiate client, have you uploaded the certificate?", new Resources().getNotificationLength(), Notification.Position.TOP_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        Logger.getGlobal().log(Level.SEVERE, "Unable to initiate client no certificate found");
                    }
                } else {
                    Notification.show("Invalid port set", new Resources().getNotificationLength(), Notification.Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    Logger.getGlobal().log(Level.WARNING, "Invalid port set");

                }
            } else {
                Notification.show("Invalid IP set", new Resources().getNotificationLength(), Notification.Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                Logger.getGlobal().log(Level.WARNING, "Invalid IP set");

            }
        } else {
            Notification.show("Username can't be blank", new Resources().getNotificationLength(), Notification.Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            Logger.getGlobal().log(Level.WARNING, "Username can't be blank");

        }
    }

    private void writeCertificate(InputStream in) {
        try {
            if (!Files.exists(Paths.get("deduplicator.p12"))) {
                File newFile = new File("deduplicator.p12");
                if (newFile.createNewFile()) {
                    Notification.show("New file created", new Resources().getNotificationLength(), Notification.Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    Logger.getGlobal().log(Level.INFO, "New file created");
                } else {
                    Notification.show("File already exists", new Resources().getNotificationLength(), Notification.Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    Logger.getGlobal().log(Level.SEVERE, "File already exists");
                }
            }
            FileOutputStream fileOutputStream = new FileOutputStream("deduplicator.p12");
            fileOutputStream.write(in.readAllBytes());
            fileOutputStream.close();

        } catch (IOException ioe) {
            Notification.show("An exception occurred, unable to create file", new Resources().getNotificationLength(), Notification.Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            Logger.getGlobal().log(Level.SEVERE, "IO Exception: " + ioe.getMessage());

        }
    }
}