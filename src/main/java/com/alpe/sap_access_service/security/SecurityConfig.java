package com.alpe.sap_access_service.security;

import com.alpe.sap_access_service.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired UsersService usersService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // Disable CORS & CSRF protection
                .cors()
                .and()
                .csrf().disable()
                .headers().frameOptions().disable()
                // Require TLS
//                .and()
//                .requiresChannel()
//                .anyRequest().requiresSecure()
                .and()
                // Add custom authentication filter
                .addFilterAfter(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // Allow access to auth methods for all & protect all other
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/systems").permitAll()
                .antMatchers(HttpMethod.POST, "/auth").permitAll()
                // Allow non authenticated access to DB console
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                // Stateless HTTP sessions (for REST architecture)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
    }

    @Bean(name = "tokenAuthenticationFilter")
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenService());
    }

    @Bean(name = "tokenService")
    public TokenService tokenService() {
        return new TokenService(usersService);
    }
}