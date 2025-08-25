package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class Orders {
  @Id private String order_id;

  private Timestamp order_date;
  private Long total_amount;
  private Integer voucher;

  @ManyToOne
  @JoinColumn(name = "employee_id")
  private Employees employee;

  @ManyToOne
  @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = true)
  private Customers customer;

  @OneToMany(mappedBy = "order")
  @JsonManagedReference
  private List<Order_details> order_detail;

  public String getOrder_id() {
    return order_id;
  }

  public void setOrder_id(String order_id) {
    this.order_id = order_id;
  }

  public Integer getVoucher() {
    return voucher;
  }

  public void setVoucher(Integer voucher) {
    this.voucher = voucher;
  }

  public Timestamp getOrder_date() {
    return order_date;
  }

  public void setOrder_date(Timestamp order_date) {
    this.order_date = order_date;
  }

  public Long getTotal_amount() {
    return total_amount;
  }

  public void setTotal_amount(Long total_amount) {
    this.total_amount = total_amount;
  }

  public Employees getEmployee() {
    return employee;
  }

  public void setEmployee(Employees employee) {
    this.employee = employee;
  }

  public Customers getCustomer() {
    return customer;
  }

  public void setCustomer(Customers customer) {
    this.customer = customer;
  }

  public List<Order_details> getOrder_detail() {
    return order_detail;
  }

  public void setOrder_detail(List<Order_details> order_detail) {
    this.order_detail = order_detail;
  }
}
