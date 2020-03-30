package samt.smajilbasic.model;

import org.springframework.stereotype.Component;
import samt.smajilbasic.SpringContext;
import samt.smajilbasic.authentication.CurrentUser;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.configuration.ConfigProperties;

/**
 * Resources
 */
@Component
public class Resources {

    /**
     * Defines the default notification length in ms.
     */
    public final int notificationLength;
    
    /**
     * The key used to store the reference to the username in the session.
     */
    public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = CurrentUser.class.getCanonicalName();

    public static final String CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY = Client.class.getCanonicalName();

    public static final String SIZE_MOBILE_L = "425px";
    public static final String SIZE_MOBILE_M = "375px";
    public static final String SIZE_MOBILE_S = "320px";
    public static final String SIZE_TABLET = "768px";
    public static final String SIZE_LAPTOP = "1024px";
    public static final String SIZE_LAPTOP_L = "1440px";
    public static final String SIZE_LAPTOP_4K = "2560px";

    //TODO: check resources taht can be exported to applciation.properties

    public static final int USERNAME_LENGTH = 4;
    public static final int PASSWORD_LENGTH = 8;

    public static final int DUPLICATES_BUFFER_LENGTH = 10;

    private ConfigProperties props = SpringContext.getBean(ConfigProperties.class);

    public Resources(){
        notificationLength = Integer.parseInt(props.getNotificationLength());
    }

    public int getNotificationLength() {
        return notificationLength;
    }
}