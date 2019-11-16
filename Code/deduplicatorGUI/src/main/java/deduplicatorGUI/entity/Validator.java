package deduplicatorGUI.entity;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Validator
 */
public class Validator {

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
	 * @param input input to validate (usually uri parameter)
	 * @return the integer value if input is a number, null otherwise
	 */
	public static Integer isInt(String input) {
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException nfe) {
			return null;
		}
			
	}
}