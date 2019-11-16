package samt.smajilbasic.deduplicator.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer.UserDetailsBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.servlet.DispatcherServlet;

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


        PasswordEncoder encoder = passwordEncoder();

        adr.findAll().forEach(user -> { // get every user form the database
            String type = "USER"; // set type of user
            if (user.getUsername().equals("admin")) { // check if they are admin
                type = "ADMIN";
            }

            try {
                auth.inMemoryAuthentication().passwordEncoder(encoder)
                        .withUser(user.getUsername()) // add user to the authentication memory
                        .password(encoder.encode(user.getPassword())) // add user's password 
                        .roles(type); // set user type

                System.out.println("user: " + user.getUsername() + " pass: " + user.getPassword());

            } catch (Exception e) {
                System.out.println("[ERROR] Unable to add user for http authentication: " + user.getUsername());
                e.printStackTrace();
            }

        }

        );

        try {
            System.out.println("Stored: " + auth.inMemoryAuthentication().getUserDetailsService().userExists("admin")); // user not found
        } catch (UsernameNotFoundException e) {
            System.out.println("[ERROR] UsernameNotFoundException " );

        } catch (Exception e) {
            System.out.println("[ERROR] Exception: " );

        }

    }

    /**
     * Used to encode the user password, currently unused because I'm testing with plain text passwords
     * If setup as bean, Spring will detect the password encryption type automatically
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().anyRequest().authenticated() // check if request is authorized on any request, any type of use can access any request 
                .and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint) // on exception use authenticationEntryPoint to make the response
                .and()
                .httpBasic(); // HTTP Basic authentication

    }
}