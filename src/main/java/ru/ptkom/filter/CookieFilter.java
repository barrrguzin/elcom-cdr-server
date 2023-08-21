package ru.ptkom.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

@Component
public class CookieFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(CookieFilter.class);

    private final static String ROLES_KEY = "ROLES";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        setAuthoritiesCookie(response, authentication);
        filterChain.doFilter(request, response);
    }

    public HttpServletResponse setAuthoritiesCookie(HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(";"));
            response.addCookie(new Cookie(ROLES_KEY, Base64.getEncoder().encodeToString(authorities.getBytes(StandardCharsets.UTF_8))));
            log.info("Cookie set: " + authorities);
            return response;
        }
        return response;
    }
}