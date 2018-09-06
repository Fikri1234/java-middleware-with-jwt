package com.ogya.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.ogya.Security.JwtAuthenticationEntryPoint;
import com.ogya.Security.JwtHelper;
import com.ogya.Security.TokenAuthenticationFilter;
import com.ogya.Service.Impl.CustomUserDetailService;

/**
 * @author FIKRI-PC
 *
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(Encoder.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private CustomUserDetailService customUserDetailService;
	
    @Autowired
    private PasswordEncoder userPasswordEncoder;
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    JwtHelper jwtHelper;
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(customUserDetailService).passwordEncoder(userPasswordEncoder);
	}
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception{
		httpSecurity
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and()
	        .exceptionHandling().authenticationEntryPoint( jwtAuthenticationEntryPoint ).and()
			.authorizeRequests()
				.antMatchers("/signup", "/signin", "/about").permitAll()
                .anyRequest().authenticated()
				.and()
				.addFilterBefore(new TokenAuthenticationFilter(jwtHelper, customUserDetailService), BasicAuthenticationFilter.class);	
	}

}
