package com.teraha.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.teraha.commons.entities.User;

public interface UserRepository extends BaseRepository<User> {

	Optional<User> findByIdAndEnabledTrue(Long id);

	Optional<User> findByUsernameAndEnabledTrue(String username);

	Optional<User> findByUsername(String username);

	boolean existsByEmail(String email);

	Page<User> findAllByEnabledTrue(Pageable page);

	@Query("""
		SELECT u FROM User u
		where u.enabled = true
		and (
			lower(u.username) like lower(concat('%', :query, '%'))
			or lower(u.email) like lower(concat('%', :query, '%'))
			)
		""")
	Page<User> searchEnabledUsers(@Param("query") String query, Pageable page);

}
