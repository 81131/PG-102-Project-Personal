package com.goldenflame.pg102.config;

import com.goldenflame.pg102.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    // IMPORTANT: This is for testing only. We will replace this with a real hash encoder later.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow public access to static resources AND our public pages
                        .requestMatchers(
                                "/", "/menu/**", "/events", "/login", "/register",
                                "/css/**", "/js/**", "/images/**", "/item/**"
                        ).permitAll()

                        // Require authentication for all OTHER pages
                        .requestMatchers("/admin/**").hasRole("MANAGER")
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/kitchen/catalogue/**").hasAnyRole("KITCHEN_SUPERVISOR", "EVENT_COORDINATOR")
                        .requestMatchers("/kitchen/**").hasAnyRole("KITCHEN_SUPERVISOR", "KITCHEN_STAFF")
                        .requestMatchers("/order/my-history").authenticated()
                        .requestMatchers("/api/notifications/**", "/notifications").authenticated()
                        .requestMatchers("/delivery/**").hasRole("DELIVERY_PERSON")
                        .requestMatchers("/kitchen/catalogue/**").hasAnyRole("KITCHEN_SUPERVISOR", "EVENT_COORDINATOR")
                        .requestMatchers("/events/manage/**").hasRole("EVENT_COORDINATOR")
                        .requestMatchers("/events/book/**").authenticated()
                        .requestMatchers("/events/manage/**").hasRole("EVENT_COORDINATOR")
                        .requestMatchers("/orders/**").authenticated()
                        .requestMatchers("/reviews/submit").authenticated()
                        .requestMatchers("/cart/**").authenticated()
                        .anyRequest().authenticated()

                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );


        return http.build();
    }

}