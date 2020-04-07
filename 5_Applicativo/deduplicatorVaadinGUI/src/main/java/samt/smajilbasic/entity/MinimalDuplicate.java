package samt.smajilbasic.entity;

import java.text.DecimalFormat;

/**
 * Class that describes the duplicate object returned by the API when querying
 * all duplicates of a report.
 */
public class MinimalDuplicate {
    private String hash;
    private Long size;
    private String count;

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the size
     */
    public String getSize() {
        return readableFileSize(size);
    }

    /**
     * Formats the given size in a readable format
     *
     * @param size the size of the duplicate as a long
     * @return the formatted file size in B, kB, MB, GB or TB depending on which is more appropriate for the file size.
     */
    public static String readableFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * @param size the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the count
     */
    public String getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(String count) {
        this.count = count;
    }

}
