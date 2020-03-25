package samt.smajilbasic.views;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;

import samt.smajilbasic.authentication.AccessControlFactory;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * The main view of the application that redirects to the login view.
 *
 * @author Vaadin Framework Simple UI example
 */
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@CssImport(value = "./styles/menu-buttons.css")
@CssImport(value = "./styles/menu-links.css")
public class MainLayout extends AppLayout{

private final Button logoutButton;

  public MainLayout() {
    //Menu toggle button
    final DrawerToggle drawerToggle = new DrawerToggle();
    drawerToggle.addClassName("menu-toggle");
    addToNavbar(drawerToggle);

    //Horizontal bar on top
    final HorizontalLayout top = new HorizontalLayout();
    top.setDefaultVerticalComponentAlignment(Alignment.CENTER);
    top.setClassName("menu-header");
    final Label title = new Label("DeduplicatorGUI");
    Label credits = new Label("Created by Fadil Smajilbasic ");
    HorizontalLayout container = new HorizontalLayout();
    container.setWidthFull();
    container.add(credits);
    container.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
    credits.setClassName("credits-label");
    top.setWidthFull();
    top.add(title,container);
    addToNavbar(top);

    //Menu links
    addToDrawer(createMenuLink(MainView.class, MainView.VIEW_NAME, VaadinIcon.HOME.create()));
    addToDrawer(createMenuLink(PathView.class, PathView.VIEW_NAME, VaadinIcon.EDIT.create()));
    addToDrawer(createMenuLink(ScanView.class, ScanView.VIEW_NAME, VaadinIcon.BUG.create()));
    addToDrawer(createMenuLink(ReportView.class, ReportView.VIEW_NAME, VaadinIcon.FILE_TEXT.create()));
    addToDrawer(createMenuLink(SchedulerView.class, SchedulerView.VIEW_NAME, VaadinIcon.CALENDAR.create()));
    addToDrawer(createMenuLink(DashboardView.class, DashboardView.VIEW_NAME, VaadinIcon.DASHBOARD.create()));

    //logout button
    logoutButton = createMenuButton("Logout", VaadinIcon.SIGN_OUT.create());
    logoutButton.addClickListener(e -> logout());
    logoutButton.getElement().setAttribute("title", "Logout (Ctrl+L)");

  }

        private RouterLink createMenuLink(Class<? extends Component> viewClass, String caption, Icon icon) {
                final RouterLink routerLink = new RouterLink(null, viewClass);
                routerLink.setClassName("menu-link");
                routerLink.add(icon);
                routerLink.add(new Span(caption));
                icon.setSize("2em");
                return routerLink;
        }

        private Button createMenuButton(String caption, Icon icon) {
                final Button routerButton = new Button(caption);
                routerButton.setClassName("menu-button");
                routerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                routerButton.setIcon(icon);
                icon.setSize("2em");
                return routerButton;
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
                super.onAttach(attachEvent);

                // User can quickly activate logout with Ctrl+L
                attachEvent.getUI().addShortcutListener(() -> logout(), Key.KEY_L, KeyModifier.CONTROL);

                // add the admin view menu item if user has admin role
                addToDrawer(logoutButton);
        }

        private void logout() {
                AccessControlFactory.getInstance().createAccessControl().signOut();
        }

}