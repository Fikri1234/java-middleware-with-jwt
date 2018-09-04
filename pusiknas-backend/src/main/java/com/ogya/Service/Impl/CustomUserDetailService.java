package com.ogya.Service.Impl;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ogya.DTO.MstUserDTO;
import com.ogya.Service.MstUserService;

@Service
public class CustomUserDetailService implements UserDetailsService {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);
	
	@Autowired
	private MstUserService mstUserService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
		ResponseEntity<MstUserDTO> response = mstUserService.loadUserByUsername(username);
		
		MstUserDTO mstUserDTO = response.getBody();
		
		List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("user"));
		logger.info("auth: {}",authorities.toArray().toString());
        if (mstUserDTO == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
        	return new User (mstUserDTO.getUsername(), null, authorities);
        }
	}
	
	public void changePassword(String oldPassword, String newPassword) {

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        if (authenticationManager != null) {
            logger.debug("Re-authenticating user '"+ username + "' for password change request.");

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
        } else {
            logger.debug("No authentication manager set. can't change Password!");

            return;
        }

        logger.debug("Changing password for user '"+ username + "'");

        ResponseEntity<MstUserDTO> response = mstUserService.loadUserByUsername(username);

        MstUserDTO mstUserDTO = response.getBody();
        
        mstUserDTO.setPassword(passwordEncoder.encode(newPassword));
        /*posting user*/
        /*userRepository.save(user);*/
	}
}

