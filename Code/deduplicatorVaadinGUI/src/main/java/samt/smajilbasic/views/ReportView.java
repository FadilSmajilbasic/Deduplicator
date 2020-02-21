package samt.smajilbasic.views;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * ReportView
 */
@Route(value = "scheduler", registerAtStartup = true)
@PageTitle(value = "Deduplicator - Report")
public class ReportView extends BaseView {

    public ReportView() {
        super();
    }
}