package samt.smajilbasic.properties;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles a config file. By default it uses a file placed in
 * this path: 'config/config.dcs' but through a second parameterized constructor
 * it is possible to set a different one. The character that identifies a
 * commented line within the config file is the '#' character,
 * also it is possible to replace it through the constructors.
 * Source: https://github.com/LuMug/DroneControlSystem
 *
 * @author Luca Di Bello
 * @version 20.05.2019
 */
public class SettingsManager {

    /**
     * Path of the configuration file. By default the path is 'config/config.dcs' (path
     * relative).
     */
    private Path filePath = Paths.get("config", "deduplicator.config");

    /**
     * Character that identifies a commented line within the
     * config.
     */
    private char commentCharacter = this.DEFAULT_COMMENT_CHARACTER;

    /**
     * String that divides the setting name from the value
     * setting. Example:
     * setting_name<division_character>setting_value.
     */
    private char settingDelimiter = this.DEFAULT_SETTING_DELIMITER;

    /**
     * Default character for the commentCharacter parameter.
     */
    private final char DEFAULT_COMMENT_CHARACTER = '#';

    /**
     * Default string for the stringDelimiter parameter.
     */
    private final char DEFAULT_SETTING_DELIMITER = '=';

    /**
     * Default constructor
     */
    public SettingsManager() {
        checkFileExists();
    }

    public void checkFileExists() {
        File file = new File(filePath.toAbsolutePath().toString());
        if (!file.exists()) {
            Logger.getGlobal().log(Level.INFO, "Configuration file not found, making a new one");
            if (Files.isWritable(Paths.get(file.getParent()))) {
                if (!file.mkdir()) {
                    try {
                        if (file.createNewFile()) {
                            Logger.getGlobal().log(Level.INFO, "Writing new values");
                            setSetting("logPath", "log/");
                            setSetting("CAPassword", "");
                            setSetting("refreshInterval", "500");
                            setSetting("notificationLength", "2000");
                            Logger.getGlobal().log(Level.INFO, "Created file and wrote default configuration to file");
                        } else {
                            Logger.getGlobal().log(Level.SEVERE, "Unable to make configuration file");
                        }
                    } catch (IOException ioe) {
                        Logger.getGlobal().log(Level.SEVERE, "Unable to make configuration file - IOException: " + ioe.getMessage());
                    }
                } else {
                    Logger.getGlobal().log(Level.SEVERE, "Unable to make configuration directory");
                }
            } else {
                Logger.getGlobal().log(Level.SEVERE, "Unable to write to parent directory");
            }
        }
    }

    /**
     * Parametrized constructor.
     *
     * @param filePath config file location (path).
     */
    public SettingsManager(Path filePath) {
        this(filePath, '=', '#');
    }

    /**
     * Costruttore parametrizzato.
     *
     * @param filePath         config file location (path).
     * @param settingDelimiter Delimiter that divides the setting name and the its value.
     * @param commentCharacter Delimiter which indentifies if a line it's commented.
     */
    public SettingsManager(Path filePath, char settingDelimiter, char commentCharacter) {
        this.filePath = filePath;
        setSettingDelimiter(settingDelimiter);
        setCommentCharacter(commentCharacter);
    }

    /**
     * * This method allows you to generate a dictionary containing all the
     * settings in the configuration file. The dictionary is structured in this way:
     * <ul>
     *  <li>Search key = Setting name</li>
     *  <li>Return value = Setting value</li>
     * </ul>
     *
     * @return Dictionary of strings where you can use the name
     * of the setting as a search key within it.
     */
    public Map<String, String> getSettings() {
        Map<String, String> map = new HashMap<>();
        try {
            for (String line : Files.readAllLines(filePath)) {
                if (line.length() > 0) {
                    if (line.charAt(0) == this.commentCharacter) {
                        continue;
                    }
                    String[] data = line.split("" + settingDelimiter);

                    if (data.length == 2) {
                        String key = data[0];
                        String value = data[1];

                        map.put(key, value);
                    }
                }
            }

        } catch (IOException ex) {
            System.err.println("[Error] Error while generating settings dictionary");
        }

        return map;
    }

    /**
     * This method allows you to take the value of a specific setting just
     * using its name.
     *
     * @param settingName Name of the setting.
     * @return Setting value.
     * @throws IllegalArgumentException throwed when a
     *                                  setting is without value or non-existent
     */
    public String getSetting(String settingName) {
        Map<String, String> map = getSettings();
        String data = map.get(settingName);

        if (data != null) {
            return data;
        } else {
            Logger.getGlobal().log(Level.SEVERE, "Unable to load setting: " + settingName);
            return null;
        }
    }

    /**
     * This method allows you to set/modify a value of a specific setting just using
     * it's name.
     *
     * @param settingName Name of the setting.
     * @param value       Value which will be set as setting value.
     * @throws IllegalArgumentException throwed when a
     *                                  setting is without value or non-existent
     */
    public void setSetting(String settingName, String value) {
        //Read all file lines
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.length() > 0 && !line.startsWith("#")) {
                    //Not comment
                    String scrapedSetting = line.split(String.valueOf(getSettingDelimiter()))[0];
                    if (scrapedSetting.equals(settingName)) {
                        //Build new setting string
                        String setting = scrapedSetting + getSettingDelimiter() + value;
                        lines.set(i, setting);
                        break;
                    }
                }
            }

            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Unable to set setting " + settingName + " IOException: " + ex.getMessage());
        }
    }

    /**
     * Getter method for commentCharacter parameter.
     *
     * @return Delimiter which indentifies if a line it's commented.
     */
    public char getCommentCharacter() {
        return commentCharacter;
    }

    /**
     * Setter method for commentCharacter parameter.
     *
     * @param commentCharacter Character that indentifies if a line it's commented.
     */
    private void setCommentCharacter(Character commentCharacter) {
        if (commentCharacter.equals('\u0000')) {
            this.commentCharacter = this.DEFAULT_COMMENT_CHARACTER;
        } else {
            this.commentCharacter = commentCharacter;
        }
    }

    /**
     * Getter method for settingDelimiter parameter.
     *
     * @return Delimiter that divides the setting name and the its value.
     */
    public char getSettingDelimiter() {
        return settingDelimiter;
    }

    /**
     * Setter method for settingDelimiter parameter.
     *
     * @param settingDelimiter New delimiter that divides the setting name and the its value.
     */
    private void setSettingDelimiter(Character settingDelimiter) {
        if (settingDelimiter.equals('\u0000')) {
            this.settingDelimiter = this.DEFAULT_SETTING_DELIMITER;
        } else {
            this.settingDelimiter = settingDelimiter;
        }
    }
}
