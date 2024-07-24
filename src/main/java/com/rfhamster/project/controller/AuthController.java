package com.rfhamster.project.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rfhamster.project.security.JwtTokenProvider;
import com.rfhamster.project.services.UserServices;
import com.rfhamster.project.vo.AccountCredentialsVO;
import com.rfhamster.project.vo.TokenVO;
import com.rfhamster.project.vo.UserSigninVO;

@RestController
@RequestMapping("/api/auth/v1")
public class AuthController {
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private UserServices service;
	
	@PostMapping(value = "/signin")
	public ResponseEntity<?> signin(@RequestBody AccountCredentialsVO data) {
		if (checkIfParamsIsNotNull(data))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		String username = data.getUsername();
		String password = data.getPassword();
		Map<String, Object> dadosAutenticacao = new HashMap<String, Object>();
		
		try {
			Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			if(!authenticate.isAuthenticated()) {
				throw new BadCredentialsException("Invalid username/password supplied!");
			}
			
			UserSigninVO userVo = service.buscarUsuarioRetornoVO(username);
			if(userVo == null) {
				throw new BadCredentialsException("Invalid username/password supplied!");
			}
			
			dadosAutenticacao.put("tokenResponse", tokenProvider.createAccessToken(username, userVo.getRoles()));
			dadosAutenticacao.put("usuario", userVo);
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username/password supplied!");
		}catch (DisabledException e) {
			return ResponseEntity.status(HttpStatus.LOCKED).body("Disabled User!");
		}catch (LockedException e) {
			return ResponseEntity.status(HttpStatus.LOCKED).body("Locked User!");
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return ResponseEntity.ok(dadosAutenticacao);
	}

	@SuppressWarnings("rawtypes")
	@PutMapping(value = "/refresh/{username}")
	public ResponseEntity refreshToken(@PathVariable("username") String username,
			@RequestHeader("Authorization") String refreshToken) {

		if (checkIfParamsIsNotNull(username, refreshToken))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		
		
		var user = service.buscarUsuario(username);
		
		var tokenResponse = new TokenVO();
		if (user != null) {
			tokenResponse = tokenProvider.refreshToken(refreshToken);
		} else {
			throw new UsernameNotFoundException("Username " + username + " not found!");
		}
		
		
		if (tokenResponse == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		return ResponseEntity.ok(tokenResponse);
	}

	
	
	private boolean checkIfParamsIsNotNull(String username, String refreshToken) {
		return refreshToken == null || refreshToken.isBlank() ||
				username == null || username.isBlank();
	}

	private boolean checkIfParamsIsNotNull(AccountCredentialsVO data) {
		return data == null || data.getUsername() == null || data.getUsername().isBlank()
				 || data.getPassword() == null || data.getPassword().isBlank();
	}

}
