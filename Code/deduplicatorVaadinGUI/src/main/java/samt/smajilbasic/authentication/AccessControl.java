package samt.smajilbasic.authentication;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import samt.smajilbasic.communication.Client;

/**
 * @author Vaadin Framework Simple UI example
 */
public class AccessControl implements AccessControlInterface {

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }

    @Override
    public String getName() {
        return CurrentUser.get();
    }

    @Override
    public void signOut() {
        VaadinSession.getCurrent().getSession().invalidate();
        CurrentUser.set(null, null);
        UI.getCurrent().navigate("");
    }

    @Override
    public boolean signedIn(String name, Client client) {

        CurrentUser.set(name, client);
        return false;
    }

}
