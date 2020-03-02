package samt.smajilbasic.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * DashboardView
 */
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle(value = "Deduplicator - Dashboard")
public class DashboardView extends VerticalLayout{

    public static final String VIEW_NAME = "Dashboard";

	public DashboardView() {
        super();
    }
}