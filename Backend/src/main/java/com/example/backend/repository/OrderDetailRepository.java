package com.example.backend.repository;
import com.example.backend.model.Order_details;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
public interface OrderDetailRepository extends JpaRepository<Order_details, String> {
   @Query("SELECT MAX(o.order_detail_id) FROM Order_details o")
    Optional<String> findMaxDetailId();
}

