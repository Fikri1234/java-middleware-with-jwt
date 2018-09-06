package com.ogya.Security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author FIKRI-PC
 *
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter  {
	
	private static Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);
	
	private JwtHelper jwtHelper;
	
	private UserDetailsService userDetailsService;

	public TokenAuthenticationFilter(JwtHelper jwtHelper, UserDetailsService userDetailsService) {
        this.jwtHelper = jwtHelper;
        this.userDetailsService = userDetailsService;
	}
	
	@Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        String username;
        String authToken = jwtHelper.getToken(request);

        if (authToken != null) {
            // get username from token
        	logger.info("auth token: {}",authToken);
            username = jwtHelper.getUsernameFromToken(authToken);
            if (username != null) {
                // get user
            	logger.info("username token: {}",username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtHelper.validateToken(authToken, userDetails)) {
                    // create authentication
                    TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
                    authentication.setToken(authToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
	}
}
