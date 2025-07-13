package com.smartparking.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
//this filter will incerpet HTTP requests to check for JWT tokens and set the authentication in the security context
//OncePerRequestFilter ensures that the filter is executed once per request
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JWTAuthentication jwtAuthentication; 
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, java.io.IOException {
        //check for the authorization header in the request
        final String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String jwt = authorizationHeader.substring(7);
            try{
                String username = jwtAuthentication.getUsernameFromToken(jwt);
                //if the username is existent and the token is a valid one, then authenticate the user
                if (username != null && jwtAuthentication.validateToken(jwt, username)){
                    //retrieving roles 
                    List<String> roles = jwtAuthentication.getRolesFromToken(jwt);
                    List<SimpleGrantedAuthority> authority = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                    //set the authentication in the security context according to the roles
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, authority);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    response .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }
            } catch (Exception e) {
                //if there is an exception while sending the token, then an error response is sent
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing or malformed");
                return;
            }
        }
        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    @Override
    //this method defines which endpoints should not have jwt authentication filter applied to them
    //in this case, the login and register endpoints are excluded from the filter
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        System.out.println("Path: " + request.getServletPath());
        return path.equals("/users/login") || path.equals("/users/register") || path.startsWith("/actuator") || path.startsWith("/resetPassword/request");
    }
}//jwt authentication filter class 
