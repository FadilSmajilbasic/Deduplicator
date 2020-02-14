package samt.smajilbasic.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.navigator.View;

import samt.smajilbasic.communication.Client;
import samt.smajilbasic.communication.Client.loginResponse;

@Route(value = "login", registerAtStartup = true)
public class LoginView extends VerticalLayout implements View {

    private static final long serialVersionUID = 4944489863331319773L;

    private Client client;
    public final static String CLIENT_STRING = "client";

    public LoginView() {

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        HorizontalLayout credentialsLayout = new HorizontalLayout();

        TextField hostTextField = new TextField("Host");
        NumberField portTextField = new NumberField("Port");
        TextField usernameTextField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");

        portTextField.setStep(1);
        portTextField.setMin(1);
        portTextField.setMax(65535);
        portTextField.setValue(8443d);
        hostTextField.setValue("127.0.0.1");
        usernameTextField.setValue("admin");
        passwordField.setValue("admin");

        Button button = new Button("Login", e -> tryLogin(hostTextField.getValue(),
         portTextField.getValue().intValue(),
         usernameTextField.getValue(),
         passwordField.getValue()));

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        button.addClickShortcut(Key.ENTER);

        horizontalLayout.add(hostTextField, portTextField);
        credentialsLayout.add(usernameTextField, passwordField);
        portTextField.setWidthFull();

        this.setAlignItems(Alignment.CENTER);

        add(horizontalLayout, credentialsLayout, button);

    }

    public void tryLogin(String host, int port, String user, String pass) {

        client = new Client(user, pass);
        loginResponse resp = client.isAuthenticated(host, port);

        switch (resp) {
        case OK:
            UI.getCurrent().getSession().setAttribute(CLIENT_STRING, client);
            UI.getCurrent().navigate("path");
            break;
        case SERVER:
            Notification.show("Server not reachable", 2000, Notification.Position.TOP_END);
            break;
        case CREDENTIALS:
            Notification.show("Invalid credentials", 2000, Notification.Position.TOP_END);
            break;
        }
    }

}
