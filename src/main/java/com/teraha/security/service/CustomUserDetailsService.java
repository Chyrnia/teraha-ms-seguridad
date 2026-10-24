package com.teraha.security.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.teraha.commons.entities.User;
import com.teraha.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username){
		User user =  userRepository.findByUsername(username)
			.orElseThrow(() -> 
					new UsernameNotFoundException("User not found"));

		return org.springframework.security.core.userdetails.User
			.withUsername(user.getUsername())
			.password(user.getPasswordHash())
			.disabled(!user.isEnabled())
			.authorities(
				user.getRoles().stream()
					.map(role -> "ROLE_" + role.getName())
					.toArray(String[]::new)
					)
			.build();
					
	}
	
}
