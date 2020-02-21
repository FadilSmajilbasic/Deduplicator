package samt.smajilbasic.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.navigator.View;

import samt.smajilbasic.communication.Client;

/**
 * The BaseView class describes the base properties and methods that every view
 * has.
 */
public class BaseView extends VerticalLayout implements View {

    /**
     * Defines the default notification length in ms.
     */
    public final static int NOTIFICATION_LENGTH = 2000;

    private final MenuBar menuBar = new MenuBar();

    /**
     * The BaseView constructor that adds a menu on the top of every page
     */
    public BaseView() {
        menuBar.addItem("Path", e -> {
            UI.getCurrent().getPage().setLocation("path/");
        });
        menuBar.addItem("Scan", e -> {
            UI.getCurrent().getPage().setLocation("scan/");
        });
        menuBar.addItem("Scheduler", e -> {
            UI.getCurrent().getPage().setLocation("scheduler/");
        });
        menuBar.addItem("Reports", e -> {
            UI.getCurrent().getPage().setLocation("reports/");
        });
        menuBar.addItem("Dashboard", e -> {
            UI.getCurrent().getPage().setLocation("dashboard/");
        });
        menuBar.addItem("Logout", e -> {
            Client client = (Client) UI.getCurrent().getSession().getAttribute(LoginView.CLIENT_STRING);
            client = null;
            UI.getCurrent().getPage().setLocation("login/");
        });
        VerticalLayout menuLayout = new VerticalLayout(menuBar);
        menuLayout.setWidthFull();
        menuLayout.setAlignItems(Alignment.CENTER);
        add(menuLayout);
    }
}