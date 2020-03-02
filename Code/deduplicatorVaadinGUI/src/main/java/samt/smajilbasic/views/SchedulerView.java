package samt.smajilbasic.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * SchedulerView
 */
@Route(value = "scheduler", layout = MainLayout.class )
@PageTitle(value = "Deduplicator - Scheduler")
public class SchedulerView extends VerticalLayout {

    public static final String VIEW_NAME = "Scheduler";

	public SchedulerView() {
        super();
        
    }
}