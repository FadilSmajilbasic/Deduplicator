package samt.smajilbasic.deduplicator.config;

import org.springframework.stereotype.Component;

import samt.smajilbasic.deduplicator.exception.Response;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;;

/**
 * MyAuthenticationEntryPoint
 */
@Component
public class MyAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
        response.addHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.write("{message:Error message " + authEx.getMessage() + "}");
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("Deduplicator");
        super.afterPropertiesSet();
    }

}