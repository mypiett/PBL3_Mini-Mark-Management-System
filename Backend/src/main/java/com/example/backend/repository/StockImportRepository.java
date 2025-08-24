package com.example.backend.repository;
import com.example.backend.model.StockImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StockImportRepository extends JpaRepository<StockImport, String> {
    @Query("SELECT s FROM StockImport s WHERE FUNCTION('MONTH', s.importDate) = :month AND FUNCTION('YEAR', s.importDate) = :year")
    List<StockImport> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
    @Query("SELECT s FROM StockImport s WHERE MONTH(s.importDate) = :month AND YEAR(s.importDate) = :year AND s.status = 'ACTIVE'")
List<StockImport> findActiveByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
