package samt.smajilbasic.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.navigator.View;

/**
 * BaseView
 */
public class BaseView extends VerticalLayout implements View {

    public BaseView() {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Scan", e -> {
            UI.getCurrent().getPage().setLocation("scan/");
        });
        menuBar.addItem("Path", e -> {
            UI.getCurrent().getPage().setLocation("path/");
        });
        add(menuBar);
    }
}