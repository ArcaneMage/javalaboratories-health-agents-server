package org.javalaboratories.healthagents.configuration;

import org.javalaboratories.healthagents.repository.MonitorRepository;
import org.javalaboratories.healthagents.service.MonitorDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

/**
 * Configures service to accept HTTPS connections, and applies specific
 * security/access control roles to service endpoints.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String AGENT_REALM = "security-monitoring@aws.langoustine.gilded.net";
    private static final String DIGEST_KEY = "{449823}";

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Autowired
    private MonitorDetailsService monitorService;

    @Autowired
    private ServiceConfiguration configuration;

    /**
     * @return PasswordEncoder implementation
     */
    @Bean
    public PasswordEncoder encoder() {
        // Using NoOpPasswordEncoder is fine for the following reasons:
        //     (1) Using Digest Authentication strategy (MD5 encryption)
        //     (2) Requests are sent via HTTPS only
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * @return Constructs DaoAuthorisationProvider, encapsulates user details
     * service and password encoder.
     */
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(monitorService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    /**
     * Configures authentication management.
     * <p>
     * Sets up the authentication manager with the current user details service
     * (monitor).
     * @param auth AuthorisationBuilder object to undergo state mutation: sets the
     *             authenticationProvider object.
     * @throws Exception If problem encountered when configuring authentication
     * manager.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
        auth.eraseCredentials(true);
    }

    /**
     * Configures HttpSecurity object.
     *
     * @param http HttpSecurity http mutable object that undergoes configuration
     *             to enable HTTPS/SSL; {@code health} endpoints require
     *             {@code ROLE_MONITOR} privileges; both
     *             {@code DigestAuthenticationFilter} and
     *             {@code RsaSecureIdAuthenticationFilter} are enabled for REST
     *             API security.
     * @throws Exception If problem encountered when configuring HttpSecurity
     * object.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/api/agents/**")
                .csrf()
                .disable()
                .cors()
                .and()

                // Enable HTTPS/SSL
                .requiresChannel()
                .anyRequest()
                .requiresSecure()
                .and()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // Authorisation
                .authorizeRequests()
                .antMatchers("/ftp/health").hasRole(MonitorRepository.ROLE_MONITOR)
                .antMatchers("/secure-traffic/health").hasRole(MonitorRepository.ROLE_MONITOR)
                .antMatchers("/service/health").hasRole(MonitorRepository.ROLE_MONITOR)
                .and()

                .anonymous().disable()
                .exceptionHandling(e -> e.authenticationEntryPoint(getDigestEntryPoint()))

                .addFilterBefore(getDigestAuthFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(getRsaAuthenticationFilter(), DigestAuthenticationFilter.class);

        logger.info("Configured HTTP security, monitoring endpoints configured correctly");
    }

    // Configure DigestAuthenticationEntryPoint
    private DigestAuthenticationEntryPoint getDigestEntryPoint() {
        DigestAuthenticationEntryPoint entryPoint = new DigestAuthenticationEntryPoint();
        entryPoint.setRealmName(AGENT_REALM);
        entryPoint.setKey(DIGEST_KEY);
        entryPoint.setNonceValiditySeconds(10);
        return entryPoint;
    }

    // Configure  DigestAuthenticationFilter.
    // Notice the we are injecting userDetailsService and DigestAuthenticationEntryPoint
    private DigestAuthenticationFilter getDigestAuthFilter() throws Exception {
        DigestAuthenticationFilter filter = new DigestAuthenticationFilter();
        filter.setUserDetailsService(monitorService);
        filter.setAuthenticationEntryPoint(getDigestEntryPoint());
        return filter;
    }

    private RsaSecureIdAuthenticationFilter getRsaAuthenticationFilter() throws Exception {
        return new RsaSecureIdAuthenticationFilter(configuration.rsaSecurity());
    }
}