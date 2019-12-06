package deduplicatorGUI.layouts;

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
        return "Id: " + i + " Count: " + array[i].get("count").toString();
    }

    public String getHash(int i){
        return array[i].get("hash").toString();
    }



    public DuplicatesComboBoxModel(JSONObject[] array) {
        super();
        this.array = array;
    }
}