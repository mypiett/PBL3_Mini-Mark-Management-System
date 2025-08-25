package com.example.backend.DTO;

import java.time.LocalDate;

public class EmployeeRequest {
  public String name;
  public String phone;
  public LocalDate date_of_birth;
  public String user_name;
  public String password;
  public String position;
  public Integer salary;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public LocalDate getDate_of_birth() {
    return date_of_birth;
  }

  public void setDate_of_birth(LocalDate date_of_birth) {
    this.date_of_birth = date_of_birth;
  }

  public String getUser_name() {
    return user_name;
  }

  public void setUser_name(String user_name) {
    this.user_name = user_name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public Integer getSalary() {
    return salary;
  }

  public void setSalary(Integer salary) {
    this.salary = salary;
  }
}
