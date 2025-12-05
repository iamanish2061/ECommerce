package com.ECommerce.repository.cartAndOrders;

import com.ECommerce.model.cartandorders.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {

}
