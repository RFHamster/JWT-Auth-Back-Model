package com.rfhamster.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rfhamster.project.services.UserServices;
import com.rfhamster.project.vo.UserSigninVO;

@RestController
@RequestMapping("/api/users/v1")
public class UserController {
	
	@Autowired
	UserServices userService;
	
	@GetMapping(value = "")
	public ResponseEntity<?> buscarTodos(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "limit", defaultValue = "12") Integer limit
			) {
		try {
			Pageable pageable = PageRequest.of(page, limit);
			return ResponseEntity.ok(userService.buscarTodos(pageable));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}		
	}
	
	@GetMapping(path = "/{codigo}")
	public ResponseEntity< ? > buscar(@PathVariable Long codigo) {
		if(codigo == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario nao encontrado");
		}
		try {
			UserSigninVO u = userService.buscarIdRetornoVO(codigo);
			if(u == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario nao encontrado");
			}
			return ResponseEntity.ok(u);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}		
	}
	
	@DeleteMapping(path = "/{codigo}")
	public ResponseEntity<?> deletar(@PathVariable Long codigo) {
		if(codigo == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario nao encontrado");
		}
	    try {
	        userService.deletar(codigo);
	        return ResponseEntity.noContent().build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}
}
