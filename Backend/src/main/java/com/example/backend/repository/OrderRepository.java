package com.example.backend.repository;
import com.example.backend.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, String> {
    @Query("SELECT MAX(o.order_id) FROM Orders o")
    Optional<String> findMaxOrderId();

    @Query("SELECT o FROM Orders o WHERE FUNCTION('MONTH', o.order_date) = :month AND FUNCTION('YEAR', o.order_date) = :year")
    List<Orders> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
    
}
