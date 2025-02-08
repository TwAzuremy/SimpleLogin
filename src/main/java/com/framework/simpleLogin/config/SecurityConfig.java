package com.framework.simpleLogin.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.simpleLogin.filter.JwtAuthenticationTokenFilter;
import com.framework.simpleLogin.utils.Encryption;
import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.utils.ResponseEntity;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return "";
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                Map<String, String> separate = Gadget.StringUtils.separateCiphertext(encodedPassword);

                String ciphertext = Encryption.SHA256(rawPassword.toString() + separate.get("salt"));

                return ciphertext.equals(separate.get("ciphertext"));
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/users/register",
                                "/users/login",
                                "/exceptions/*",
                                "/email/send-register-captcha"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Defines a custom AuthenticationEntryPoint bean that handles unauthorized access attempts.
     * <p>
     * When an authentication exception occurs, this entry point returns a JSON error response
     * with a 401 Unauthorized status code.
     *
     * @return a custom AuthenticationEntryPoint bean
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            // Sets the HTTP response status to 401 Unauthorized.
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            // Sets the response content type to JSON.
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            // Writes a serialized error response to the response body.
            response.getWriter().write(responseSerializer(authException.getMessage()));
        };
    }

    /**
     * Creates a custom AccessDeniedHandler bean that handles access denied exceptions in the application.
     *
     * @return a custom AccessDeniedHandler instance
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(responseSerializer(accessDeniedException.getMessage()));
        };
    }

    private String responseSerializer(String message) throws JsonProcessingException {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED, message, null);

        return new ObjectMapper().writeValueAsString(responseEntity);
    }
}
