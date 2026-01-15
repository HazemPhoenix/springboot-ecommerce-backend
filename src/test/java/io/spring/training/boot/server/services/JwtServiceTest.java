package io.spring.training.boot.server.services;

import io.spring.training.boot.server.security.services.JwtService;
import io.spring.training.boot.server.security.services.JwtServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JwtServiceTest {
    @Autowired
    private JwtServiceImpl jwtService;

    @Test
    public void testTokenCreationAndValidationAndEmailExtraction() {
        String email = "test@test.com";
        String token = jwtService.generateToken(email);

        assertThat(token).isNotNull();
        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
        assertThat(jwtService.isTokenValid(token)).isEqualTo(true);
    }

    @Configuration
    static class testConfig {
        @Bean
        public JwtService jwtService(){
            return new JwtServiceImpl();
        }
    }

}
