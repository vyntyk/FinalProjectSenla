package org.example.foodmonitoring.repository;

import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.foodmonitoring.entity.Product;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public class ProductRepository {
    @PersistenceContext
    private EntityManager em;

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    public List<Product> findAll(String nameFilter, String categoryFilter) {
        String jpql = "SELECT p FROM Product p WHERE "
                + "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%'))) AND "
                + "(:cat IS NULL OR LOWER(p.category) = LOWER(:cat))";
        return em.createQuery(jpql, Product.class)
                .setParameter("name", nameFilter)
                .setParameter("cat", categoryFilter)
                .getResultList();
    }

    @Transactional
    public Product save(Product p) {
        if (p.getId() == null) em.persist(p);
        else p = em.merge(p);
        return p;
    }

    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }
}
