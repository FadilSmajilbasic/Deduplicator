package samt.smajilbasic.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.navigator.View;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import samt.smajilbasic.GlobalPath;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.listener.LoginListener;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and use @Route
 * annotation to announce it in a URL as a Spring managed bean. Use the @PWA
 * annotation make the application installable on phones, tablets and some
 * desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every browser
 * tab/window.
 */
@Route
@PWA(name = "Deduplicator GUI", shortName = "Deduplicator", description = "Deduplicator GUI to control thje deduplicator service.", enableInstallPrompt = true)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class LoginView extends VerticalLayout implements View {

    /**
     *
     */
    private static final long serialVersionUID = 4944489863331319773L;

    private Client client;
    LoginListener listener = null;

    /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param service The message service. Automatically injected Spring managed
     *                bean.
     */
    public LoginView(LoginListener listener) {
        this.listener = listener;
        // Use TextField for standard text input

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
        // Button click listeners can be defined as lambda expressions

        Button button = new Button("Login", e -> tryLogin(hostTextField.getValue(), portTextField.getValue().intValue(),
                usernameTextField.getValue(), passwordField.getValue()));

        

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        button.addClickShortcut(Key.ENTER);
        
       

        horizontalLayout.add(hostTextField, portTextField);
        credentialsLayout.add(usernameTextField, passwordField);
        portTextField.setWidthFull();
        horizontalLayout.setAlignItems(Alignment.CENTER);
        credentialsLayout.setAlignItems(Alignment.CENTER);
        add(horizontalLayout,credentialsLayout , button);
        
       
    }

    public void tryLogin(String host, int port, String user, String pass) {

        client = new Client(user, pass);
        if (client.isAuthenticated(host, port)) {
            listener.userConnected(client);
            Notification.show("User authenticated sucessfully");
        } else {
            Notification.show("Invalid credentials");
        }
    }

     

}
