
package samt.smajilbasic.communication;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

import javax.net.ssl.SSLContext;

/**
 *
 * @author Fadil Smajilbasic
 */
public class Client {

    private String username;

    private String password;
    private String host;
    RestTemplate restTemplate;

    private int port;
    private final String CA_PASS = "Password&1";
    private KeyStore keyStore;

    public enum loginResponse {
        CREDENTIALS,
        SERVER,
        OK
    }

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

    /**
     * Il costruttore che riceve il username e la password per l'autenticazione
     * basic che verrà utilizzata in tutte le GUI. Il costruttore tenta di caricare
     * 
     * @param username il username da impostare.
     * @param password la password da impostare.
     */
    public Client(String username, String password) {
        this.username = username;
        this.password = password;

        /*
         * InputStream in =
         * getClass().getResourceAsStream("../resource/deduplicator.p12"); try{ KeyStore
         * keyStore = KeyStore.getInstance("PKCS12"); keyStore.load(in,
         * CA_PASS.toCharArray()); }catch(IOException e){ StackTraceElement[] output =
         * e.getStackTrace(); for (StackTraceElement element : output) {
         * System.out.println(element.toString()); } } catch (KeyStoreException e) {
         * e.printStackTrace(System.out); } catch (NoSuchAlgorithmException e) {
         * e.printStackTrace(System.out); } catch (CertificateException e) {
         * e.printStackTrace(System.out); }
         */
        HttpComponentsClientHttpRequestFactory requestFactory = null;
        // try {
        /*
         * TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String
         * authType) -> true; SSLContext sslContext = SSLContextBuilder.create()
         * .loadKeyMaterial(keyStore, CA_PASS.toCharArray()) .loadTrustMaterial(null,
         * acceptingTrustStrategy) .build();
         * 
         * .setSSLContext(sslContext)
         */
        HttpClient httpClient = HttpClients.custom().build();

        requestFactory = new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);

        /*
         * } catch (UnrecoverableKeyException | NoSuchAlgorithmException |
         * KeyStoreException | KeyManagementException e) {
         * 
         * System.out.println("Unable to create client: " + e.getMessage());
         * e.printStackTrace(); }
         */
        if (requestFactory != null)
            restTemplate = new RestTemplate(requestFactory);
    }

    public loginResponse isAuthenticated(String host, int port) throws RestClientException {

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(createHeaders(false));
        ResponseEntity<String> response = null;

        try {
            response = restTemplate.exchange("http://" + host + ":" + port + "/login/", HttpMethod.GET, requestEntity,
                    String.class);
        } catch (RestClientException rce) {
            System.err.println("[ERROR] Client exception: " + rce.getMessage());
        }

        if (response != null) {
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                this.host = host;
                setPort(port);
                return loginResponse.OK;
            } else {
                return loginResponse.CREDENTIALS;
            }
        }
        return loginResponse.SERVER;

    }

    public ResponseEntity<String> get(String path) {

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(createHeaders(false));
        try {
            ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":" + port + "/" + path,
                    HttpMethod.GET, requestEntity, String.class);

            if (response.getStatusCode().equals(HttpStatus.OK)) {

                return response;

            } else {
                return null;
            }
        } catch (RestClientException rce) {
            System.out.println("[ERROR] Rest client exception: " + rce);
        }
        return null;
    }

    public ResponseEntity<String> delete(String path, String param) {

        MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("path", param);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange("http://" + host + ":" + port + "/path/", HttpMethod.DELETE, requestEntity,
                    String.class);
        } catch (RestClientException rce) {
            System.out.println("rce: " + rce.getMessage());
        }

        if (response != null) {
            return response;
        } else {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
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

    public ResponseEntity<String> post(String path, MultiValueMap<String, Object> values) {

        values = values == null ? new LinkedMultiValueMap<>() : values;

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange("http://" + host + ":" + port + "/" + path, HttpMethod.POST, requestEntity,
                    String.class);
        } catch (RestClientException rce) {
            System.out.println("rce: " + rce.getMessage());
        }
        return response;
    }

    public ResponseEntity<String> put(String path, MultiValueMap<String, Object> values) {
        values = values == null ? new LinkedMultiValueMap<>() : values;

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;

        try {
            response = restTemplate.exchange("http://" + host + ":" + port + "/" + path, HttpMethod.PUT, requestEntity,
                    String.class);
        } catch (RestClientException rce) {
            System.out.println("Rest client exception: ");
            StackTraceElement[] st = rce.getStackTrace();
            for (StackTraceElement stackTraceElement : st) {
                System.out.println(stackTraceElement.toString());
            }
        }

        return response;
    }

}