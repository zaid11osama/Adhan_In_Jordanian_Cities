package com.arabbank.hdf.uam.brain.security;

import com.arabbank.hdf.uam.brain.validation.ad.AdUserEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final LdapService ldapService;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        AuthHeaderData authHeaderData = extractAuthHeaderData(request)
                .orElse(null);

        if (authHeaderData != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            AdUserEntry adUserEntry = ldapService.getUserInfo(authHeaderData.username)
                    .orElse(null);

            if (adUserEntry != null && jwtUtil.validateToken(authHeaderData.jwt, adUserEntry.getSAMAccountName())) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        adUserEntry, null, null
                );
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

    @NotNull
    private Optional<AuthHeaderData> extractAuthHeaderData(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            return Optional.of(new AuthHeaderData(username, jwt));
        }
        return Optional.empty();
    }

    @RequiredArgsConstructor
    private static class AuthHeaderData {
        public final String username;
        public final String jwt;
    }
}

