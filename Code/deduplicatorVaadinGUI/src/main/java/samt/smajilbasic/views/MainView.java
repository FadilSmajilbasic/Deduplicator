package samt.smajilbasic.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.navigator.View;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;

/**
 * The main view of the application that redirects to the login view.
 */
@Route(value = "")
@PWA(name = "Deduplicator GUI", shortName = "Deduplicator", description = "Deduplicator GUI to control the deduplicator service.", enableInstallPrompt = true)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout implements View {

        public MainView() {
                UI.getCurrent().getPage().setLocation("login/");
        }
}