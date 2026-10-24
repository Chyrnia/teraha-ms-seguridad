package com.teraha.security.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teraha.commons.dtos.CreateUserRequestDTO;
import com.teraha.commons.dtos.UserDTO;
import com.teraha.commons.entities.Role;
import com.teraha.commons.entities.User;
import com.teraha.security.exception.InvalidRoleAssignmentException;
import com.teraha.security.exception.RoleNotFoundException;
import com.teraha.security.exception.UserAlreadyExistsException;
import com.teraha.security.exception.UserNotFoundException;
import com.teraha.security.mapper.UserMapper;
import com.teraha.security.repository.RoleRepository;
import com.teraha.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
@PreAuthorize("hasRole('SYSADMIN')")
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.pagination.page-size:20}")
    private int pageSize;

    public UserDTO createUser(CreateUserRequestDTO dto) {
        String username = dto.getUsername().trim();
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException(
                "A user with that username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(
                "A user with that email already exists");
        }

        Set<Role> roles = resolveRoles(dto.getRoles());

        User user = new User(
				username,
				email,
				passwordEncoder.encode(dto.getPassword())
				);

        roles.forEach(user::addRole);

        User saved = userRepository.save(user);

        log.info("Created user with username {}", saved.getUsername());

        return userMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public UserDTO findUserById(Long id) {
        return userRepository.findByIdAndEnabledTrue(id)
            .map(userMapper::toDto)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> listUsers(int page) {
        Pageable pageable = PageRequest.of(page, pageSize);

        return userRepository.findAllByEnabledTrue(pageable)
            .map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String query, int page) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                "Search query must not be blank");
        }

        Pageable pageable = PageRequest.of(page, pageSize);

        return userRepository.searchEnabledUsers(
                query.trim(), pageable)
            .map(userMapper::toDto);
    }

    public void disableUser(Long id) {
        User user = userRepository.findByIdAndEnabledTrue(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        user.setEnabled(false);

        log.info("Disabled user with username {}", user.getUsername());
    }

    public UserDTO updateUserRoles(Long id, List<String> roleNames) {
        User user = userRepository.findByIdAndEnabledTrue(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        Set<Role> newRoles = resolveRoles(roleNames);

        // Copy the collection before modifying the association.
        for (Role role : new ArrayList<>(user.getRoles())) {
            user.removeRole(role);
        }

        newRoles.forEach(user::addRole);

        User saved = userRepository.save(user);

        log.info("Updated roles for user with username {}",
            saved.getUsername());

        return userMapper.toDto(saved);
    }

    private Set<Role> resolveRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new InvalidRoleAssignmentException(
                "At least one role must be assigned");
        }

        Set<Role> resolved = new LinkedHashSet<>();

        for (String rawName : roleNames) {
            if (rawName == null || rawName.isBlank()) {
                throw new InvalidRoleAssignmentException(
                    "Role names must not be blank");
            }

            String name = rawName.trim().toUpperCase(Locale.ROOT);

            Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException(name));

            resolved.add(role);
        }

        return resolved;
    }
}
