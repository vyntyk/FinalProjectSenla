package org.example.foodmonitoring.repository;

import org.example.foodmonitoring.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}