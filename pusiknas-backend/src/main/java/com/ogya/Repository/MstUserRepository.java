package com.ogya.Repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ogya.DTO.MstUserDTO;
import com.ogya.Service.MstUserService;


@Service
public class MstUserRepository implements MstUserService{

	public static final Logger logger = LoggerFactory.getLogger(MstUserRepository.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	public ResponseEntity<MstUserDTO> loadUserById(HttpHeaders headers, int id) {
		logger.info("id: {}",id);
		
		Map<String, String> uriVariable = new HashMap<>();
		uriVariable.put("id", String.valueOf(id));
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<MstUserDTO> response = restTemplate.exchange("http://localhost:3000/users?id={id}",HttpMethod.GET, entity, MstUserDTO.class,uriVariable);
		
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		
		return response;
	}
	
	public ResponseEntity<MstUserDTO> loadUserByUsernameAndPassword(HttpHeaders headers, String username, String password) {
		logger.info("user: {} , pass: {}",username);
		
		Map<String, String> uriVariable = new HashMap<>();
		uriVariable.put("username", username);
		uriVariable.put("password", password);
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<MstUserDTO> response = restTemplate.exchange("http://localhost:3000/users?user_name={username}&password={password}",HttpMethod.GET, entity, MstUserDTO.class,uriVariable);
		
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		
		return response;
	}
	
	/*That would be add API without credential security to access username*/
	public ResponseEntity<MstUserDTO> loadUserByUsername(String username) {
		logger.info("user: {} , pass: {}",username);
		
		Map<String, String> uriVariable = new HashMap<>();
		uriVariable.put("username", username);
		
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> entity = new HttpEntity<String>(header);
		
		ResponseEntity<MstUserDTO> response = restTemplate.exchange("http://localhost:3000/users?user_name={username}",HttpMethod.GET, entity, MstUserDTO.class,uriVariable);
		
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		
		return response;
	}
}
