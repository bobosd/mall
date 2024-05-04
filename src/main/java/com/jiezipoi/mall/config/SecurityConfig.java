package com.jiezipoi.mall.config;

import com.jiezipoi.mall.filter.JwtAuthenticationTokenFilter;
import com.jiezipoi.mall.handler.AccessDeniedHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final ResourcePathConfig resourcePathConfig;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    public SecurityConfig(JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter, ResourcePathConfig resourcePathConfig, AuthenticationEntryPoint authenticationEntryPoint, AccessDeniedHandlerImpl accessDeniedHandler) {
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
        this.resourcePathConfig = resourcePathConfig;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
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
                                "/login",
                                "/signup",
                                "/goods/detail/**",
                                "/search/**",
                                "/user/login",
                                "/user/signup",
                                "/user/reset-password/**",
                                "/user/forgot-password",
                                "/user/activate-account/**").permitAll()
                        .requestMatchers(resourcePath).permitAll()
                        .anyRequest().authenticated());
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(config -> {
            config.authenticationEntryPoint(authenticationEntryPoint);
            config.accessDeniedHandler(accessDeniedHandler);
        });
        return http.build();
    }

    private String[] getResourcePathAsArray() {
        HashSet<String> resourcePath = resourcePathConfig.resourcePath();
        return resourcePath.stream().map(s -> s + "**").toArray(String[]::new);
    }
}
