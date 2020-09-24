package com.sportera.sportera.repositories;

import com.sportera.sportera.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmailIgnoreCase(String email);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);


}
