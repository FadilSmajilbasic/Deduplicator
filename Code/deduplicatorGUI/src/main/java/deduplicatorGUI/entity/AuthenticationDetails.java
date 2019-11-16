package deduplicatorGUI.entity;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * AuthenticationDetails
 */
public class AuthenticationDetails {

    private String username;

    private String password;

    private List<Report> report;

    private List<Action> action;

    public AuthenticationDetails(String username, String password) throws NoSuchAlgorithmException {
        setUsername(username);
        setPassword(password);
    }

    public AuthenticationDetails() {
        super();
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {

        return password;
    }

    /**
     * @param password the password to set
     * @throws NoSuchAlgorithmException
     */
    public void setPassword(String password) throws NoSuchAlgorithmException {
        if(password != null){
            this.password = new BCryptPasswordEncoder().encode(password);
        }else{
            this.password = "";
        }
    }

}