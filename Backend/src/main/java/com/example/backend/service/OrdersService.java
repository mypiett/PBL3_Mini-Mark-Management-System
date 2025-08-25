package com.example.backend.service;

import com.example.backend.model.Customers;
import com.example.backend.model.Employees;
import com.example.backend.model.Order_details;
import com.example.backend.model.Orders;
import com.example.backend.model.Products;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.OrderDetailRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdersService {
  @Autowired private OrderRepository ordersRepository;
  @Autowired private OrderDetailRepository orderDetailRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private EmployeeRepository employeesRepository;
  @Autowired private CustomerRepository customersRepository;

  @Autowired
  public List<Orders> getAllOrders() {
    return ordersRepository.findAll();
  }

  public Orders getOrderById(String id) {
    return ordersRepository.findById(id).orElse(null);
  }

  @Transactional
  public Orders createOrder(Orders request) {
    if (request == null) {
      throw new IllegalArgumentException("Order request cannot be null");
    }
    System.out.println("employee" + request.getEmployee());
    System.out.println("order" + request.getOrder_detail().size());

    Orders order = new Orders();
    String lastOrderId = ordersRepository.findMaxOrderId().orElse("ORD0");
    System.out.println("Last Order ID from DB: " + lastOrderId);
    String numberPart = lastOrderId.replaceAll("\\D+", "");
    long nextId = Long.parseLong(numberPart) + 1;
    System.out.println("Next ID: " + nextId);
    String newOrderId = String.format("ORD%03d", nextId);

    order.setOrder_id(newOrderId);
    order.setOrder_date(new Timestamp(System.currentTimeMillis()));

    if (request.getEmployee() == null || request.getEmployee().getEmployee_id() == null) {
      throw new IllegalArgumentException("Employee is null or missing");
    }
    Employees employee =
        employeesRepository
            .findById(request.getEmployee().getEmployee_id())
            .orElseThrow(() -> new RuntimeException("Employee not found"));
    order.setEmployee(employee);

    if (request.getCustomer() != null && request.getCustomer().getCustomer_id() != null) {
      Customers customer =
          customersRepository
              .findById(request.getCustomer().getCustomer_id())
              .orElseThrow(
                  () ->
                      new RuntimeException(
                          "Customer not found with ID: " + request.getCustomer().getCustomer_id()));
      order.setCustomer(customer);
    }

    if (request.getVoucher() != null) {
      order.setVoucher(request.getVoucher());
    }

    ordersRepository.save(order);

    if (request.getOrder_detail() == null || request.getOrder_detail().isEmpty()) {
      throw new IllegalArgumentException("Order details are missing");
    }

    long totalAmount = 0;
    String lastDetailId = orderDetailRepository.findMaxDetailId().orElse("ORDT001");
    System.out.println("Last OrderDetail ID from DB: " + lastDetailId);

    String numberPartDetail = lastDetailId.replaceAll("\\D+", "");
    long nextDetailId = Long.parseLong(numberPartDetail) + 1;
    for (Order_details productRequest : request.getOrder_detail()) {
      if (productRequest.getProduct() == null
          || productRequest.getProduct().getProduct_id() == null) {
        throw new IllegalArgumentException("Product information is missing in order details");
      }

      Products product =
          productRepository
              .findById(productRequest.getProduct().getProduct_id())
              .orElseThrow(() -> new RuntimeException("Product not found"));

      Order_details orderDetail = new Order_details();
      String newOrderDetailId = String.format("ORDT%03d", nextDetailId++);
      orderDetail.setOrder_detail_id(newOrderDetailId);
      orderDetail.setOrder(order);
      orderDetail.setProduct(product);
      orderDetail.setProduct_quantity(productRequest.getProduct_quantity());
      orderDetail.setSubtotal(product.getPrice() * productRequest.getProduct_quantity());
      product.setQuantity(product.getQuantity() - orderDetail.getProduct_quantity());
      product.setSell(product.getSell() + orderDetail.getProduct_quantity());
      productRepository.save(product);
      totalAmount += orderDetail.getSubtotal();
      orderDetailRepository.save(orderDetail);
    }

    if (request.getVoucher() != null) {
      totalAmount -= totalAmount * request.getVoucher() / 100;
    }
    if (request.getCustomer() != null) {
      Customers customer =
          customersRepository
              .findById(request.getCustomer().getCustomer_id())
              .orElseThrow(() -> new RuntimeException("Customer not found"));
      long newPoints = (long) (customer.getPoints() + totalAmount / 10000);
      customer.setPoints(newPoints);
      if (request.getVoucher() != null) {
        int discount = request.getVoucher();
        long currentPoints = customer.getPoints();
        long requiredPoints = 0;
        if (discount > 0) {
          requiredPoints = (discount / 5) * 500;
        }

        if (requiredPoints > 0 && currentPoints >= requiredPoints) {
          customer.setPoints(currentPoints - requiredPoints);
        }
      }
      long currentPoints = customer.getPoints();
      if (currentPoints >= 5000) {
        customer.setMembership_type("Kim cương");
      } else if (currentPoints >= 2000) {
        customer.setMembership_type("Vàng");
      } else if (currentPoints >= 700) {
        customer.setMembership_type("Bạc");
      } else if (currentPoints >= 100) {
        customer.setMembership_type("Đồng");
      } else {
        customer.setMembership_type("Chưa có hạng");
      }

      customersRepository.save(customer);
    }

    order.setTotal_amount(totalAmount);
    return ordersRepository.save(order);
  }

  public Orders updateOrder(String id, Orders order) {
    Orders existing = ordersRepository.findById(id).orElse(null);
    if (existing == null) return null;

    if (order.getOrder_date() != null) existing.setOrder_date(order.getOrder_date());
    if (order.getTotal_amount() != null) existing.setTotal_amount(order.getTotal_amount());
    if (order.getEmployee() != null) existing.setEmployee(order.getEmployee());
    if (order.getCustomer() != null) existing.setCustomer(order.getCustomer());
    if (order.getOrder_detail() != null) existing.setOrder_detail(order.getOrder_detail());

    return ordersRepository.save(existing);
  }

  public void deleteOrder(String id) {
    ordersRepository.deleteById(id);
  }

  public boolean exists(String id) {
    return ordersRepository.existsById(id);
  }
}
