package samt.smajilbasic.authentication;

import com.vaadin.flow.component.UI;

import samt.smajilbasic.communication.Client;
import samt.smajilbasic.model.Resources;

/**
 * Class for retrieving and setting the name and {@link Client} of the current user of the current
 * session (without using JAAS). 
 *
 * @author Vaadin Framework Simple UI example
 */
public final class CurrentUser {

    private CurrentUser() {
    }

    /**
     * Returns the name of the current user stored in the current session, or an
     * empty string if no user name is stored.
     * 
     * @throws IllegalStateException if the current session cannot be accessed.
     */
    public static String get() {
        String currentUser = (String) UI.getCurrent().getSession()
                .getAttribute(Resources.CURRENT_USER_SESSION_ATTRIBUTE_KEY);
        if (currentUser == null) {
            return "";
        } else {
            return currentUser;
        }
    }

    /**
     * Sets the name of the current user and the {@link Client} associated with it in the current session. Using
     * a {@code null} username will remove the username and the {@link Client} from the session.
     * 
     * @throws IllegalStateException if the current session cannot be accessed.
     */
    public static void set(String currentUser, Client client) {
        if (currentUser == null) {
            UI.getCurrent().getSession().setAttribute(Resources.CURRENT_USER_SESSION_ATTRIBUTE_KEY, null);
            UI.getCurrent().getSession().setAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY, null);
        } else {
            UI.getCurrent().getSession().setAttribute(Resources.CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);
            UI.getCurrent().getSession().setAttribute(Resources.CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY, client);
        }
    }

}
