
package samt.smajilbasic.communication;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import samt.smajilbasic.configuration.ConfigProperties;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.entity.Action;
import samt.smajilbasic.entity.GlobalPath;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Fadil Smajilbasic
 */
@Component
public class Client {

    private String username;
    private String password;
    private String host;
    private RestTemplate restTemplate;

    private int port;
    private KeyStore keyStore;
    private static final String prefix = "http://";

    @Autowired
    private ConfigProperties props;

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

    JSONParser parser = new JSONParser();


    public Client(){

    }

    /**
     * Il costruttore che riceve il username e la password per l'autenticazione
     * basic che verrà utilizzata in tutte le GUI.
     *
     * @param username il username da impostare.
     * @param password la password da impostare.
     */
    public Client(String username, String password) {
        this.username = username;
        this.password = password;

        HttpComponentsClientHttpRequestFactory requestFactory = null;
        try {
            FileInputStream in = new FileInputStream(new File("deduplicator.p12"));

            String caPassword = props.getCAPassword();
            System.out.println("CA_PASS " + caPassword);
            try {
                keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(in,
                    caPassword.toCharArray());
            } catch (IOException e) {
                StackTraceElement[] output =
                    e.getStackTrace();
                for (StackTraceElement element : output) {
                    System.out.println(element.toString());
                }
            } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
                e.printStackTrace(System.out);
            }


            try {

                TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String
                    authType) -> true;
                SSLContext sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore, caPassword.toCharArray()).loadTrustMaterial(null,
                        acceptingTrustStrategy).build();

                HttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();

                requestFactory = new HttpComponentsClientHttpRequestFactory();

