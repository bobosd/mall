package com.jiezipoi.mall.config;

import com.jiezipoi.mall.filter.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashSet;

@Configuration
public class SecurityConfig {
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final ResourcePathConfig resourcePathConfig;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter, ResourcePathConfig resourcePathConfig, AuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
        this.resourcePathConfig = resourcePathConfig;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] resourcePath = getResourcePathAsArray();
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(
                                "/",
                                "/favicon.ico",
                                "/admin/**",
                                "/login",
                                "/signup",
                                "/user/login",
                                "/user/signup",
                                "/user/activate-account/**").permitAll()
                        .requestMatchers(resourcePath).permitAll()
                        .anyRequest().authenticated());
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(config -> config.authenticationEntryPoint(authenticationEntryPoint));
        return http.build();
    }

    private String[] getResourcePathAsArray() {
        HashSet<String> resourcePath = resourcePathConfig.resourcePath();
        return resourcePath.stream().map(s -> s + "**").toArray(String[]::new);
    }
}
