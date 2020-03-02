package samt.smajilbasic;

import samt.smajilbasic.authentication.CurrentUser;
import samt.smajilbasic.communication.Client;

/**
 * Resources
 */
public abstract class Resources {

    /**
     * Defines the default notification length in ms.
     */
    public final static int NOTIFICATION_LENGTH = 2000;
    
    /**
     * The attribute key used to store the username in the session.
     */
    public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = CurrentUser.class.getCanonicalName();

    public static final String CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY = Client.class.getCanonicalName();
}