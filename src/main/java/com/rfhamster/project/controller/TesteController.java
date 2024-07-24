package com.rfhamster.project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/teste")
public class TesteController {
	@GetMapping(path = "")
    public String helloWorld() {
        return "Hello World";
    }
	
	@GetMapping(path = "/user")
    public String helloWorldUser() {
        return "Hello User";
    }
	
	@GetMapping(path = "/admin")
    public String helloWorldADMIN() {
        return "Hello ADM";
    }
}
