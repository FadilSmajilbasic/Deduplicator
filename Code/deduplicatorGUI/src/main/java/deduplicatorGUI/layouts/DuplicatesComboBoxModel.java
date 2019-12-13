package deduplicatorGUI.layouts;

import java.text.DecimalFormat;

import javax.swing.DefaultComboBoxModel;

import org.json.simple.JSONObject;

/**
 * DuplicatesComboBoxModel
 */
public class DuplicatesComboBoxModel extends DefaultComboBoxModel {

    private JSONObject[] array;

    public int getSize() {
        return array.length;
    }

    public String getElementAt(int i) {
        return "Id: " + i + " Count: " + array[i].get("count").toString() + " Size: " + getSize(Double.valueOf(array[i].get("size").toString()));
    }

    public String getHash(int i) {
        return array[i].get("hash").toString();
    }

    public DuplicatesComboBoxModel(JSONObject[] array) {
        super();
        this.array = array;
    }

    private String getSize(Double sizeDouble) {
        String size = "";
        DecimalFormat formatter = new DecimalFormat("0.0##");

        if (sizeDouble > 1073741824.0)
            size = formatter.format(sizeDouble / 1073741824.0) + " GB";
        else if (sizeDouble > 1048576.0)
            size = formatter.format(sizeDouble / 1048576.0) + " MB";
        else if (sizeDouble > 1024.0)
            size = formatter.format(sizeDouble / 1024.0) + " KB";
        else
            size = sizeDouble + " B";

        return size;
    }
}