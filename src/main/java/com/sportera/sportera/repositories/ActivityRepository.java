package com.sportera.sportera.repositories;

import com.sportera.sportera.models.Activity;
import com.sportera.sportera.models.EActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findByName(EActivity name);
}
