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
        String sql = "WITH RankedPrices AS ("
                + " SELECT ph.id, ph.product_id, ph.store_id, ph.price, ph.timestamp, "
                + "        ROW_NUMBER() OVER(PARTITION BY ph.store_id ORDER BY ph.timestamp DESC) as rn "
                + " FROM price_history ph "
                + " WHERE ph.product_id = :productId AND ph.store_id IN (:storeIds)"
                + ") "
                + "SELECT id, product_id, store_id, price, timestamp FROM RankedPrices WHERE rn = 1";

        @SuppressWarnings("unchecked") // Suppress warning for native query
        List<PriceHistory> result = em.createNativeQuery(sql, PriceHistory.class)
                .setParameter("productId", productId)
                .setParameter("storeIds", storeIds)
                .getResultList();
        return result;
    }
}
