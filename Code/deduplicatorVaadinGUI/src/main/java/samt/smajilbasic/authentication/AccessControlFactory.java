package samt.smajilbasic.authentication;

/**
 * @author Vaadin Framework Simple UI example
 */
public class AccessControlFactory {
    private static final AccessControlFactory INSTANCE = new AccessControlFactory();
    private final AccessControl accessControl = new AccessControl();

    private AccessControlFactory() {
    }

    public static AccessControlFactory getInstance() {
        return INSTANCE;
    }

    public AccessControl createAccessControl() {
        return accessControl;
    }
}
