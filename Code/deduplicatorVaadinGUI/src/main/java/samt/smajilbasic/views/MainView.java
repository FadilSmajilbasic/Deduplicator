package samt.smajilbasic.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.navigator.View;

import samt.smajilbasic.communication.Client;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.menubar.MenuBar;

/**
 * MainView
 */
@Route(value = "")
@PWA(name = "Deduplicator GUI", shortName = "Deduplicator", description = "Deduplicator GUI to control the deduplicator service.", enableInstallPrompt = true)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout implements View{

        private MenuBar menuBar;
        private MenuItem path;
        private MenuItem login;

        public MainView() {
                menuBar = new MenuBar();
                login = menuBar.addItem("Login", e -> login.getUI().ifPresent(ui -> ui.navigate("login")));
                path = menuBar.addItem("Path", e -> path.getUI().ifPresent(ui -> ui.navigate("path")));
                menuBar.setSizeFull();
                add(menuBar);
        }
}