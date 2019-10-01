package samt.smajilbasic.deduplicator.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
     */
    public void setPassword(String password) {
        this.password = password;
    }

}