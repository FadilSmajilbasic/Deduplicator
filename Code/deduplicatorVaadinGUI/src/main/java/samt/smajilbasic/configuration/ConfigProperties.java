package samt.smajilbasic.configuration;

import org.springframework.beans.factory.annotation.Value;
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
}
