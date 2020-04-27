package com.alpe.sap_access_service.security;

import com.alpe.sap_access_service.security.model.TokenAuthentication;
import com.alpe.sap_access_service.security.service.TokenService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.security.auth.login.CredentialException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class TokenAuthenticationFilter extends GenericFilterBean {

    private final TokenService tokenService;

    public TokenAuthenticationFilter(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        String token = ((HttpServletRequest) request).getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        token = token.substring(7);

        try {
            final TokenAuthentication authentication = tokenService.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (CredentialException ex) {
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(request, response);
    }
}
