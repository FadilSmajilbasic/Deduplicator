/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deduplicatorGUI.communication;

import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
    
    public Client(String username, String password, String ip, int port) {
        Authenticator auth = Authenticator.getDefault() ;
        auth.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                username, password.toCharArray());
                    }
                });
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .authenticator(auth.getDefault())
                .build();
        
    }
    
    private static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
    
    public boolean isAuthenticated(){
        try {
            
            var request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/login"))
                    .GET()
                    .build();
                        
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            ObjectMapper encoder = new ObjectMapper();
            Object asd = new Object();
            Map<String,List<String>> resp = response.headers().map();
            List<String> asdd = resp.getOrDefault("message", new ArrayList<>());
            
            asdd.forEach(kek ->{
                System.out.println("Header: " + kek.toString());
            });
            
            asd = encoder.readValue(response.body(), Object.class);
            
            System.out.println(response.body());
            return true;
            
        } catch (URISyntaxException ex) {
            System.out.println("deduplicatorGUI.communication.Client.getAll()");
            
        } catch (InterruptedException ie) {
            System.out.println("interr" + ie.getMessage());
        } catch (IOException ioe) {
            System.err.println("ioe" + ioe.getMessage());
        }
        return false;
    }
    
    public Object getAll(String path) {
        try {
            
            var request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/" + path))
                    .GET()
                    .build();
                        
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            ObjectMapper encoder = new ObjectMapper();
            Object test;
            test = encoder.readValue(response.body(), Object.class);
            
            System.out.println(response.body());
            
        } catch (URISyntaxException ex) {
            System.out.println("deduplicatorGUI.communication.Client.getAll()");
            
        } catch (InterruptedException ie) {
            System.out.println("interr" + ie.getMessage());
        } catch (IOException ioe) {
            System.err.println("ioe" + ioe.getMessage());
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
