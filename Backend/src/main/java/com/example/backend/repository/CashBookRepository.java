// ====================
package com.example.backend.repository;

import com.example.backend.model.CashBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CashBookRepository extends JpaRepository<CashBook, Long> {

   @Query("SELECT c FROM CashBook c WHERE c.month = :month AND c.year = :year")
List<CashBook> findByMonthAndYear(@Param("month") int month, @Param("year") int year);


    @Query("SELECT DISTINCT c.month FROM CashBook c ORDER BY c.month")
List<Integer> findDistinctMonths();

@Query("SELECT DISTINCT c.year FROM CashBook c ORDER BY c.year")
List<Integer> findDistinctYears();

}
