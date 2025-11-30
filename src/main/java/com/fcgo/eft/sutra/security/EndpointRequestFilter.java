package com.fcgo.eft.sutra.security;

import com.fcgo.eft.sutra.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class EndpointRequestFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authToken = request.getHeader("Authorization");
        if (authToken != null && !authToken.isEmpty()) {
            if (!authToken.startsWith("Bearer ")) {
                throw new UnauthorizedException();
            }
            Claims claims = jwtHelper.decodeToken(authToken.substring(7));
            if (claims == null) {
                throw new UnauthorizedException();
            }

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(AuthenticatedUser.builder()
                    .id(claims.get("A", String.class))
                    .username(claims.get("B", String.class))
                    .ipAddress(claims.get("C", String.class))
                    .paymentUser(claims.get("D", String.class))
                    .appName(claims.get("E", String.class))
                    .deploymentType(claims.get("F", String.class))
                    .build(), "EFT", new ArrayList<>()));
        }
        filterChain.doFilter(request, response);
    }
}