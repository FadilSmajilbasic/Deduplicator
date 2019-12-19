package deduplicatorGUI.layouts;

import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import deduplicatorGUI.communication.Client;

/**
 * Il JPanel di base che verrà implementato dalle altre schermate.
 */
public class BaseJPanel extends JPanel{

    /**
     * Il client per le richieste.
     * È di tipo {@link Client}. 
     */
    private Client client;
    JSONParser parser = new JSONParser();

    /**
     * @param client il client da impostare.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * @return l'oggetto client.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Methodo che sarà sovrascritto dagli altri panel del progetto.
     */
    public void tabSelected(){
    }


    /**
     * Il metodo getArray trasforma un oggetto di tipo JSONArray in JSONObject[].
     * @param array l'array da trasformare.
     * @return l'array di JSONObject.
     */
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