package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Employees {
  @Id private String employee_id;

  @Column(unique = true)
  private String phone;

  private String name;
  private String position;

  @Column(unique = true)
  private String userName;

  private String password;
  private Integer salary;
  private LocalDate date_of_birth;

  @Column(unique = true)
  private Long ind;

  @OneToMany(mappedBy = "employee")
  @JsonIgnore
  private List<Orders> order;

  @OneToMany(mappedBy = "employee")
  @JsonManagedReference
  private List<StockImport> stockImport;

  public String getEmployee_id() {
    return employee_id;
  }

  public void setEmployee_id(String employee_id) {
    this.employee_id = employee_id;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getUser_name() {
    return userName;
  }

  public void setUser_name(String user_name) {
    this.userName = user_name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getSalary() {
    return salary;
  }

  public void setSalary(Integer salary) {
    this.salary = salary;
  }

  public LocalDate getDate_of_birth() {
    return date_of_birth;
  }

  public void setDate_of_birth(LocalDate date_of_birth) {
    this.date_of_birth = date_of_birth;
  }

  public Long getInd() {
    return ind;
  }

  public void setInd(Long ind) {
    this.ind = ind;
  }

  public List<Orders> getOrder() {
    return order;
  }

  public void setOrder(List<Orders> order) {
    this.order = order;
  }

  public List<StockImport> getStockImport() {
    return stockImport;
  }

  public void setStockImport(List<StockImport> stockImport) {
    this.stockImport = stockImport;
  }
}
