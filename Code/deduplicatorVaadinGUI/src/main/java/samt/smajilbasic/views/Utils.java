package samt.smajilbasic.views;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * GlobalView
 */
public abstract class Utils {

    /**
     * Il metodo getArray trasforma un oggetto di tipo JSONArray in JSONObject[].
     * @param array l'array da trasformare.
     * @return l'array di JSONObject.
     */
    public static JSONObject[] getArray(JSONArray array) {

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