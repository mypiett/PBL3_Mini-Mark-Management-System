package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_import")
public class StockImport {

  @Id private String importId;

  private Timestamp importDate;

  private long totalCost;

  private String status;

  @ManyToOne
  @JoinColumn(name = "employeeId")
  @JsonBackReference
  private Employees employee;

  @Transient private String employeeId;

  @OneToMany(
      mappedBy = "stockImport",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  @JsonManagedReference(value = "import-stockDetail")
  private List<StockImportDetails> stockImportDetail = new ArrayList<>();

  @PostLoad
  public void setEmployee_id() {
    if (employee != null) {
      this.employeeId = employee.getEmployee_id();
    }
  }

  public String getImportId() {
    return importId;
  }

  public void setImportId(String importId) {
    this.importId = importId;
  }

  public Timestamp getImportDate() {
    return importDate;
  }

  public void setImportDate(Timestamp importDate) {
    this.importDate = importDate;
  }

  public long getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(long totalCost) {
    this.totalCost = totalCost;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Employees getEmployee() {
    return employee;
  }

  public void setEmployee(Employees employee) {
    this.employee = employee;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public List<StockImportDetails> getStockImportDetail() {
    return stockImportDetail;
  }

  public void setStockImportDetail(List<StockImportDetails> stockImportDetail) {
    this.stockImportDetail = stockImportDetail;
  }
}
