
package deduplicatorGUI.communication;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 *
 * @author Fadil Smajilbasic
 */
public class Client {

    private final HttpClient httpClient;

    private String username;

    private String password;
    private String ip;
    private int port;

    public Client(String username, String password) {

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }

        });
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                .authenticator(Authenticator.getDefault()).build();

    }

    public boolean isAuthenticated(InetAddress addr, int port) {
        try {

            var request = HttpRequest.newBuilder().uri(new URI("http://" + addr.getHostName() + ":" + port + "/login/"))
                    .GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Object responseObj = new JSONParser().parse(response.body());

            JSONObject jsonObj = (JSONObject) responseObj;
            String stat = (String) jsonObj.get("status");

            System.out.println("stat: " + stat);
            return (stat.equals("OK"));

        } catch (URISyntaxException ex) {
            System.out.println("Uri exception " + ex.getMessage());
        } catch (InterruptedException ex) {
            System.out.println("Interrupted exception " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("IO exceptionn " + ex.getMessage());
        } catch (ParseException ex) {
            System.out.println("Parse exception " + ex.getMessage());
        }
        return false;

    }

    public JSONObject getAll(String path) {
        try {

            var request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/" + path)).GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Object responseObj = new JSONParser().parse(response.body());

            JSONObject jsonObj = (JSONObject) responseObj;

            String stat = (String) jsonObj.get("status");
            if (stat.equals("OK")) {
                System.out.println("stat is 200");
                return jsonObj;
            }

        } catch (URISyntaxException ex) {
            System.out.println("deduplicatorGUI.communication.Client.getAll()");

        } catch (InterruptedException ie) {
            System.out.println("interr" + ie.getMessage());
        } catch (IOException ioe) {
            System.err.println("ioe" + ioe.getMessage());
        } catch (ParseException pe) {
            System.err.println("parse" + pe.getMessage());

        }
        return null;
    }

    public Object get(String path) {
        return null;
    }

    public Object post(String path, Map<String, String> values) {
        return null;
    }

    public Object put(String path, Map<String, String> values) {
        return null;
    }

}
