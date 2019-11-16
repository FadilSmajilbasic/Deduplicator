package samt.smajilbasic.deduplicator.security;
import org.springframework.stereotype.Component;

import samt.smajilbasic.deduplicator.exception.Message;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;;

/**
 * MyAuthenticationEntyPoint
 */
@Component
public class MyBasicAuthenticationEntyPoint extends BasicAuthenticationEntryPoint{

    @Override
    public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx) 
      throws IOException, ServletException {
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        Message message =  new Message(HttpStatus.UNAUTHORIZED,"Error message: " + authEx.getMessage());
        ObjectMapper mapper = new ObjectMapper();
        writer.write(mapper.writeValueAsString(message));
    }
 
    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("Deduplicator");
        super.afterPropertiesSet();
    }

    
}