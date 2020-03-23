package samt.smajilbasic.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Validator
 */
public class Validator {

	private static final String numerRange = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

	private static final String IP_REGEXP = numerRange + "\\." + numerRange + "\\." + numerRange + "\\." + numerRange;

	private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

	public static PathType getPathType(String path) {
		try {
			if (path != null) {

				String pathParsed = path.replaceAll("&#47;", File.separator);
				Path pa = Paths.get(pathParsed);

				if (Files.isDirectory(pa)) {
					return PathType.Directory;
				} else {
					if (Files.exists(pa)) {
						return PathType.File;
					} else {
						return PathType.Invalid;
					}
				}
			} else {
				return PathType.Invalid;
			}
		} catch (InvalidPathException | NullPointerException | SecurityException ex) {
			System.err.println("[ERROR] " + ex.getMessage());
			return PathType.Invalid;
		}
	}

	/**
	 * 
	 * @param input input to validate (usually parameter)
	 * @return the integer value if input is an Integer, null otherwise
	 */
	public static Integer isInt(String input) {
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException nfe) {
			return null;
		}

	}

	/**
	 * 
	 * @param input input to validate (usually uri parameter)
	 * @return the Long value if input is a Long, null otherwise
	 */
	public static Long isLong(String input) {
		try {
			return Long.parseLong(input);
		} catch (NumberFormatException nfe) {
			return null;
		}

	}

	/**
	 * 
	 * @param input La stringa da verificare
	 * @return il valore di input se è valido oppure null.
	 */
	public static String isHex(String input) {
		return Pattern.matches("[0-9a-fA-F]+", input) ? input : null;

	}

	public static boolean isValidIP(String address) {
		return IP_PATTERN.matcher(address).matches();
	}

}