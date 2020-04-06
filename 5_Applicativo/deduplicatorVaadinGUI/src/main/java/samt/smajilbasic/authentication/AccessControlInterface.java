package samt.smajilbasic.authentication;

import java.io.Serializable;

import samt.smajilbasic.communication.Client;

/**
 * Source: https://vaadin.com/start/v14
 * @author Vaadin Framework Simple UI example
 */
public interface AccessControlInterface extends Serializable {

    boolean signedIn(String name, Client client);

    boolean isUserSignedIn();

    String getName();

    void signOut();
}
