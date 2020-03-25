package samt.smajilbasic.authentication;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import samt.smajilbasic.model.Resources;
import samt.smajilbasic.communication.Client;

import java.util.logging.Level;
import java.util.logging.Logger;

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
        Client client = (Client) UI.getCurrent().getSession().getAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY);
        if(client != null)
            client.post("/logout", null);
        CurrentUser.set(null, null);
        UI.getCurrent().navigate("");
        Logger.getGlobal().log(Level.INFO,"Successfully logged out");
    }

    @Override
    public boolean signedIn(String name, Client client) {
        CurrentUser.set(name, client);
        return false;
    }

}
