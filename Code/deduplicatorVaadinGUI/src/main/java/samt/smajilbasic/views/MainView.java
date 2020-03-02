package samt.smajilbasic.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * MainView
 */
@Route(value = "", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@PageTitle(value = "Deduplicator - Home")
public class MainView extends VerticalLayout{

    public static final String VIEW_NAME = "Home";

	public MainView() {
        super();
        this.setAlignItems(Alignment.CENTER);
        add(new Label("Welcome to Main View"),new Label("Select a section from the side menu"));
    }
}