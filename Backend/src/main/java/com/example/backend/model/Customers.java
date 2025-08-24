package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
public class Customers {
    @Id
    private String customer_id;
    
    @Column(unique = true)
    private String phone; 

    private String name;
    private LocalDate date_of_birth;
    private Long points;
    private String membership_type;
    // private String customer_group;

    @Column(unique = true)
    private Long ind; 

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Orders> order;



    public String getCustomer_id() { return customer_id; }
public void setCustomer_id(String customer_id) { this.customer_id = customer_id; }

public String getPhone() { return phone; }
public void setPhone(String phone) { this.phone = phone; }

public String getName() { return name; }
public void setName(String name) { this.name = name; }

public LocalDate getDate_of_birth() { return date_of_birth; }
public void setDate_of_birth(LocalDate date_of_birth) { this.date_of_birth = date_of_birth; }

public Long getPoints() { return points; }
public void setPoints(Long points) { this.points = points; }

public String getMembership_type() { return membership_type; }
public void setMembership_type(String membership_type) { this.membership_type = membership_type; }

public Long getInd() { return ind; }
public void setInd(Long ind) { this.ind = ind; }

public List<Orders> getOrder() { return order; }
public void setOrder(List<Orders> order) { this.order = order; }


}