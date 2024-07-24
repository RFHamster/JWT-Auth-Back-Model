package com.rfhamster.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.rfhamster.project.security.JwtTokenFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

	@Autowired
	private JwtTokenFilter customFilter;
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
    AuthenticationManager authenticationManagerBean(
    		AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		//@formatter:off
        return http
            .httpBasic(basic -> basic.disable())
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
            		//Teste Controller
            		.requestMatchers(HttpMethod.GET, "/teste").permitAll()
            		.requestMatchers(HttpMethod.POST, "/api/auth/v1/signin").permitAll()
            		.requestMatchers(HttpMethod.GET, "/api/auth/v1/teste").permitAll()
                    .anyRequest().authenticated()
            )
            .cors(cors -> {})
            .build();
        //@formatter:on
	}
}
