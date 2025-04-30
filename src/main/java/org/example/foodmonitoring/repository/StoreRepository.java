package org.example.foodmonitoring.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.foodmonitoring.entity.Store;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public class StoreRepository {
    @PersistenceContext
    private EntityManager em;

    public Optional<Store> findById(Long id) {
        return Optional.ofNullable(em.find(Store.class, id));
    }

    public List<Store> findAll() {
        return em.createQuery("SELECT s FROM Store s", Store.class).getResultList();
    }

    @Transactional
    public Store save(Store store) {
        if (store.getId() == null) em.persist(store);
        else store = em.merge(store);
        return store;
    }

    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }
}
