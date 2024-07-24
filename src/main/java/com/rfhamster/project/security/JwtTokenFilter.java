package com.rfhamster.project.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter{
	
	@Autowired
	private HandlerExceptionResolver handlerExceptionResolver;

	@Autowired
	private JwtTokenProvider tokenProvider;
	
	public JwtTokenFilter(JwtTokenProvider tokenProvider,HandlerExceptionResolver handlerExceptionResolver) {
		this.tokenProvider = tokenProvider;
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
		String token = tokenProvider.resolveToken((HttpServletRequest) request);
		if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
		
		try {
			if (tokenProvider.validateToken(token)) {
				UsernamePasswordAuthenticationToken auth = tokenProvider.getAuthentication(token);
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				if (auth != null) {
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}

			filterChain.doFilter(request, response);
		}catch (Exception e) {
			 handlerExceptionResolver.resolveException(request, response, null, e);
		}
				
	}
}
