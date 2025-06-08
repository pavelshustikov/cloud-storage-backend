package com.example.cloud_storage.config;

import com.example.cloud_storage.filter.AuthTokenFilter;
import com.example.cloud_storage.service.AuthService;
import jakarta.servlet.http.HttpServletResponse; // <-- Важно: это jakarta
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthService authService;

    public SecurityConfig(AuthService authService) {
        this.authService = authService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration,
            UserDetailsService userDetailsService, // <--- Инжектируем UserDetailsService здесь
            PasswordEncoder passwordEncoder // <--- Инжектируем PasswordEncoder здесь
    ) throws Exception {
        // Создаем и конфигурируем DaoAuthenticationProvider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Используем инжектированный UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder);       // Используем инжектированный PasswordEncoder

        // Получаем AuthenticationManager из AuthenticationConfiguration
        // И затем конфигурируем его с нашим DaoAuthenticationProvider
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
        // В Spring Security 6+ AuthenicationManager уже сам знает, какие AuthenticationProvider'ы использовать
        // Если возникает циклическая зависимость, это может быть из-за того, что
        // AuthenticationConfiguration пытается загрузить UserDetailsService, который является AuthService.
        // Чтобы разорвать это, мы можем воспользоваться @Lazy.
        return authenticationManager;
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(List.of("http://localhost:8080", "http://localhost:8081"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization", "auth-token"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public AuthTokenFilter authTokenFilter() { // <--- @Lazy здесь
        return new AuthTokenFilter(authService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Spring Security 6+ синтаксис
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Использование CorsFilter бина
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}