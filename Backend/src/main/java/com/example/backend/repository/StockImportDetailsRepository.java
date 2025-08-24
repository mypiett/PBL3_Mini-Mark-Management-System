package com.example.backend.repository;

import com.example.backend.model.StockImport;
import com.example.backend.model.StockImportDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockImportDetailsRepository extends JpaRepository<StockImportDetails, String> {

    // Truy vấn lấy danh sách productId dựa trên importId
    @Query("SELECT d.product.productId FROM StockImportDetails d WHERE d.stockImport.importId = :importId")
    List<String> findProductIdsByImportId(@Param("importId") String importId);

    // Truy vấn lấy danh sách StockImportDetails dựa trên importId
    @Query("SELECT d FROM StockImportDetails d WHERE d.stockImport.importId = :importId")
    List<StockImportDetails> findByImportId(@Param("importId") String importId);
    
    // Phương thức tìm theo StockImport để dễ dàng cập nhật tồn kho
    List<StockImportDetails> findByStockImport_ImportId(String importId);

    // Phương thức trả về Optional<StockImportDetails> để xử lý tốt hơn các trường hợp không tìm thấy
    Optional<StockImportDetails> findById(String importDetailId);
    
@Query("SELECT s FROM StockImport s WHERE MONTH(s.importDate) = :month AND YEAR(s.importDate) = :year")
List<StockImport> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
}


