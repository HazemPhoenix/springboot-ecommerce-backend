package io.spring.training.boot.server.security.filters;

import io.spring.training.boot.server.exceptions.UnauthorizedException;
import io.spring.training.boot.server.security.services.JwtService;
import io.spring.training.boot.server.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtFilter(JwtService jwtService,
                     UserDetailsServiceImpl userDetailsService,
                     @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.exceptionResolver = exceptionResolver;
    }

    private final HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String email = null;
        String token = null;

        if(authHeader != null && !authHeader.trim().isEmpty()){
            token = extractTokenFromHeader(authHeader);
            try {
                email = jwtService.extractEmail(token);
            } catch (Exception exception){
                exceptionResolver.resolveException(request, response, null, new UnauthorizedException("Invalid or malformed token"));
                return;
            }
        }

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if(jwtService.isTokenValid(token)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    exceptionResolver.resolveException(request, response, null, new UnauthorizedException("Invalid or expired token"));
                }

            } catch (UsernameNotFoundException exception) {
                exceptionResolver.resolveException(request, response, null, new UnauthorizedException("Invalid token"));
                return;
            } catch (Exception e) {
                exceptionResolver.resolveException(request, response, null, new UnauthorizedException("Authorization failed"));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(String header){
        String bearer = "Bearer ";
        return header.trim().substring(header.indexOf(bearer) + bearer.length());
    }
}
