package samt.smajilbasic.deduplicator.entities;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

import samt.smajilbasic.deduplicator.exception.PathException;

@Entity
public class GlobalPath {
	@Id
	private String path;

	private boolean file;

	private boolean ignoreFile;

	private Timestamp date;

	public GlobalPath() {
	}

	public GlobalPath(String path, boolean ignoreFile) {
		this.setPath(path);
		this.setIgnoreFile(ignoreFile);
		this.setDate(new Timestamp(System.currentTimeMillis()));
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	private void setPath(String path) {
		try {
			if (path != null) {

				String pathParsed = path.replaceAll("&#47;", File.separator);
				System.out.println("Path: " + pathParsed);
				Path pa = Paths.get(pathParsed);

				this.path = path;
				if (Files.isDirectory(pa)) {
					this.setFile(false);
				} else {
					if (Files.exists(pa)) {
						this.setFile(true);
					} else {
						throw new InvalidPathException(path, "Doesn't exist");
					}
				}

			} else {
				throw new PathException();
			}
		} catch (InvalidPathException | NullPointerException | SecurityException ex) {
			System.err.println("[ERROR] " + ex.getMessage());
			throw new PathException(ex.getMessage());
		}
	}

	/**
	 * @return the file
	 */
	public boolean isFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	private void setFile(boolean file) {
		this.file = file;
	}

	/**
	 * @return the ignore_file
	 */
	public boolean isIgnoreFile() {
		return ignoreFile;
	}

	/**
	 * @param ignoreFile the ignoreFile to set
	 */
	private void setIgnoreFile(boolean ignoreFile) {
		this.ignoreFile = ignoreFile;
	}

	/**
	 * @return the date
	 */
	public Timestamp getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	private void setDate(Timestamp date) {
		this.date = date;
	}
}