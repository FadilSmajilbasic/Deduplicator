package samt.smajilbasic.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import samt.smajilbasic.Resources;
import samt.smajilbasic.authentication.AccessControl;
import samt.smajilbasic.authentication.AccessControlFactory;

/**
 * DashboardView
 */
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle(value = "Deduplicator - Dashboard")
public class DashboardView extends FormLayout{

    public static final String VIEW_NAME = "Dashboard";

	public DashboardView() {
        super();
        setResponsiveSteps(new ResponsiveStep(Resources.SIZE_MOBILE_S,1),new ResponsiveStep(Resources.SIZE_MOBILE_M,2));
        VerticalLayout leftSide = new VerticalLayout(new Label("User: " + AccessControlFactory.getInstance().createAccessControl().getName()));
        FormLayout leftSideFormLayout = new FormLayout();
        leftSideFormLayout.setResponsiveSteps(new ResponsiveStep(Resources.SIZE_MOBILE_S,2));
        Button changePasswordButton = new Button("Change password");
        Button clearLogsButton = new Button("Clear logs");
        Button changeUsernameButton = new Button("Change username");
        Button downloadLogsButton = new Button("Download logs");
        Button changeMYSQLButton = new Button("Change MYSQL database credentials");
        Button changeLogFileLocationButton = new Button("Change log file location");

        FormLayout rightSide = new FormLayout();

        setMinWidth(Resources.SIZE_MOBILE_S);
        add(leftSide,rightSide);
    }
}