package samt.smajilbasic.deduplicator.config;

import org.springframework.stereotype.Component;

import samt.smajilbasic.deduplicator.exception.Message;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;;

/**
 * MyAuthenticationEntyPoint
 */
@Component
public class MyAuthenticationEntyPoint extends BasicAuthenticationEntryPoint{

    @Override
    public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx) 
      throws IOException, ServletException {
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
        response.addHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        Message message =  new Message(HttpStatus.UNAUTHORIZED,"Error message: " + authEx.getMessage());
        Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();
        
        writer.write(encoder.getObjectMapper().writeValueAsString(message));
    }
 
    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("Deduplicator");
        super.afterPropertiesSet();
    }

    
}