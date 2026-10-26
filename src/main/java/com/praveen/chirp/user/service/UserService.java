package com.praveen.chirp.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.praveen.chirp.common.exception.DuplicateEmailException;
import com.praveen.chirp.common.exception.DuplicateUsernameException;
import com.praveen.chirp.common.exception.UserNotFoundException;
import com.praveen.chirp.user.dto.CreateUserRequest;
import com.praveen.chirp.user.dto.UserResponse;
import com.praveen.chirp.user.entity.User;
import com.praveen.chirp.user.repository.UserRepository;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserResponse register(CreateUserRequest request) {
		String username = request.username().trim().toLowerCase(Locale.ROOT);
		String email = request.email().trim().toLowerCase(Locale.ROOT);

		if (userRepository.existsByUsernameIgnoreCase(username)) {
			throw new DuplicateUsernameException(username);
		}

		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new DuplicateEmailException(email);
		}

		String passwordHash = passwordEncoder.encode(request.password());
		User user = new User(username, email, passwordHash);
		User savedUser = userRepository.save(user);

		return new UserResponse(
				savedUser.getId(),
				savedUser.getUsername(),
				savedUser.getEmail(),
				savedUser.getCreatedAt());
	}

	@Transactional(readOnly = true)
	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));

		return new UserResponse(
				user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt());
	}
}
