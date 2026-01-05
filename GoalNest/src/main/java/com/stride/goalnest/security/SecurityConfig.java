package com.stride.goalnest.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                // H2 Console
                .requestMatchers("/h2-console/**").permitAll()

                // Read-only access for everyone (including Engineers)
                .requestMatchers(HttpMethod.GET, "/").authenticated()
                .requestMatchers(HttpMethod.GET, "/employees", "/leaves", "/goals", "/followups", "/points", "/talkingpoints", "/ratings").authenticated()

                // Create forms and Save actions restricted to Manager/Scrum Master
                .requestMatchers("/employees/new", "/employees").hasAnyRole("MANAGER", "SCRUM_MASTER")
                .requestMatchers("/leaves/new", "/leaves").hasAnyRole("MANAGER", "SCRUM_MASTER")
                .requestMatchers("/goals/new", "/goals").hasAnyRole("MANAGER", "SCRUM_MASTER")
                .requestMatchers("/followups/new", "/followups").hasAnyRole("MANAGER", "SCRUM_MASTER")
                .requestMatchers("/points/new", "/points").hasAnyRole("MANAGER", "SCRUM_MASTER")
                .requestMatchers("/talkingpoints/new", "/talkingpoints").hasAnyRole("MANAGER", "SCRUM_MASTER")
                .requestMatchers("/ratings/new", "/ratings").hasAnyRole("MANAGER", "SCRUM_MASTER")

                // Any other request needs authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Fix for H2 console
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
