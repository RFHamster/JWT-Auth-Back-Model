package com.rfhamster.project.vo.handler;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.rfhamster.project.model.User;
import com.rfhamster.project.vo.UserSigninVO;

public class CustomMapper {
	private static ModelMapper mapper = new ModelMapper();
	
	static {
		mapper.createTypeMap(User.class, UserSigninVO.class)
		.addMapping(User::getId, UserSigninVO::setKey);
		
		mapper.createTypeMap(UserSigninVO.class, User.class)
		.addMapping(UserSigninVO::getKey, User::setId);
	}
	
	public static <O, D> D parseObject(O origin, Class<D> destination) {
		return mapper.map(origin, destination);
	}
	
	public static <O, D> List<D> parseListObjects(List<O> origin, Class<D> destination) {
		List<D> destinationObjects = new ArrayList<D>();
		for (O o : origin) {
			destinationObjects.add(mapper.map(o, destination));
		}
		return destinationObjects;
	}
}
