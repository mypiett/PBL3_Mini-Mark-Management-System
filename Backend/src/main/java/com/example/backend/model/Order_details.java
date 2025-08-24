package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonIgnore;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Order_details {
    @Id
    private String order_detail_id; // Khóa chính duy nhất cho bảng Order_details

    @Column(name = "order_id", nullable = false, insertable = false, updatable = false)
    private String order_id; // Lưu trữ order_id từ Orders

    private Integer product_quantity;
    private Integer subtotal;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Orders order; // Liên kết với Orders

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false) 
    @JsonBackReference(value = "product-orderDetail")
    private Products product; // Liên kết với Products

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
    public void setOrder_detail_id(String order_detail_id) {
        this.order_detail_id = order_detail_id;
    }
    public String getOrder_detail_id() {
        return order_detail_id;
    }
    // public void setProduct_id(String product_id) {
    //     this.product.getProduct_id() = product_id;
    // }
    @Transient
    public String getProduct_id() {
        return product != null ? product.getProduct_id() : null;
    }
    @Transient
    public String getProduct_name() {
        return product != null ? product.getName() : null;
    }
    public Integer getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(Integer product_quantity) {
        this.product_quantity = product_quantity;
    }

    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }
}