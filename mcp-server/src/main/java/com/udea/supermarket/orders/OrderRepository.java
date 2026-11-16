package com.udea.supermarket.orders;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    Optional<CustomerOrder> findFirstByCustomerIdAndStatusOrderByCreatedAtDesc(Long customerId, OrderStatus status);

    List<CustomerOrder> findTop10ByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
