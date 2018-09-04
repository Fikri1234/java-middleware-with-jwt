package com.ogya.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	
	private static final String RESOURCE_ID = "resource-server-rest-api";
    private static final String SECURED_READ_SCOPE = "#oauth2.hasScope('read')";
    private static final String SECURED_WRITE_SCOPE = "#oauth2.hasScope('write')";
    private static final String SECURED_PATTERN = "/secured/**";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID);
    }
	
	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception{
		httpSecurity
			.authorizeRequests()
				.antMatchers("/signup", "/about").permitAll()
				.antMatchers(HttpMethod.POST, SECURED_PATTERN).access(SECURED_WRITE_SCOPE)
                .anyRequest().access(SECURED_READ_SCOPE)
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.failureUrl("/login?error=1")
                .loginProcessingUrl("/authenticate")
                .and()
			.logout()
				.logoutUrl("/logout")
	            .permitAll()
	            .logoutSuccessUrl("/signin?logout")
	            .and();
	}
}
