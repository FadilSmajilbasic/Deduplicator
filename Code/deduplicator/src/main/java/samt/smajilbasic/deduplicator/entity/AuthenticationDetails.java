package samt.smajilbasic.deduplicator.entity;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * AuthenticationDetails
 */
@Entity
public class AuthenticationDetails {

    @Id
    private String username;

    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Report> report;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
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
            this.password = password;
        }else{
            this.password = "";
        }
    }

}