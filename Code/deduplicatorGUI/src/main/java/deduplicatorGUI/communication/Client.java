
package deduplicatorGUI.communication;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.hibernate.validator.internal.util.privilegedactions.GetResource;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // private final HttpClient httpClient;

    private String username;

    private String password;
    private InetAddress addr;
    RestTemplate restTemplate;

    private int port;
    private final String CA_PASS = "Password&1";

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
        try{
            URL jks = new URL("classpath:resources/deduplicator.jks");
            System.out.println("jks: " + jks.getPath());
        }catch(MalformedURLException ex){
            System.out.println("Excpe: "+ ex.getMessage());

        }
        /*HttpComponentsClientHttpRequestFactory requestFactory = null;
        try {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

            

            SSLContext sslContext =
            SSLContextBuilder.create()
            .loadKeyMaterial(ResourceUtils.getFile(jks), CA_PASS.toCharArray(), CA_PASS.toCharArray())
                .loadTrustMaterial(null, acceptingTrustStrategy)
            .build();

            HttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();

            requestFactory = new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);

        } catch (UnrecoverableKeyException | IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException | KeyManagementException  e) {

            System.out.println("Unable to create client: " + e.getMessage());
            e.printStackTrace();
        }
        if (requestFactory != null)
            restTemplate = new RestTemplate(requestFactory);
        */

    }

    public boolean isAuthenticated(InetAddress addr, int port) throws RestClientException {

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(createHeaders(false));
        ResponseEntity<String> response = restTemplate.exchange("https://" + addr.getHostName() + ":" + port + "/login/",
                HttpMethod.GET, requestEntity, String.class);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            this.addr = addr;
            setPort(port);
            return true;
        } else {
            return false;
        }

    }

    public Object get(String path) {

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(createHeaders(false));
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://" + addr.getHostName() + ":" + port + "/" + path, HttpMethod.GET, requestEntity,
                    String.class);

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                setPort(port);
                Object responseObj = new JSONParser().parse(response.getBody());
                return responseObj;

            } else {
                return null;
            }
        } catch (RestClientException rce) {
            System.out.println("[ERROR] Rest client exception: " + rce);
        } catch (ParseException pe) {
            System.err.println("[ERROR] Parse exception  : " + pe.getStackTrace());
        }
        return null;
    }

    public ResponseEntity<String> delete(String path, String param) {

        MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("path", param);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange("https://" + addr.getHostAddress() + ":" + port + "/path/",
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

        values = values == null ? new LinkedMultiValueMap<>() : values;

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange("https://" + addr.getHostAddress() + ":" + port + "/" + path,
                    HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException rce) {
            System.out.println("rce: " + rce.getMessage());
        }

        return response;
    }

    public Object put(String path, MultiValueMap<String, Object> values) {
        values = values == null ? new LinkedMultiValueMap<>() : values;

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;

        try {
            response = restTemplate.exchange("https://" + addr.getHostAddress() + ":" + port + "/" + path,
                    HttpMethod.PUT, requestEntity, String.class);
        } catch (RestClientException rce) {
            System.out.println("Rest client exception: " + rce.getMessage());
        }

        return response;
    }

    public ResponseEntity<String> insertPath(String pathText, boolean ignored) {
        MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("path", pathText);
        values.add("ignorePath", ignored);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(values, createHeaders(true));

        ResponseEntity<String> response = null;

        try {
            response = restTemplate.exchange("https://" + addr.getHostAddress() + ":" + port + "/path/", HttpMethod.PUT,
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
