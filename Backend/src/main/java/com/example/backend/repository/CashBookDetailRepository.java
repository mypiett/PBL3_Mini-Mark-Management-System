package com.example.backend.repository;

import com.example.backend.model.CashBookDetail;
import com.example.backend.model.Orders;
import com.example.backend.model.StockImport;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashBookDetailRepository extends JpaRepository<CashBookDetail, Long> {

  @EntityGraph(attributePaths = {"order", "stockImport"})
  List<CashBookDetail> findByCashBookId(Long cashBookId);

  boolean existsByCashBookIdAndOrder(Long cashBookId, Orders order);

  boolean existsByCashBookIdAndStockImport(Long cashBookId, StockImport stockImport);
}
