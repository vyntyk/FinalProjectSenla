package org.example.foodmonitoring.repository;

import org.example.foodmonitoring.entity.Role;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class RoleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Role> findByName(String name) {
        return entityManager
                .createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }
}
