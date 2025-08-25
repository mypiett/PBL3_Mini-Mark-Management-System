package com.example.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cash_book", uniqueConstraints = @UniqueConstraint(columnNames = {"month", "year"}))
public class CashBook {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private BigDecimal salary = BigDecimal.ZERO;

  @Column(nullable = false)
  private BigDecimal electricity = BigDecimal.ZERO;

  @Column(nullable = false)
  private BigDecimal water = BigDecimal.ZERO;

  @Column(nullable = false)
  private BigDecimal rent = BigDecimal.ZERO;

  @Column(name = "total_income", nullable = false)
  private BigDecimal totalIncome = BigDecimal.ZERO;

  @Column(name = "total_expense", nullable = false)
  private BigDecimal totalExpense = BigDecimal.ZERO;

  @Column(name = "profit_before_cost", nullable = false)
  private BigDecimal profitBeforeCost = BigDecimal.ZERO;

  @Column(name = "actual_profit", nullable = false)
  private BigDecimal actualProfit = BigDecimal.ZERO;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at", nullable = false)
  private Date createdAt = new Date();

  @OneToMany(mappedBy = "cashBook", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CashBookDetail> details;

  @Column(name = "month", nullable = false)
  private int month;

  @Column(name = "year", nullable = false)
  private int year;

  // ===== GETTERS & SETTERS =====
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BigDecimal getSalary() {
    return salary;
  }

  public void setSalary(BigDecimal salary) {
    this.salary = salary;
  }

  public BigDecimal getElectricity() {
    return electricity;
  }

  public void setElectricity(BigDecimal electricity) {
    this.electricity = electricity;
  }

  public BigDecimal getWater() {
    return water;
  }

  public void setWater(BigDecimal water) {
    this.water = water;
  }

  public BigDecimal getRent() {
    return rent;
  }

  public void setRent(BigDecimal rent) {
    this.rent = rent;
  }

  public BigDecimal getTotalIncome() {
    return totalIncome;
  }

  public void setTotalIncome(BigDecimal totalIncome) {
    this.totalIncome = totalIncome;
  }

  public BigDecimal getTotalExpense() {
    return totalExpense;
  }

  public void setTotalExpense(BigDecimal totalExpense) {
    this.totalExpense = totalExpense;
  }

  public BigDecimal getProfitBeforeCost() {
    return profitBeforeCost;
  }

  public void setProfitBeforeCost(BigDecimal profitBeforeCost) {
    this.profitBeforeCost = profitBeforeCost;
  }

  public BigDecimal getActualProfit() {
    return actualProfit;
  }

  public void setActualProfit(BigDecimal actualProfit) {
    this.actualProfit = actualProfit;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public List<CashBookDetail> getDetails() {
    return details;
  }

  public void setDetails(List<CashBookDetail> details) {
    this.details = details;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }
}
