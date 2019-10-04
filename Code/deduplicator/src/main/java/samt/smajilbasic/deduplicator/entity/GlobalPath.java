package samt.smajilbasic.deduplicator.entity;


import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

import samt.smajilbasic.deduplicator.PathType;
import samt.smajilbasic.deduplicator.Validator;
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
		PathType type = Validator.getPathType(path) ;
		if(type  == PathType.File){
			this.path = path;
			this.setFile(true);
		}else if (type == PathType.Directory){
			this.path = path;
			this.setFile(false);
		}else{
			throw new PathException("Invalid path: " + path);
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