package samt.smajilbasic.authentication;

import java.io.Serializable;

import samt.smajilbasic.communication.Client;


public interface AccessControlInterface extends Serializable {

    boolean signedIn(String name, Client client);

    boolean isUserSignedIn();

    String getName();

    void signOut();
}
