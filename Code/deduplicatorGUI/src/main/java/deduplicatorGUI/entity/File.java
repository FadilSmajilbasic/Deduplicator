package deduplicatorGUI.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * File
 */
public class File {

    private String path;

    private Long lastModified;

    private String hash;
    
    private Integer size;


    @JsonBackReference
    private Report report;


    public File() {
    }

    public File(String path,Long lastModified,String hash,Integer size,Report report) {
        setPath(path);
        setLastModified(lastModified);
        setHash(hash);
        setSize(size);
        setReport(report);
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
		}else if (type == PathType.Directory){
            throw new RuntimeException("[ERROR] Path is not a file: " + path);
		}else{
			throw new RuntimeException("[ERROR] Invalid path: " + path);
		}
    }

    /**
     * @return the lastModified
     */
    public Long getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified the lastModified to set
     */
    private void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    private void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the size
     */
    public Integer getSize() {
        return size;
    }


    /**
     * @param size the size to set
     */
    private void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @return the report
     */
    public Report getReport() {
        return report;
    }

    /**
     * @param report the report to set
     */
    public void setReport(Report report) {
        this.report = report;
    }

}