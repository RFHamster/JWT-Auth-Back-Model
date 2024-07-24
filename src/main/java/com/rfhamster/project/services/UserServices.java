package com.rfhamster.project.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rfhamster.project.controller.UserController;
import com.rfhamster.project.enums.UserRole;
import com.rfhamster.project.model.User;
import com.rfhamster.project.repositories.UserRepository;
import com.rfhamster.project.vo.UserSigninVO;
import com.rfhamster.project.vo.handler.CustomMapper;

@Service
public class UserServices implements UserDetailsService{
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	PagedResourcesAssembler<UserSigninVO> assembler;
	
	public UserServices(UserRepository repository) {
		this.repository = repository;
	}
	
	public User salvarUserComum (String username, String password) throws DataIntegrityViolationException{
		User usuario = new User(username, password, true, true, true, true, UserRole.USER);
		try {
			return repository.save(usuario);
		} catch (DataIntegrityViolationException  e) {
			System.out.println(e.getMessage());
            throw e;
		}		
	}
	
	public PagedModel<EntityModel<UserSigninVO>> buscarTodos(Pageable pageable) {
        Page<User> usuarios = repository.findAll(pageable);
        
        var personVoPage = usuarios.map(p -> CustomMapper.parseObject(p, UserSigninVO.class));
        personVoPage.map(p -> p.add(linkTo(methodOn(UserController.class).buscar(p.getKey())).withSelfRel()));
        Link link = linkTo(methodOn(UserController.class).
        		buscarTodos(pageable.getPageNumber(), pageable.getPageSize())).withSelfRel();
		return assembler.toModel(personVoPage, link);
    }
	
	
	public UserSigninVO buscarIdRetornoVO(Long id) {
        Optional<User> usuario = repository.findById(id);
        if(!usuario.isPresent()) {
        	return null;
        }
        return retornarVOcomLinkTo(usuario.get());
    }
	
	public UserSigninVO buscarUsuarioRetornoVO(String username) {
        Optional<User> usuario = repository.findByUsernameOP(username);
        if(!usuario.isPresent()) {
        	return null;
        }
        return retornarVOcomLinkTo(usuario.get());
    }
	
	public User buscarIdUser(Long id) {
        Optional<User> usuario = repository.findById(id);
        return usuario.orElse(null);
    }
	
	public User buscarUsuario(String username) {
        Optional<User> usuario = repository.findByUsernameOP(username);
        return usuario.orElse(null);
    }
	
	public User atualizarUser(Long id, String username, String pass) {
		Optional<User> userExistente = repository.findById(id);
		if(!userExistente.isPresent()) {
			return null;
		}
    	User u = userExistente.get();
    	u.setUsername(username);
    	u.setPassword(pass);
        return repository.save(u);
	}
	
	public boolean deletar(Long id) {
        repository.deleteById(id);
		return true;
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = repository.findByUsername(username);
		if (user != null) {
			return user;
		} else {
			throw new UsernameNotFoundException("Username " + username + " not found!");
		}
	}
	
	public UserSigninVO retornarVOcomLinkTo(User u) {
		 UserSigninVO usuarioVo = CustomMapper.parseObject(u, UserSigninVO.class);
        usuarioVo.add(linkTo(methodOn(UserController.class).buscar(usuarioVo.getKey())).withSelfRel());
        return usuarioVo;
	}
}