                requestFactory.setHttpClient(httpClient);


            } catch (UnrecoverableKeyException | NoSuchAlgorithmException |
                KeyStoreException | KeyManagementException e) {

                System.out.println("Unable to create client: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (FileNotFoundException fne) {
            try {
                System.out.println("curr dir: " +   new java.io.File( "." ).getCanonicalPath());
            }catch (IOException ioe){
                System.out.println("ioe");
            }
            System.out.println("File not found");
            Logger.getGlobal().log(Level.SEVERE,"File not found");
        }

        if (requestFactory != null)
            restTemplate = new RestTemplate(requestFactory);
    }

    public HttpStatus isAuthenticated(String host, int port) throws RestClientException {

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(createHeaders(false));
        ResponseEntity<String> response = null;

        try {
            response = restTemplate.exchange(prefix + host + ":" + port + "/access/login/", HttpMethod.GET, requestEntity,
                String.class);
        } catch (RestClientException rce) {
            System.err.println("[ERROR] Client exception: " + rce.getMessage());
            if (rce.getMessage().strip().equals("401")) {
                return HttpStatus.UNAUTHORIZED;
            }
        }

        if (response != null) {
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                this.host = host;
                setPort(port);
            }
            return response.getStatusCode();
        }
        return HttpStatus.SERVICE_UNAVAILABLE;

    }

    public ResponseEntity<String> get(String path) {

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(createHeaders(false));
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(prefix + host + ":" + port + "/" + path,
                String.class, requestEntity);

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return response;
            } else {
                Logger.getGlobal().log(Level.SEVERE, "Response status code is not OK");
                return null;
            }
        } catch (RestClientException rce) {
            Logger.getGlobal().log(Level.SEVERE, "Rest client exception: " + rce);
        }
        return null;
    }

    public ResponseEntity<String> delete(String path, String param) {

        MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("path", param);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(prefix + host + ":" + port + "/path/", HttpMethod.DELETE, requestEntity,
                String.class);
        } catch (RestClientException rce) {
            System.err.println("[ERROR] Delete rce: " + rce.getMessage());
        }

        return Objects.requireNonNullElseGet(response, () -> new ResponseEntity<String>(HttpStatus.BAD_REQUEST));

    }

    private HttpHeaders createHeaders(boolean hasFormData) {
        HttpHeaders header = new HttpHeaders();

        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        header.add("Authorization", authHeader);

        if (hasFormData) {
            header.setContentType(MediaType.MULTIPART_FORM_DATA);
        }
        return header;
    }

    public ResponseEntity<String> post(String path, MultiValueMap<String, Object> values) {

        values = values == null ? new LinkedMultiValueMap<>() : values;

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(prefix + host + ":" + port + "/" + path, HttpMethod.POST, requestEntity,
                String.class);
        } catch (RestClientException rce) {
            System.err.println("[ERROR] Post rce: " + rce.getMessage());
            rce.printStackTrace(System.out);
        }
        return response;
    }

    public ResponseEntity<String> put(String path, MultiValueMap<String, Object> values) {
        values = values == null ? new LinkedMultiValueMap<>() : values;

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;

        try {
            response = restTemplate.exchange(prefix + host + ":" + port + "/" + path, HttpMethod.PUT, requestEntity,
                String.class);
        } catch (RestClientException rce) {
            System.err.println("Rest client exception: " + rce.getMessage());
            rce.printStackTrace(System.out);
        }

        return response;
    }

    public ResponseEntity<String> savePath(String value, String type) {

        MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("path", value);
        values.add("ignorePath", type.equals("ignore"));
        return put("path/", values);
    }

    public HttpStatus deletePath(GlobalPath value) {


        if (value != null) {
            ResponseEntity<String> response = delete("/path", value.getPath());

            return response.getStatusCode();
        } else {
            return HttpStatus.BAD_REQUEST;
        }

    }


    public HttpStatus modifyPath(GlobalPath oldPath, String newIgnoreValue) {
        if (oldPath != null) {
            if (deletePath(oldPath) == HttpStatus.OK) {
                ResponseEntity<String> response = savePath(oldPath.getPath(), newIgnoreValue);
                if (response != null) {
                    return response.getStatusCode();
                } else {
                    return HttpStatus.SERVICE_UNAVAILABLE;
                }
            } else {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }

    public JSONObject getStatus() {

        ResponseEntity<String> response = get("scan/status");
        if (response != null) {
            Logger.getGlobal().log(Level.INFO, "response " + response.getStatusCode());

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                try {
                    String body = response.getBody();
                    JSONParser parser = new JSONParser();
                    JSONObject resp = (JSONObject) parser.parse(body);
                    if (resp.get("fileCount") != null && resp.get("progress") != null
                        && resp.get("timestamp") != null) {
                        return resp;
                    } else {
                        HashMap<String, String> error = new HashMap<String, String>();
                        error.put("message", "Response status format invalid");
                        Logger.getGlobal().log(Level.SEVERE, "Response status format invalid");
                        return new JSONObject(error);
                    }
                } catch (ParseException pe) {
                    HashMap<String, String> error = new HashMap<String, String>();
                    error.put("message", "Unable to parse server status");
                    Logger.getGlobal().log(Level.SEVERE, "Unable to parse server status");
                    return new JSONObject(error);
                }
            } else if (response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                HashMap<String, String> error = new HashMap<String, String>();
                error.put("message", "Scan already running");
                Logger.getGlobal().log(Level.WARNING, "Scan already running");
                return new JSONObject(error);
            } else {
                HashMap<String, String> error = new HashMap<String, String>();
                error.put("message", "Unknown error");
                Logger.getGlobal().log(Level.SEVERE, "Unknown error - Error code: " + response.getStatusCode());

                return new JSONObject(error);
            }
        } else {
            HashMap<String, String> error = new HashMap<String, String>();
            error.put("message", "Scan is not running");
            Logger.getGlobal().log(Level.WARNING, "Scan is not running");

            return new JSONObject(error);
        }
    }

    public HttpStatus startScan() {
        ResponseEntity<String> response = post("scan/start/", null);
        return response.getStatusCode();
    }

    public Object insertSchedule(LocalDateTime dateTime, Integer weekNumber, Integer monthNumber, String repetition) {

        MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        if (repetition.length() > 0) {
            if (weekNumber != null)
                values.add("weekly", weekNumber);
            else
                values.add("weekly", "");
            if (monthNumber != null)
                values.add("monthly", monthNumber);
            else
                values.add("monthly", monthNumber);
        }

        values.add("repeated", repetition.equals("One off"));
        values.add("timeStart", Timestamp.valueOf(dateTime).getTime());

        ResponseEntity<String> response = put("scheduler/", values);
        if (response != null) {
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                Logger.getGlobal().log(Level.WARNING, "Response is not of status code OK");
                return null;
            }
        } else {
            Logger.getGlobal().log(Level.SEVERE, "Response is null");
            return null;
        }
    }

    public HttpStatus addActions(LocalDateTime time, List<GlobalPath> actions) {

        Object response = insertSchedule(time, null, null, "One off");
        if (response != null) {

            try {
                JSONObject responseJSON = (JSONObject) parser.parse(response.toString());
                String schedulerId = responseJSON.get("id").toString();
                for (GlobalPath path : actions) {
                    Action action = path.getAction();
                    MultiValueMap<String, Object> values = new LinkedMultiValueMap<String, Object>();

                    values.add("type", action.getType().toString());
                    values.add("path", path.getPath());
                    values.add("newPath", action.getNewPath());
                    values.add("scheduler", schedulerId);

                    ResponseEntity<String> addResponse = put("action/", values);
                    if (addResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                        Notification.show("Unable to add action of: " + path.getPath(), Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        Logger.getGlobal().log(Level.WARNING, "Unable to add action of: " + path.getPath());
                    }
                }
                return HttpStatus.OK;

            } catch (ParseException e) {
                e.printStackTrace();
                Notification.show("Unable to parse server response", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                Logger.getGlobal().log(Level.SEVERE, "Unable to parse server response");
                return HttpStatus.BAD_REQUEST;
            }
        } else {
            Notification.show("Unable to create scheduler to add actions", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
            Logger.getGlobal().log(Level.SEVERE, "Unable to create scheduler to add actions - Response is null");
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public ResponseEntity<String> updatePassword(String newPassword) {
        MultiValueMap<String, Object> values = new LinkedMultiValueMap<String, Object>();
        values.add("oldPassword", this.password);
        values.add("newPassword", newPassword);
        return put("/account/password", values);
    }

    public ResponseEntity<String> updateUsername(String username) {
        MultiValueMap<String, Object> values = new LinkedMultiValueMap<String, Object>();
        values.add("password", this.password);
        values.add("newUsername", username);
        return put("/account/username", values);
    }
}
