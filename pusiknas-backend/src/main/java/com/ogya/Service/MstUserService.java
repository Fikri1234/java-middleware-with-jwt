package com.ogya.Service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.ogya.DTO.MstUserDTO;

/**
 * @author FIKRI-PC
 *
 */
public interface MstUserService {

	public ResponseEntity<MstUserDTO[]> loadUserById(HttpHeaders headers, int id);
	public ResponseEntity<MstUserDTO> loadUserByUsernameAndPassword(HttpHeaders headers, String username, String password);
	
	public ResponseEntity<MstUserDTO[]> loadUserByUsername(String username);
}
