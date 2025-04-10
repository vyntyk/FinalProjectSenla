package org.example.foodmonitoring.repository;

import org.example.foodmonitoring.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}