package com.teraha.security.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.teraha.commons.entities.Role;

@Repository
public interface RoleRepository extends BaseRepository<Role> {

    Optional<Role> findByName(String name);
}
