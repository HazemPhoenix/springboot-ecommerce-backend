package io.spring.training.boot.server.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private Long jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(this.jwtSecret.getBytes());
    }

    @Override
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + this.jwtExpirationMs))
                .signWith(this.key)
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        return Jwts.parser().verifyWith(this.key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(this.key).build().parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e){
            return false;
        }
    }

    private boolean isTokenExpired(String token){
        Date expirationDate = Jwts.parser().verifyWith(this.key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expirationDate.before(new Date());
    }
}
