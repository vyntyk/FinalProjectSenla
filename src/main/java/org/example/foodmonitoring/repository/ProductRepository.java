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

    public List<Product> findAll(String nameFilter, Long categoryIdFilter) {
        String jpql = "SELECT p FROM Product p WHERE "
                + "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%'))) AND "
                + "(:categoryId IS NULL OR p.category.id = :categoryId)"; // JOINs p.category implicitly
        return em.createQuery(jpql, Product.class)
                .setParameter("name", nameFilter)
                .setParameter("categoryId", categoryIdFilter)
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
