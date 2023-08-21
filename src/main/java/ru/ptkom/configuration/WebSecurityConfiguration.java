package ru.ptkom.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.ptkom.filter.CookieFilter;
import ru.ptkom.mapper.ActiveDirectoryGroupToRoleMapper;
import ru.ptkom.service.ConfigurationFIleService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@EnableWebSecurity
public class WebSecurityConfiguration {

    private static final Logger log = LoggerFactory.getLogger(WebSecurityConfiguration.class);

    public static final String viewApplicationURL = "localhost:8080";
    private final static String ACTIVE_DIRECTORY_URL_TEMPLATE = "ldap://%s:%s/";
    private String activeDirectoryAddress;
    private String activeDirectoryPort;
    private String activeDirectoryDomain;
    private Boolean webSecurityDebug;

    //private final UserDetailsService userDetailsService;
    private final ActiveDirectoryGroupToRoleMapper activeDirectoryGroupToRoleMapper;
    private final ConfigurationFIleService configurationFIleService;
    private final PasswordEncoder passwordEncoder;

    private final CookieFilter cookieFilter;

    public WebSecurityConfiguration(ActiveDirectoryGroupToRoleMapper activeDirectoryGroupToRoleMapper, ConfigurationFIleService configurationFIleService, PasswordEncoder passwordEncoder, CookieFilter cookieFilter) {
        this.activeDirectoryGroupToRoleMapper = activeDirectoryGroupToRoleMapper;
        //this.userDetailsService = userDetailsService;
        this.configurationFIleService = configurationFIleService;
        this.passwordEncoder = passwordEncoder;
        this.cookieFilter = cookieFilter;
        initializeConfigurationProperties();
        log.info("Security configuration got");
    }

    private void initializeConfigurationProperties() {
        this.webSecurityDebug = configurationFIleService.getSecurityDebug();
        this.activeDirectoryAddress = configurationFIleService.getActiveDirectoryIpAddress();
        this.activeDirectoryPort = configurationFIleService.getActiveDirectoryPort();
        this.activeDirectoryDomain = configurationFIleService.getActiveDirectoryDomain();
        this.webSecurityDebug = configurationFIleService.getSecurityDebug();
    }

    @Bean
    @DependsOn({"configurationFIleService"})
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(webSecurityDebug);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf()
                .disable();
        http.httpBasic()
                .disable();
        http
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(getRestAuthenticationEntryPoint(), getAntPathRequestMatcher());
        http
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/cdr").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/history/**").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/report/**").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/history/**").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/operator/**").hasRole("USER")
                .requestMatchers("/**").hasRole("ADMIN");
        http.addFilterAfter(cookieFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests().anyRequest().fullyAuthenticated();
        http.formLogin()
                .successHandler(appAuthenticationSuccessHandler())
                .failureHandler(appAuthenticationFailureHandler());

        return http
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://" + viewApplicationURL, "http://" + viewApplicationURL, "https://localhost:4200", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public HttpFirewall httpFirewall() {
        StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
        strictHttpFirewall.setAllowUrlEncodedDoubleSlash(true);
        return strictHttpFirewall;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(activeDirectoryDomain, String.format(ACTIVE_DIRECTORY_URL_TEMPLATE, activeDirectoryAddress, activeDirectoryPort));
        provider.setAuthoritiesMapper(activeDirectoryGroupToRoleMapper);
        return provider;
    }

//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
//        provider.setPasswordEncoder(passwordEncoder);
//        return provider;
//    }

    private AuthenticationEntryPoint getRestAuthenticationEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    private AntPathRequestMatcher getAntPathRequestMatcher() {
        return new AntPathRequestMatcher("/**");
    }

    @Bean
    public AuthenticationSuccessHandler appAuthenticationSuccessHandler(){
        return new AppAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler appAuthenticationFailureHandler(){
        return new AppAuthenticationFailureHandler();
    }

    public class AppAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
        protected void handle(HttpServletRequest request, HttpServletResponse response,
                              Authentication authentication) throws IOException, ServletException {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            response.setStatus(200);
            cookieFilter.setAuthoritiesCookie(response, authentication);
            log.info(String.format("User %s from %s is authenticated", username, request.getLocalAddr()));
        }

    }

    public class AppAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
        protected void handle(HttpServletRequest request, HttpServletResponse response,
                              Authentication authentication) throws IOException, ServletException {
            response.setStatus(401);
            log.warn(String.format("Authentication attempt from %s failed", request.getLocalAddr()));
        }
    }

}