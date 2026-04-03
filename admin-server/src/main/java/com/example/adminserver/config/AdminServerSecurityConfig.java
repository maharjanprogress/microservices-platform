package com.example.adminserver.config;

import java.util.UUID;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableConfigurationProperties(AdminServerSecurityProperties.class)
public class AdminServerSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/assets/**", "/login").permitAll()
                        .requestMatchers("/actuator/**").hasAnyRole("ADMIN", "ACTUATOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(request -> "/logout".equals(request.getServletPath()))
                        .logoutSuccessUrl("/login?logout")
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/instances", "/actuator/**","/logout")
                )
                .rememberMe(rememberMe -> rememberMe.key(UUID.randomUUID().toString()));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    InMemoryUserDetailsManager userDetailsService(
            AdminServerSecurityProperties properties,
            PasswordEncoder passwordEncoder
    ) {
        UserBuilder users = User.builder().passwordEncoder(passwordEncoder::encode);

        return new InMemoryUserDetailsManager(
                users
                        .username(properties.getAdminUsername())
                        .password(properties.getAdminPassword())
                        .roles("ADMIN")
                        .build(),
                users
                        .username(properties.getActuatorUsername())
                        .password(properties.getActuatorPassword())
                        .roles("ACTUATOR")
                        .build()
        );
    }
}
