package samt.smajilbasic.deduplicator.entities;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class GlobalPath {
	@Id
    private String path;

    private boolean file;

	private boolean ignoreFile;
	
	private Timestamp date;

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
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
	public void setFile(boolean file) {
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
	public void setIgnoreFile(boolean ignoreFile) {
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
	public void setDate(Timestamp date) {
		this.date = date;
	}
}