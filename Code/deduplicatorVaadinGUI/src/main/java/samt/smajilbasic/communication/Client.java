
package samt.smajilbasic.communication;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.jboss.resteasy.core.ExceptionAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

import samt.smajilbasic.entity.Report;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.entity.Action;
import samt.smajilbasic.entity.GlobalPath;
import samt.smajilbasic.properties.Settings;

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
@Component(value = "connectionClient")
public class Client {

    private String username;
    private String password;
    private String host;
    private RestTemplate restTemplate;

    private int port;
    private KeyStore keyStore;
    private static final String prefix = "https://";

    private Settings settings = new Settings();

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        if (port > 0 && port < 65535) {
            this.port = port;
        } else {
            this.port = 8443;
        }
    }

    JSONParser parser = new JSONParser();

    public Client() {
    }

    /**
     * Il costruttore che riceve il username e la password per l'autenticazione
     * basic che verrà utilizzata in tutte le GUI.
     *
     * @param username il username da impostare.
     * @param password la password da impostare.
     */
    public boolean init(String username, String password) throws Exception {
        this.username = username;
        this.password = password;

        try {
            FileInputStream in = new FileInputStream(new File("deduplicator.p12"));
            String caPassword = new String(Base64.getDecoder().decode(settings.getCaPassword()));
            try {
                keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(in, caPassword.toCharArray());
            } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to load SSL key into HTTPS client ");
                e.printStackTrace(System.out);
                throw new Exception("Invalid password for SSL certificate");
            }
            try {
                TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
                SSLContext sslContext = SSLContextBuilder.create().loadKeyMaterial(keyStore, caPassword.toCharArray())
                    .loadTrustMaterial(null, acceptingTrustStrategy).build();

                HttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();

                HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

                requestFactory.setHttpClient(httpClient);
                restTemplate = new RestTemplate(requestFactory);
                return true;

            } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException
                | KeyManagementException e) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to create client: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (FileNotFoundException fne) {
            Logger.getGlobal().log(Level.SEVERE, "CA certificate not found");
            return false;
        }
    }

    public HttpStatus isAuthenticated(String host, int port) throws RestClientException {

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(createHeaders(false));
        ResponseEntity<String> response = null;

        try {
            response = restTemplate.exchange(prefix + host + ":" + port + "/access/login/", HttpMethod.GET,
                requestEntity, String.class);
        } catch (RestClientException rce) {
            Logger.getGlobal().log(Level.SEVERE, "Rest client exception: " + rce.getMessage());
            if (rce.getMessage().contains("Connection refused")) {
                return HttpStatus.SERVICE_UNAVAILABLE;
            } else if (rce.getMessage().startsWith("I/O error on GET request")) {
                return HttpStatus.EXPECTATION_FAILED;
            } else if (rce.getMessage().strip().startsWith("401")) {
                return HttpStatus.UNAUTHORIZED;
            }
        }

        if (response != null) {
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                this.host = host;
                setPort(port);
            }
            return response.getStatusCode();
        } else {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }

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

    public ResponseEntity<String> delete(String path, MultiValueMap<String, Object> values) {
        HttpEntity<MultiValueMap<String, Object>> requestEntity;
        if (values != null)
            requestEntity = new HttpEntity<>(values, createHeaders(true));
        else {
            values = new LinkedMultiValueMap<>();
            requestEntity = new HttpEntity<>(values, createHeaders(false));
        }
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(prefix + host + ":" + port + "/" + path, HttpMethod.DELETE, requestEntity,
                String.class);
        } catch (RestClientException rce) {
            Logger.getGlobal().log(Level.SEVERE, "Rest Client Exception: " + rce.getMessage());
        }

        return Objects.requireNonNullElseGet(response, () -> new ResponseEntity<String>(HttpStatus.BAD_REQUEST));

    }

    public ResponseEntity<String> post(String path, MultiValueMap<String, Object> values) {
        values = values == null ? new LinkedMultiValueMap<>() : values;
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(prefix + host + ":" + port + "/" + path, requestEntity,
                String.class);
        } catch (RestClientException rce) {
            try {
                JSONObject resp = (JSONObject) parser.parse(rce.getMessage().split(" : ")[1].replace("[", "").replace("]", ""));
                Logger.getGlobal().log(Level.SEVERE, "Rest Client Exception: " + rce.getMessage());
                return new ResponseEntity<>(String.valueOf(resp.get("message")), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch ( Exception pe) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to parse error message: " + pe.getMessage());
            }
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
            try {
                JSONObject resp = (JSONObject) parser.parse(rce.getMessage().split(" : ")[1].replace("[", "").replace("]", ""));
                if (resp.get("message") != null) {
                    Logger.getGlobal().log(Level.SEVERE, "Rest client exception with error: " + resp.get("message"));
                    return new ResponseEntity<>(String.valueOf(resp.get("message")), HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    Logger.getGlobal().log(Level.SEVERE, "Rest client exception: unable to parse exception message value");
                }
            } catch (ParseException | ClassCastException pe) {
                Logger.getGlobal().log(Level.SEVERE, "Rest client exception: unable to parse exception message");
            }
            Logger.getGlobal().log(Level.SEVERE, "Rest client exception: general error");
        }

        return response;
    }

    public ResponseEntity<String> savePath(String path, String type) {

        MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("path", path);
        values.add("ignorePath", type.equals("ignore"));
        return put("path/", values);
    }

    public HttpStatus deletePath(GlobalPath value) {

        if (value != null) {
            MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
            values.add("path", value.getPath());
            ResponseEntity<String> response = delete("path/", values);
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
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                try {
                    String body = response.getBody();
                    JSONObject resp = (JSONObject) parser.parse(body);
                    if (resp.get("fileCount") != null && resp.get("progress") != null && resp.get("timestamp") != null
                        && resp.get("totalFiles") != null) {
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

    public ResponseEntity<String> insertSchedule(LocalDateTime dateTime, String weekNumber, String monthNumber,
                                                 String repetition) {

        MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("weekly", Objects.requireNonNullElse(weekNumber, ""));
        values.add("monthly", Objects.requireNonNullElse(monthNumber, ""));
        values.add("repeated", !repetition.equals("One off"));
        values.add("timeStart", Timestamp.valueOf(dateTime).getTime());

        ResponseEntity<String> response = put("scheduler/", values);
        if (response != null) {
            if (response.getStatusCode() == HttpStatus.OK) {
                return response;
            } else {
                Logger.getGlobal().log(Level.WARNING, "Response has not status code OK");
                return null;
            }
        } else {
            Logger.getGlobal().log(Level.SEVERE, "Response is null");
            return null;
        }
    }

    public HttpStatus addActions(LocalDateTime time, List<GlobalPath> actions, String schedulerId) {
        if (schedulerId == null) {
            ResponseEntity<String> response = insertSchedule(time, null, null, "One off");
            try {
                JSONObject responseJSON = (JSONObject) parser.parse(response.getBody());
                schedulerId = responseJSON.get("schedulerId").toString();
            } catch (ParseException e) {
                Notification.show("Unable to parse server response", settings.getNotificationLength(),
                    Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                Logger.getGlobal().log(Level.SEVERE, "Unable to parse server response");
                return HttpStatus.BAD_REQUEST;
            } catch (NullPointerException npe) {
                Notification
                    .show("Unable to add action - insertScheduler response is null",
                        settings.getNotificationLength(), Notification.Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                Logger.getGlobal().log(Level.SEVERE, "Unable to add action - insert scheduler response is null ");
                return HttpStatus.BAD_REQUEST;
            }
        }
        for (GlobalPath path : actions) {
            Action action = path.getAction();
            ResponseEntity<String> response = insertAction(action.getType(), path.getPath(), action.getNewPath(),
                schedulerId);
            if (response != null) {
                Logger.getGlobal().log(Level.INFO,
                    "Added action " + path.getPath() + " status: " + response.getStatusCode());
                if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                    Notification.show("Unable to add action of: " + path.getPath(), settings.getNotificationLength(),
                        Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    Logger.getGlobal().log(Level.WARNING, "Unable to add action of: " + path.getPath());
                }
            } else {
                Logger.getGlobal().log(Level.SEVERE,
                    "No response from server - unable to add action of " + path.getPath());

            }

        }
        return HttpStatus.OK;
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

    public ResponseEntity<String> insertAction(String type, String path, String newPath, String scheduler) {
        MultiValueMap<String, Object> values = new LinkedMultiValueMap<String, Object>();

        values.add("type", type);
        values.add("path", path);
        values.add("newPath", newPath);
        values.add("scheduler", scheduler);

        return put("action/", values);

    }

    public ResponseEntity<String> insertScheduledScan(LocalDateTime dateTime, String weekNumber, String monthNumber,
                                                      String repetition) {
        ResponseEntity<String> response = insertSchedule(dateTime, weekNumber, monthNumber, repetition);
        if (response != null) {
            ResponseEntity<String> responseEntity = response;
            try {
                JSONObject object = (JSONObject) parser.parse(responseEntity.getBody());
                String schedulerId = object.get("schedulerId").toString();
                Logger.getGlobal().log(Level.INFO, "scheduler id " + schedulerId);
                return insertAction("SCAN", null, null, schedulerId);
            } catch (ParseException pe) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to parse response from server");
                Notification.show("Unable to parse response from server", settings.getNotificationLength(),
                    Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return null;
            }
        } else {
            Logger.getGlobal().log(Level.SEVERE, "Unable to insert scheduler");
            Notification
                .show("Unable to insert scheduler", settings.getNotificationLength(), Notification.Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return null;
        }
    }

    public ResponseEntity<String> addUser(String username, String password) {
        MultiValueMap<String, Object> values = new LinkedMultiValueMap<String, Object>();

        values.add("username", username);
        values.add("password", password);

        return put("account/", values);
    }

    public Report getReport(String reportId) {
        ResponseEntity<String> response = get("/report/" + reportId);

        if (response != null) {
            try {
                JSONObject body = (JSONObject) parser.parse(response.getBody());
                Report report = new Report(Objects.requireNonNullElse(body.get("user"), "unknown").toString());
                report.setAverageDuplicateCount(Float.parseFloat(body.get("averageDuplicateCount").toString()));
                report.setFilesScanned(Integer.parseInt(body.get("filesScanned").toString()));
                report.setStart(Long.parseLong(body.get("start").toString()));
                report.setDuration(Long.parseLong(body.get("duration").toString()));
                report.setId(Integer.parseInt(body.get("id").toString()));
                return report;
            } catch (ParseException | NumberFormatException | NullPointerException pe) {
                Notification.show("Unable to parse response from server", settings.getNotificationLength(), Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                Logger.getGlobal().log(Level.SEVERE, "Unable to parse response from server: " + pe.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }
}
