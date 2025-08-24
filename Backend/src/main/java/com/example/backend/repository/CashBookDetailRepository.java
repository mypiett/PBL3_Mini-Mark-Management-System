package com.example.backend.repository;

import com.example.backend.model.CashBookDetail;
import com.example.backend.model.Orders;
import com.example.backend.model.StockImport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashBookDetailRepository extends JpaRepository<CashBookDetail, Long> {

    @EntityGraph(attributePaths = {"order", "stockImport"})
    List<CashBookDetail> findByCashBookId(Long cashBookId);

    boolean existsByCashBookIdAndOrder(Long cashBookId, Orders order);
    boolean existsByCashBookIdAndStockImport(Long cashBookId, StockImport stockImport);
}
