
package deduplicatorGUI.communication;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;

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
import java.util.function.BiConsumer;

/**
 *
 * @author Fadil Smajilbasic
 */
public class Client {

    private final HttpClient httpClient;

    private String username;

    private String password;
    private InetAddress addr;

    private int port;
    private URI uri;

    private final JSONParser parser = new JSONParser();

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        if (port > 0 && port < 65535) {
            this.port = port;
        } else {
            port = 8080;
        }
    }

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

            this.addr = addr;
            setPort(port);
            return stat.equals("OK");

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

    public Object getAll(String path) {
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://" + addr.getHostAddress() + ":" + port + "/" + path)).GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Object responseObj = new JSONParser().parse(response.body());

            return responseObj;

        } catch (URISyntaxException ex) {
            System.out.println("deduplicatorGUI.communication.Client.getAll()");

        } catch (InterruptedException ie) {
            System.out.println("interr" + ie.getMessage());
        } catch (IOException ioe) {
            System.err.println("ioe" + ioe.getMessage());
        } catch (ParseException pe) {
            System.err.println("parse get : " + pe.getStackTrace());

        }
        return null;
    }

    public Object get(String path) {
        return null;
    }

    public Object delete(String path, String param) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("path", param);

        RestTemplate restTemplate = new RestTemplate();
        // HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        // ResponseEntity<String> response = restTemplate.postForEntity("http://" +
        // addr.getHostAddress() + ":" + port + "/form", request, String.class);

        restTemplate.delete("http://" + addr.getHostAddress() + ":" + port + "/path/" + param.replace("/", "&47#;"),
                map);
        return new Object();
        // try {

        // HttpRequest request = HttpRequest.newBuilder()
        // .uri(new URI("http://" + addr.getHostAddress() + ":" + port + "/" + path))
        // .method("DELETE", HttpRequest.BodyPublishers.ofString("path:" +
        // param)).build();

        // HttpResponse<String> response = httpClient.send(request,
        // HttpResponse.BodyHandlers.ofString());

        // System.out.println("body: " + response.body());
        // Object resp = parser.parse(response.body());

        // return resp;

        // } catch (URISyntaxException ex) {
        // System.out.println("deduplicatorGUI.communication.Client.delete()");
        // } catch (InterruptedException ie) {
        // System.out.println("interr delete: " + ie.getMessage());
        // } catch (IOException ioe) {
        // System.err.println("ioe delete: " + ioe.getMessage());
        // } catch (ParseException pe) {
        // System.err.println("parse ex delete :" + pe.getMessage());
        // }

        // return null;
    }

    public Object post(String path, Map<String, String> values) {
        return null;
    }

    public Object put(String path, Map<String, String> values) {
        return null;
    }

}
