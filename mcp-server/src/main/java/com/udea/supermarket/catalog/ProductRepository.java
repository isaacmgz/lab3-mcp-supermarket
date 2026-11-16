package com.udea.supermarket.catalog;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p join fetch p.category where upper(p.sku) = upper(:sku)")
    Optional<Product> findBySku(@Param("sku") String sku);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    @Query(value = """
            SELECT p.* FROM product p
            JOIN category c ON c.id = p.category_id
            WHERE p.active
              AND (unaccent(lower(p.name)) LIKE '%' || unaccent(lower(:text)) || '%'
                   OR unaccent(lower(coalesce(p.description, ''))) LIKE '%' || unaccent(lower(:text)) || '%'
                   OR unaccent(lower(c.name)) LIKE '%' || unaccent(lower(:text)) || '%')
              AND (:category = '' OR unaccent(lower(c.name)) LIKE '%' || unaccent(lower(:category)) || '%')
            ORDER BY p.name
            LIMIT 20
            """, nativeQuery = true)
    List<Product> search(@Param("text") String text, @Param("category") String category);
}
