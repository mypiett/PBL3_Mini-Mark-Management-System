package com.example.backend.repository;

import com.example.backend.model.StockImport;
import com.example.backend.model.StockImportDetails;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockImportDetailsRepository extends JpaRepository<StockImportDetails, String> {

  @Query(
      "SELECT d.product.productId FROM StockImportDetails d WHERE d.stockImport.importId = :importId")
  List<String> findProductIdsByImportId(@Param("importId") String importId);

  @Query("SELECT d FROM StockImportDetails d WHERE d.stockImport.importId = :importId")
  List<StockImportDetails> findByImportId(@Param("importId") String importId);

  List<StockImportDetails> findByStockImport_ImportId(String importId);

  Optional<StockImportDetails> findById(String importDetailId);

  @Query(
      "SELECT s FROM StockImport s WHERE MONTH(s.importDate) = :month AND YEAR(s.importDate) = :year")
  List<StockImport> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
