package org.example.foodmonitoring.repository;

import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.foodmonitoring.entity.PriceHistory;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public class PriceHistoryRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public PriceHistory save(PriceHistory ph) {
        if (ph.getId() == null) em.persist(ph);
        else ph = em.merge(ph);
        return ph;
    }

    public List<PriceHistory> findByProductAndPeriod(Long productId, LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                        "SELECT ph FROM PriceHistory ph WHERE ph.product.id = :pid AND ph.timestamp BETWEEN :from AND :to ORDER BY ph.timestamp",
                        PriceHistory.class)
                .setParameter("pid", productId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    public List<PriceHistory> findLatestByProductAndStores(Long productId, List<Long> storeIds) {
        return em.createQuery(
                        "SELECT ph FROM PriceHistory ph WHERE ph.product.id = :pid "
                                + "AND ph.timestamp = (SELECT MAX(ph2.timestamp) FROM PriceHistory ph2 WHERE ph2.product.id = :pid AND ph2.store.id = ph.store.id) "
                                + "AND ph.store.id IN :stores",
                        PriceHistory.class)
                .setParameter("pid", productId)
                .setParameter("stores", storeIds)
                .getResultList();
    }
}
