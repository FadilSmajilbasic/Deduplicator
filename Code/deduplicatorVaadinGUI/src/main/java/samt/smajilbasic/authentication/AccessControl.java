package samt.smajilbasic.authentication;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import samt.smajilbasic.Resources;
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
        Client client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);
        client.post("/logout", null);
        System.out.println("Logged out from client");
        UI.getCurrent().navigate("");
    }

    @Override
    public boolean signedIn(String name, Client client) {

        CurrentUser.set(name, client);
        return false;
    }

}
