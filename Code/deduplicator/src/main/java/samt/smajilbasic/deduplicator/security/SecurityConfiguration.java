package samt.smajilbasic.deduplicator.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyBasicAuthenticationEntyPoint authenticationEntryPoint;

    @Autowired
    private AuthenticationDetailsRepository adr;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        adr.findAll().forEach(user -> {
            String type = "USER";
            if (user.getUsername().equals("admin")) {
                type = "ADMIN";
            }

            try {
                auth.inMemoryAuthentication().withUser(user.getUsername()).password(user.getPassword())
                        .roles(type);
            } catch (Exception e) {
                System.out.println("[ERROR] Unable to add user for http authentication: " + user.getUsername());
                e.printStackTrace();
            }
        }

        );

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // HTTP Basic authentication
                .httpBasic()
                .and()
                .authorizeRequests().anyRequest().hasAnyRole("USER","ADMIN")
                .and()
                .csrf().disable()
                .formLogin().disable()
                .logout()
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

    }
}