package com.example.backend.controller;

import com.example.backend.model.Orders;
import com.example.backend.service.OrdersService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrdersController {

  @Autowired private OrdersService service;

  // READ ALL
  @GetMapping
  public List<Orders> getAllOrders() {
    return service.getAllOrders();
  }

  // READ BY ID
  @GetMapping("/{id}")
  public ResponseEntity<Orders> getOrderById(@PathVariable String id) {
    Orders order = service.getOrderById(id);
    return (order != null) ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
  }

  // CREATE
  @PostMapping(consumes = {"application/json", "application/json;charset=UTF-8"})
  public Orders createOrder(@RequestBody Orders order) {
    return service.createOrder(order);
  }

  // UPDATE
  @PatchMapping("/{id}")
  public ResponseEntity<Orders> updateOrder(@PathVariable String id, @RequestBody Orders order) {
    if (!service.exists(id)) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(service.updateOrder(id, order));
  }

  // DELETE
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
    if (!service.exists(id)) return ResponseEntity.notFound().build();
    service.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}
