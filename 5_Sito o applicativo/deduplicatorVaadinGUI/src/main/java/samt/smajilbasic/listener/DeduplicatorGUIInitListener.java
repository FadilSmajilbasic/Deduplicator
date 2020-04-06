package samt.smajilbasic.listener;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import samt.smajilbasic.authentication.AccessControlInterface;
import samt.smajilbasic.authentication.AccessControlFactory;
import samt.smajilbasic.views.LoginView;


/**
 * This class is used to listen to BeforeEnter event of all UIs in order to
 * check whether a user is signed in or not before allowing entering any page.
 * It is registered in a file named
 * com.vaadin.flow.server.VaadinServiceInitListener in META-INF/services.
 */
public class DeduplicatorGUIInitListener implements VaadinServiceInitListener {
    @Override
    public void serviceInit(ServiceInitEvent initEvent) {
        final AccessControlInterface accessControl = AccessControlFactory.getInstance()
                .createAccessControl();

        initEvent.getSource().addUIInitListener(uiInitEvent -> {
            uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
                if (!accessControl.isUserSignedIn() && !LoginView.class
                        .equals(enterEvent.getNavigationTarget()))
                    enterEvent.rerouteTo(LoginView.class);
            });
        });
    }
}
