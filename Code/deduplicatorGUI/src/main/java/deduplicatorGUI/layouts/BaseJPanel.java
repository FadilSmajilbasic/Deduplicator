package deduplicatorGUI.layouts;

import javax.swing.JPanel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import deduplicatorGUI.communication.Client;

/**
 * BaseJPanel
 */
public class BaseJPanel extends JPanel{

    private Client client;

    /**
     * @param client the client to set
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Method to be overridden
     */
    public void tabSelected(){

    }


    public JSONObject[] getArray(JSONArray array) {

        Object[] objectArray = (Object[]) array.toArray();
        JSONParser parser = new JSONParser();
        JSONObject[] result = new JSONObject[objectArray.length];
        for (int i = 0; i < objectArray.length; i++) {
            try {
                result[i] = (JSONObject) parser.parse(objectArray[i].toString());
            } catch (ParseException e) {
                System.out.println("unable to parse " + objectArray[i].toString());
            }
        }
        return result;
    }
    
}