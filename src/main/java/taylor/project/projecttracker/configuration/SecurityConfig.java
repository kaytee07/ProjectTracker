package taylor.project.projecttracker.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import taylor.project.projecttracker.security.auth.AuthenticationFilter;
import taylor.project.projecttracker.security.auth.JwtFilter;
import taylor.project.projecttracker.security.Handlers.CustomAccessDeniedHandlers;
import taylor.project.projecttracker.security.Handlers.CustomAuthenticationSuccessHandler;
import taylor.project.projecttracker.security.auth.CustomOAuth2User;
import taylor.project.projecttracker.service.AuditLogService;
import taylor.project.projecttracker.security.service.CustomOAuth2UserService;
import taylor.project.projecttracker.security.service.CustomUserDetailsService;
import taylor.project.projecttracker.security.util.JwtTokenUtil;

import java.util.Collections;
import java.util.List;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomAccessDeniedHandlers customAccessDeniedHandlers;
    private final AuditLogService auditLogService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Value("${app.jwt.secret}")
    private String signingKey;


    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomOAuth2UserService customOAuth2UserService,
                          JwtTokenUtil jwtTokenUtil,
                          CustomAccessDeniedHandlers customAccessDeniedHandlers,
                          AuditLogService auditLogService,
                          CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.customAccessDeniedHandlers = customAccessDeniedHandlers;
        this.auditLogService = auditLogService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .addFilterAt(new AuthenticationFilter(authenticationManager(), auditLogService, jwtTokenUtil),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(signingKey, jwtTokenUtil),
                        BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/v3/**","/api/auth/refresh-token", "/h2-console/**", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/projects", "/api/projects/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/tasks/{id}").hasRole("DEVELOPER")
                        .requestMatchers("/api/users", "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers("/api/projects/summaries").hasRole("CONTRACTOR")
                        .requestMatchers(EndpointRequest.to("health", "info", "prometheus","metrics", "threaddump")).permitAll()
                        .requestMatchers("/auth/login", "/api/auth/register", "/api/tasks/process").permitAll()
                        .anyRequest().authenticated())

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)).successHandler(customAuthenticationSuccessHandler)
                )
                .exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandlers))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            String jwtToken = jwtTokenUtil.generateToken(oauthUser);

            response.setContentType("application/json");
            response.getWriter().write(
                    String.format("{\"token\":\"%s\", \"email\":\"%s\", \"role\":\"%s\"}",
                            jwtToken,
                            oauthUser.getEmail(),
                            oauthUser.getAuthorities().iterator().next().getAuthority())
            );
        };
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ProviderManager authenticationManager() {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(Collections.singletonList(daoProvider));
    }


}
