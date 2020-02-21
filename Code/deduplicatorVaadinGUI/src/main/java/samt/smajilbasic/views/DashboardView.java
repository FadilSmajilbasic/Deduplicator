package samt.smajilbasic.views;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * DashboardView
 */
@Route(value = "dashboard", registerAtStartup = true)
@PageTitle(value = "Deduplicator - Dashboard")
public class DashboardView extends BaseView {

    public DashboardView() {
        super();
    }
}