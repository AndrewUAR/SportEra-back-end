package com.sportera.sportera.repositories;

import com.sportera.sportera.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailIgnoreCase(String email);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);


}
