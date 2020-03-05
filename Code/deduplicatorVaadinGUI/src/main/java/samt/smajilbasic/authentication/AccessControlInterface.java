package samt.smajilbasic.authentication;

import java.io.Serializable;

import samt.smajilbasic.communication.Client;

/**
 * @author Vaadin Framework Simple UI example
 */
public interface AccessControlInterface extends Serializable {

    boolean signedIn(String name, Client client);

    boolean isUserSignedIn();

    String getName();

    void signOut();
}
