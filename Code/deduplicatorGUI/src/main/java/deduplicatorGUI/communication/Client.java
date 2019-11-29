
package deduplicatorGUI.communication;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;


/**
 *
 * @author Fadil Smajilbasic
 */
public class Client {

    // private final HttpClient httpClient;

    private String username;

    private String password;
    private InetAddress addr;

    private int port;

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
        this.username = username;
        this.password = password;
    }

    public boolean isAuthenticated(InetAddress addr, int port) throws RestClientException {

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(createHeaders(false));
        ResponseEntity<String> response = restTemplate.exchange("http://" + addr.getHostName() + ":" + port + "/login/",
                HttpMethod.GET, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            this.addr = addr;
            setPort(port);
            return true;

        } else {
            return false;
        }

    }

    public Object get(String path) {

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(createHeaders(false));
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://" + addr.getHostName() + ":" + port + "/" + path, HttpMethod.GET, requestEntity,
                    String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                setPort(port);
                Object responseObj = new JSONParser().parse(response.getBody());
                return responseObj;

            } else {

                return null;
            }
        } catch (RestClientException rce) {
            System.out.println(rce);
            return null;
        } catch (ParseException pe) {
            System.err.println("parse get : " + pe.getStackTrace());

        }
        return null;
    }

    public ResponseEntity<String> delete(String path, String param) {

        MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("path", param);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange("http://" + addr.getHostAddress() + ":" + port + "/path/",
                    HttpMethod.DELETE, requestEntity, String.class);
        } catch (RestClientException rce) {
            System.out.println("rce: " + rce.getMessage());
        }

        if (response != null) {
            return response;
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    private HttpHeaders createHeaders(boolean hasFormData) {
        HttpHeaders header = new HttpHeaders();

        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);

        header.add("Authorization", authHeader);

        if (hasFormData) {
            header.setContentType(MediaType.MULTIPART_FORM_DATA);
        }
        return header;
    }

    public Object post(String path, MultiValueMap<String, Object> values) {

        RestTemplate restTemplate = new RestTemplate();
        values = values == null ? new LinkedMultiValueMap<>() : values;

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange("http://" + addr.getHostAddress() + ":" + port + "/" + path,
                    HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException rce) {
            System.out.println("rce: " + rce.getMessage());
        }

        return response;
    }

    public Object put(String path, Map<String, String> values) {
        return null;
    }

    public ResponseEntity<String> insertPath(String pathText, boolean ignored) {
        MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("path", pathText);
        values.add("ignorePath", ignored);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;

        try {
            response = restTemplate.exchange("http://" + addr.getHostAddress() + ":" + port + "/path/", HttpMethod.PUT,
                    requestEntity, String.class);
        } catch (RestClientException rce) {
            System.out.println("rce: " + rce.getMessage());
        }

        if (response != null) {
            return response;
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
