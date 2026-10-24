package com.teraha.security.mapper;

import org.springframework.stereotype.Component;

import com.teraha.commons.dtos.RoleDTO;
import com.teraha.commons.dtos.UserDTO;
import com.teraha.commons.entities.Role;
import com.teraha.commons.entities.User;

@Component
public class UserMapper {

    public UserDTO toDto(User user) {
        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        dto.setLastLogin(user.getLastLogin());

        dto.setRoles(
            user.getRoles().stream()
                .map(this::toRoleDto)
                .toList()
        );

        return dto;
    }

    private RoleDTO toRoleDto(Role role) {
        RoleDTO dto = new RoleDTO();

        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());

        return dto;
    }
}
