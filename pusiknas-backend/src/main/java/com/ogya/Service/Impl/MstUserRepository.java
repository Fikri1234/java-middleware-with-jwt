package com.ogya.Service.Impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ogya.DTO.MstUserDTO;
import com.ogya.Service.MstUserService;

/**
 * @author FIKRI-PC
 *
 */
@Service
public class MstUserRepository implements MstUserService{

	public static final Logger logger = LoggerFactory.getLogger(MstUserRepository.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	public ResponseEntity<MstUserDTO[]> loadUserById(HttpHeaders headers, int id) {
		logger.info("id: {}",id);
		
		Map<String, String> uriVariable = new HashMap<>();
		uriVariable.put("id", String.valueOf(id));
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<MstUserDTO[]> response = restTemplate.exchange("http://localhost:3000/users?id={id}",HttpMethod.GET, entity, MstUserDTO[].class,uriVariable);
		
		return response;
	}
	
	public ResponseEntity<MstUserDTO> loadUserByUsernameAndPassword(HttpHeaders headers, String username, String password) {
		logger.info("loadUserByUsernameAndPassword user: {} , pass: {}",username,password);
		
		Map<String, String> uriVariable = new HashMap<>();
		uriVariable.put("username", username);
		uriVariable.put("password", password);
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<MstUserDTO> response = restTemplate.exchange("http://localhost:3000/users?username={username}&password={password}",HttpMethod.GET, entity, MstUserDTO.class,uriVariable);
		
		return response;
	}
	
	/*That would be add API without credential security to access username*/
	public ResponseEntity<MstUserDTO[]> loadUserByUsername(String username) {
		logger.info("loadUserByUsername user: {}",username);
		
		//restTemplate = new RestTemplate();
		
		Map<String, String> uriVariable = new HashMap<>();
		uriVariable.put("username", username);
		logger.info("loadUserByUsername step 1");
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		logger.info("loadUserByUsername step 2");
		HttpEntity<String> entity = new HttpEntity<String>(header);
		logger.info("loadUserByUsername entity");
		ResponseEntity<MstUserDTO[]> response = restTemplate.exchange("http://localhost:3000/users?username={username}",HttpMethod.GET, entity, MstUserDTO[].class,uriVariable);
		//ResponseEntity<MstUserDTO[]> response = restTemplate.exchange("http://localhost:3000/api/users?filter[where][user_name]={username}",HttpMethod.GET, entity, MstUserDTO[].class,uriVariable);
		logger.info("loadUserByUsername step 3");
		MstUserDTO[] resp = response.getBody();
		logger.info("lengt: {}",resp.length);
		logger.info("loadUserByUsername user:{} pass: {}",resp[0].getUsername(), resp[0].getPassword());
		return response;
	}
}
