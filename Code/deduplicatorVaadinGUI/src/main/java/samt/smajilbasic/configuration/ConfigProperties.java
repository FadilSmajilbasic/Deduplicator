package samt.smajilbasic.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "deduplicator")
public class ConfigProperties {

    @NotBlank
    private String logPath;

    @NotBlank
    private String CAPassword;

    @NotBlank
    private String refreshInterval;

    @NotBlank
    private String notificationLength;

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        if(logPath != null)
            this.logPath = logPath;
    }

    public String getCAPassword() {
        return CAPassword;
    }

    public void setCAPassword(String CAPassword) {
        this.CAPassword = CAPassword;
    }

    public String getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(String refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public String getNotificationLength() {
        return notificationLength;
    }

    public void setNotificationLength(String notificationLength) {
        this.notificationLength = notificationLength;
    }
}
