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

/**
 * La classe SecurityConfig configura il webserver spring ad obbligare i client a usare HTTPS e l'autenticazione BASIC.
 * Usa l'annotazione @{@link Configuration} per indicare a Spring che si tratta di una classe che definisce delle configurazioni per Spring.
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    /**
     * L'attributo authenticationEntryPoint definisce il punto d'entrata quando un client su collega al webserver.
     */
    @Autowired
    private MyAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * L'attributo adr viene usato per interfacciarsi con la tabella AuthenticationDetails del database.
     */
    @Autowired
    private AuthenticationDetailsRepository adr;

    /**
     * Il metodo configure imposta la configurazione del server.
     * In questo metodo viene impostata l'autenticazione BASIC e il requisito di usare HTTPS per ogni richiesta.
     * Se l'utente non è 
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception 
    {
        http
         .csrf().disable()
         .authorizeRequests().anyRequest().authenticated() // richiesta autorizzazione per ogni controller e ogni tipo di richiesta
         .and()
         .httpBasic()  //abilita autenticazione basic
         .and()
         .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint) // definizione punto d'entrata in caso che l'utente non è autenticato
         ;//.and().requiresChannel().anyRequest().requiresSecure(); // richiesta utilizzo protocollo sicuro (TSL) per ogni tipo di
    }
 
    /**
     * Il metodo configureGlobal salva le credenziali dei utenti nella memoria interna che saranno accessibili in tutto il progetto.
     * @param auth aiuta a creare un {@link org.springframework.security.authentication.AuthenticationManager} in modo semplice.
     * @throws Exception 
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
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
        });
    }

    /**
     * Il metodo passwordEncoder ritorna il encoder che verrà utilizzzato per le password.
     * @return il password encoder impostato ({@link BCryptPasswordEncoder})
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}