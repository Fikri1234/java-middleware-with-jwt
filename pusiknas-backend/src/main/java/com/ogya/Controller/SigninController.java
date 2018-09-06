package com.ogya.Controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ogya.DTO.UserTokenStateDTO;
import com.ogya.Security.JwtAuthenticationRequest;
import com.ogya.Security.JwtHelper;
import com.ogya.Service.Impl.CustomUserDetailService;

@RestController
public class SigninController {

	private static final Logger logger = LoggerFactory.getLogger(SigninController.class);
	
	//get Id session
			//logger.debug("http session={}", httpSession.getId());
	
	// update last login
	
	@Autowired
    JwtHelper jwtHelper;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailService customUserDetailService;
    
	@RequestMapping(value = "/signin", method = RequestMethod.POST
			, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest,
            HttpServletResponse response
    ) throws AuthenticationException, IOException {

		logger.debug("login user: {} pass: {}",authenticationRequest.getUsername(),
                        authenticationRequest.getPassword());
        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        
        logger.debug("login user: {} pass: {}",authenticationRequest.getUsername(),
                authenticationRequest.getPassword());

        // Inject into security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // token creation
        User user = (User)authentication.getPrincipal();
        String jws = jwtHelper.generateToken( user.getUsername());
        int expiresIn = jwtHelper.getExpiredIn();
        // Return the token
        return ResponseEntity.ok(new UserTokenStateDTO(jws, expiresIn));
    }
}
