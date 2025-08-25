package com.example.backend.repository;

import com.example.backend.model.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employees, String> {
  boolean existsByPhone(String phone);

  @Query("SELECT MAX(e.ind) FROM Employees e")
  Long findMaxInd();

  Employees findByUserName(String user_name);

  @Query("SELECT SUM(e.salary) FROM Employees e")
  Integer sumAllSalaries();
}
