package com.example.backend.DTO;

import java.time.LocalDate;

public class CustomerRequest {
  public String name;
  public String phone;
  public LocalDate date_of_birth;
  public Long points;

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

  public Long getPoints() {
    return points;
  }

  public void setPoints(Long points) {
    this.points = points;
  }
}
