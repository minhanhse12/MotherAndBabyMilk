package com.motherandbabymilk.config;

import com.motherandbabymilk.entity.Users;
import com.motherandbabymilk.exception.AuthException;
import com.motherandbabymilk.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;
    @Qualifier("handlerExceptionResolver")
    @Autowired
    HandlerExceptionResolver resolver;
    private final List<String> AUTH_PERMISSIONS = List.of("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/api/login", "/api/register", "/api/request-reset-password","/reset-password", "/api/reset-password/**", "/api/products/get/**", "/api/products/getAll", "/api/categories/getAll", "/api/categories/get/**", "/api/articles/getAll", "/api/articles/get/**", "/api/brands/getAll", "/api/brands/get/**");

    public Filter() {
    }

    public boolean checkIsPublicAPI(String uri) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return this.AUTH_PERMISSIONS.stream().anyMatch((pattern) -> {
            return pathMatcher.match(pattern, uri);
        });
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean isPublicAAPI = this.checkIsPublicAPI(request.getRequestURI());
        if (isPublicAAPI) {
            filterChain.doFilter(request, response);
        } else {
            String token = this.getToken(request);
            if (token == null) {
                this.resolver.resolveException(request, response, (Object)null, new AuthException("Empty token!"));
                return;
            }

            Users user;
            try {
                user = this.tokenService.verifyToken(token);
            } catch (ExpiredJwtException var8) {
                this.resolver.resolveException(request, response, (Object)null, new AuthException("Expired token!"));
                return;
            } catch (MalformedJwtException var9) {
                this.resolver.resolveException(request, response, (Object)null, new AuthException("Invalid token!"));
                return;
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
            authenticationToken.setDetails((new WebAuthenticationDetailsSource()).buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        }

    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authHeader == null ? null : authHeader.substring(7);
    }
}