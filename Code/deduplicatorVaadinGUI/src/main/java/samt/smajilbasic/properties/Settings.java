package samt.smajilbasic.properties;

import samt.smajilbasic.model.Resources;

import javax.validation.constraints.NotBlank;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to load the config file settings and values into real variables.
 * <p>
 * Source: https://github.com/LuMug/DroneControlSystem
 *
 * @author Luca Di Bello
 * @version 20.05.2019
 */
public class Settings {

    /**
     * SettingManager object that allows the user to interact within the config file-
     */
    private SettingsManager manager = new SettingsManager();

    private String logPath;

    private String caPassword;

    private int refreshInterval;

    private int notificationLength;


    /**
     * Default constructor.
     */
    public Settings() {
        updateSettings();
    }

    /**
     * This method loads into the variables all the controller-related settings.
     *
     * @throws IllegalArgumentException thrown when a
     *                                  setting is without value or non-existent
     */
    private void loadSettings() {
        logPath = Objects.requireNonNullElse(manager.getSetting("logPath"), Resources.LOG_PATH);
        caPassword = Objects.requireNonNullElse(manager.getSetting("CAPassword"), "");
        refreshInterval = getIntSetting("refreshInterval");
        notificationLength = getIntSetting("notificationLength");
    }


    private int getIntSetting(String settingName) {
        try {
            return Integer.parseInt(manager.getSetting(settingName));
        } catch (NumberFormatException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Unable to read integer value of requested setting: " + settingName);
            return 0;
        }
    }

    /**
     * Reload all the settings.
     */
    public void updateSettings() {
        loadSettings();
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {

        Path p = Paths.get(logPath);
        if (Files.isDirectory(p)) {
            if (logPath.charAt(logPath.length() - 1) != File.separatorChar) {
                logPath += File.separator;
                this.logPath = logPath;
                manager.setSetting("logPath", logPath);
            }
        }else{
            Logger.getGlobal().log(Level.SEVERE,"Log path to set " + logPath + " is not a directory");
        }

    }

    public String getCaPassword() {
        return caPassword;
    }

    public void setCAPassword(String caPassword) {
        this.caPassword = caPassword;
        manager.setSetting("CAPassword", caPassword);

    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        if (refreshInterval > 0)
            this.refreshInterval = refreshInterval;
        else
            this.refreshInterval = Resources.REFRESH_INTERVAL;
        manager.setSetting("refreshInterval", String.valueOf(this.refreshInterval));
    }

    public int getNotificationLength() {
        return notificationLength;
    }

    public void setNotificationLength(int notificationLength) {
        if (notificationLength > 0)
            this.notificationLength = notificationLength;
        else
            this.notificationLength = Resources.NOTIFICATION_LENGTH;

        manager.setSetting("notificationLength", String.valueOf(this.notificationLength));

    }
}  
