package io.spring.training.boot.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security){
        return security
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST,"/api/v1/users/register").permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .logout(withDefaults())
                .formLogin(withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
}
