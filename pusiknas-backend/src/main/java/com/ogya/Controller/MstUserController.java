package com.ogya.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ogya.DTO.MstUserDTO;
import com.ogya.Service.MstUserService;

/**
 * @author FIKRI-PC
 *
 */
@RestController
@RequestMapping("/user")
public class MstUserController {
	
	public static final Logger logger = LoggerFactory.getLogger(MstUserController.class);

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	MstUserService mstUserService;
	
//  --------------- retreave single data ------------------
	@GetMapping("/{id}")
	public ResponseEntity<MstUserDTO> retrieveMstUserById(@PathVariable("id") int id,
			@RequestHeader HttpHeaders headers) {
		logger.info("[retrieveMstUserById] id: {} header: {} header ct: {}",id,headers,headers.getContentType());
		ResponseEntity<MstUserDTO[]> response = mstUserService.loadUserById(headers, id);
		
		MstUserDTO dep = response.getBody()[0];
		logger.info("[retrieveMstUserById]  user: {}",dep.getUsername());
		
		if (response.hasBody()) {
			return new ResponseEntity<MstUserDTO> (dep, HttpStatus.OK);
		}else {
			logger.error("id not found: ",id);
			return new ResponseEntity<MstUserDTO>(HttpStatus.NOT_FOUND);
		}
	}
	
//  --------------- retreave single data by username ------------------
	@GetMapping("?username={username}")
	public ResponseEntity<MstUserDTO> retrieveMstUserByUsername(@PathVariable("username") String username,
			@RequestHeader HttpHeaders headers) {
		
		Map<String, String> uriVariable = new HashMap<>();
		uriVariable.put("username", String.valueOf(username));
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<MstUserDTO> response = restTemplate.exchange("http://localhost:3000/users?username={username}",HttpMethod.GET, entity, MstUserDTO.class,uriVariable);
		
		MstUserDTO dep = response.getBody();
		
		logger.info("nama user: {}",dep.getUsername());
		
		if (!response.hasBody()) {
			return new ResponseEntity<MstUserDTO> (dep, HttpStatus.OK);
		}else {
			logger.error("id not found: ",username);
			return new ResponseEntity<MstUserDTO>(HttpStatus.NOT_FOUND);
		}
	}
	
	// ---------------- retrieve all data ----------------------
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<?> getAllMstUser(@RequestHeader HttpHeaders headers){
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		logger.info("test 1");
		ResponseEntity<List<MstUserDTO>> response = restTemplate.exchange("http://localhost:3000/users/", HttpMethod.GET, entity, new ParameterizedTypeReference<List<MstUserDTO>>() {} );
		logger.info("test 2");
		
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	// ---------------- posting data ----------------------
	@RequestMapping(value = "/", method = RequestMethod.POST, produces= {MediaType.APPLICATION_JSON_VALUE},
			consumes= {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> createUser(@RequestHeader HttpHeaders headers, @RequestBody MstUserDTO mstUserDTO){
		
		try {
			HttpEntity<MstUserDTO> request = new HttpEntity<MstUserDTO>(mstUserDTO, headers);
			restTemplate.postForObject("http://localhost:3000/users/}", request, MstUserDTO.class);
			
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	// ---------------- update data ----------------------
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable("id") int id, @RequestHeader HttpHeaders headers, @RequestBody MstUserDTO mstUserDTO){
		
		logger.info("Updating user with id {}",id);
		
		Map<String, String> uriVariable = new HashMap<>();
		uriVariable.put("id", String.valueOf(id));
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<MstUserDTO> response = restTemplate.exchange("http://localhost:3000/users?id={id}",HttpMethod.GET, entity, MstUserDTO.class,uriVariable);
		
		if (!response.hasBody()) {
			logger.error("Unable to update. User with id {} not found",id);
			return new ResponseEntity<>("Unable to update. User with id "+id+" not found", HttpStatus.NOT_FOUND);
		}
		
		//restTemplate.put("http://localhost:3000/users?id={id}", mstUserDTO, uriVariable);
		restTemplate.exchange("http://localhost:3000/users?id={id}",HttpMethod.PUT, entity, MstUserDTO.class,uriVariable);
		
		return new ResponseEntity<>(entity, HttpStatus.OK);
	}
	
	// ---------------- delete data by id ----------------------
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUserById(@PathVariable("id") int id, @RequestHeader HttpHeaders headers){
		
		logger.info("Fetching & Deleting UserProfiles with id {}", id);
		
		Map<String, String> uriVariable = new HashMap<>();
		uriVariable.put("id", String.valueOf(id));
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<MstUserDTO> response = restTemplate.exchange("http://localhost:3000/users?id={id}",HttpMethod.GET, entity, MstUserDTO.class,uriVariable);
		
		if (!response.hasBody()) {
			logger.error("Unable to delete. User with id {} not found",id);
			return new ResponseEntity<>("Unable to update. User with id "+id+" not found", HttpStatus.NOT_FOUND);
		}
		
		restTemplate.exchange("http://localhost:3000/users?id={id}",HttpMethod.DELETE, entity, MstUserDTO.class,uriVariable);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	// ---------------- delete all data ----------------------
	@RequestMapping(value = "/", method = RequestMethod.DELETE)
	public ResponseEntity<MstUserDTO> deleteUser(@RequestHeader HttpHeaders headers){
		
		logger.info("Deleting All User");
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		restTemplate.exchange("http://localhost:3000/users/",HttpMethod.DELETE, entity, MstUserDTO.class);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
