package samt.smajilbasic.views;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * SchedulerView
 */
@Route(value = "scheduler", registerAtStartup = true)
@PageTitle(value = "Deduplicator - Scheduler")
public class SchedulerView extends BaseView {

    public SchedulerView() {
        super();
    }
}