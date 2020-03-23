package samt.smajilbasic.deduplicator.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;

/**
 * La classe SecurityConfig configura il webserver spring ad obbligare i client
 * a usare HTTPS e l'autenticazione BASIC. Usa
 * l'annotazione @{@link Configuration} per indicare a Spring che si tratta di
 * una classe che definisce delle configurazioni per Spring.
 */
@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * L'attributo authenticationEntryPoint definisce il punto d'entrata quando un
     * client su collega al webserver.
     */
    @Autowired
    private MyAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * L'attributo adr viene usato per interfacciarsi con la tabella
     * AuthenticationDetails del database.
     */
    @Autowired
    private AuthenticationDetailsRepository adr;

    /**
     * Il metodo configure imposta la configurazione del server. In questo metodo
     * viene impostata l'autenticazione BASIC e il requisito di usare HTTPS per ogni
     * richiesta. Se l'utente non è
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().anyRequest().authenticated() // richiesta autorizzazione per ogni
                                                                               // controller e ogni tipo di richiesta
                .and().httpBasic() // abilita autenticazione basic
                .and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)// definizione punto
                                                                                             // d'entrata in caso che
                                                                                             // l'utente non è
                                                                                             // autenticato
                .and().logout().clearAuthentication(true).logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/access/logout/success").invalidateHttpSession(true).deleteCookies("JSESSIONID");
        // .and().requiresChannel().anyRequest().requiresSecure(); // richiesta utilizzo
        // protocollo sicuro (TSL) per ogni tipo di
    }

    /**
     * Il metodo configureGlobal salva le credenziali dei utenti nella memoria
     * interna che saranno accessibili in tutto il progetto.
     * 
     * @param auth aiuta a creare un
     *             {@link org.springframework.security.authentication.AuthenticationManager}
     *             in modo semplice.
     * @throws Exception
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        try {
            auth.userDetailsService(getInMemoryUserDetailsManager());
            Logger.getGlobal().log(Level.INFO,
                    "userDetailsManager set");
        }catch (Exception ex){
            Logger.getGlobal().log(Level.SEVERE,
                    "Unable to set inMemoryUserDetailsManagerConfigurer ");
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager getInMemoryUserDetailsManager()
    {
        User.UserBuilder builder = User.builder();
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        adr.findAll().forEach(user -> {
                manager.createUser(builder.username(user.getUsername()).password(user.getPassword()).roles("ADMIN").build());
        });

        return manager;
    }
}