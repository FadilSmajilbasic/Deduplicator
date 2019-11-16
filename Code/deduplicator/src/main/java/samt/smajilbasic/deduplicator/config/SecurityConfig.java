package samt.smajilbasic.deduplicator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    private MyAuthenticationEntyPoint authenticationEntryPoint;

    @Autowired
    private AuthenticationDetailsRepository adr;

    @Override
    protected void configure(HttpSecurity http) throws Exception 
    {
        http
         .csrf().disable()
         .authorizeRequests().anyRequest().authenticated()
         .and()
         .httpBasic()
         .and()
         .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
    }
 
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) 
            throws Exception 
    {

        PasswordEncoder encoder = passwordEncoder();


        adr.findAll().forEach(user -> {
            String type = "USER";
            if (user.getUsername().equals("admin")) { 
                type = "ADMIN";
            }

            try {
                auth.inMemoryAuthentication().passwordEncoder(encoder)
                        .withUser(user.getUsername()) 
                        .password(user.getPassword()) 
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
}