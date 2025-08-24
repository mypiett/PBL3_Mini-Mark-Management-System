package com.example.backend.service;

import com.example.backend.model.Orders;
import com.example.backend.model.Employees;
import com.example.backend.model.Customers;
import com.example.backend.model.Products;
import com.example.backend.model.Order_details;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.OrderDetailRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
// import java.util.UUID;

@Service
public class OrdersService {
    @Autowired
    private OrderRepository ordersRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EmployeeRepository employeesRepository;
    @Autowired
    private CustomerRepository customersRepository;
    @Autowired
    private CustomerService customerService;
    // Lấy tất cả đơn hàng
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    // Lấy đơn hàng theo ID
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

        // Tạo đối tượng Orders mới
        Orders order = new Orders();
        // Lấy order_id lớn nhất từ DB
        String lastOrderId = ordersRepository.findMaxOrderId().orElse("ORD0");
        System.out.println("Last Order ID from DB: " + lastOrderId);
        // Tách phần số: "ORD10" -> "10"
        String numberPart = lastOrderId.replaceAll("\\D+", ""); // xóa hết ký tự không phải số
        long nextId = Long.parseLong(numberPart) + 1;
        System.out.println("Next ID: " + nextId);
        // Tạo ID mới
        String newOrderId = String.format("ORD%03d", nextId);

        // Gán vào đơn hàng
        order.setOrder_id(newOrderId);
        order.setOrder_date(new Timestamp(System.currentTimeMillis()));

        // Kiểm tra và thiết lập Employee
        if (request.getEmployee() == null || request.getEmployee().getEmployee_id() == null) {
            throw new IllegalArgumentException("Employee is null or missing");
        }
        Employees employee = employeesRepository.findById(request.getEmployee().getEmployee_id())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        order.setEmployee(employee);

        // Kiểm tra và thiết lập Customer (nếu có)
        if (request.getCustomer() != null && request.getCustomer().getCustomer_id() != null) {
            Customers customer = customersRepository.findById(request.getCustomer().getCustomer_id())
                    .orElseThrow(() -> new RuntimeException(
                            "Customer not found with ID: " + request.getCustomer().getCustomer_id()));
            order.setCustomer(customer); // Lưu thông tin khách hàng vào đơn hàng
        }

        // Lưu voucher
        if (request.getVoucher() != null) {
            order.setVoucher(request.getVoucher());
        }
        // Lưu đơn hàng trước
        ordersRepository.save(order);

        // Kiểm tra và xử lý Order Details
        if (request.getOrder_detail() == null || request.getOrder_detail().isEmpty()) {
            throw new IllegalArgumentException("Order details are missing");
        }

        long totalAmount = 0;
        // long maxId = orderDetailRepository.findMaxId().orElse(0L);
        // long orderDetailId = maxId + 1;
        // Lấy max order_detail_id hiện tại từ DB
        String lastDetailId = orderDetailRepository.findMaxDetailId().orElse("ORDT001");
        System.out.println("Last OrderDetail ID from DB: " + lastDetailId);

        // Tách số thứ tự (ORDT023 → 23)
        String numberPartDetail = lastDetailId.replaceAll("\\D+", ""); // chỉ lấy số
        long nextDetailId = Long.parseLong(numberPartDetail) + 1;
        for (Order_details productRequest : request.getOrder_detail()) {
            if (productRequest.getProduct() == null || productRequest.getProduct().getProduct_id() == null) {
                throw new IllegalArgumentException("Product information is missing in order details");
            }

            Products product = productRepository.findById(productRequest.getProduct().getProduct_id())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            // Tạo Order_details mới

            Order_details orderDetail = new Order_details();
            String newOrderDetailId = String.format("ORDT%03d", nextDetailId++);
            orderDetail.setOrder_detail_id(newOrderDetailId);
            orderDetail.setOrder(order); // Liên kết với đơn hàng đã lưu
            orderDetail.setProduct(product); // Liên kết với sản phẩm\
            // orderDetail.setProduct_id(product.getProduct_id());
            // orderDetail.setProduct_name(product.getName());
            orderDetail.setProduct_quantity(productRequest.getProduct_quantity());
            orderDetail.setSubtotal(product.getPrice() * productRequest.getProduct_quantity());
            // trừ đi số lượng hàng trong kho
            product.setQuantity(product.getQuantity() - orderDetail.getProduct_quantity());
            product.setSell(product.getSell() + orderDetail.getProduct_quantity());
            productRepository.save(product);
            // Tính tổng tiền
            totalAmount += orderDetail.getSubtotal();

            // Lưu Order_details
            orderDetailRepository.save(orderDetail);
        }

        if (request.getVoucher() != null) {
            totalAmount -= totalAmount * request.getVoucher() / 100; // Giảm giá theo voucher
        }
        // cộng điểm cho khách hàng
        if (request.getCustomer() != null) {
            Customers customer = customersRepository.findById(request.getCustomer().getCustomer_id())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            long newPoints = (long) (customer.getPoints() + totalAmount / 10000);
            customer.setPoints(newPoints);
            // customerService.updatePoints(customer.getCustomer_id(), newPoints);
            

            if (request.getVoucher() != null) {
                int discount = request.getVoucher(); // phần trăm giảm giá

                // String membership = customer.getMembership_type();
                long currentPoints = customer.getPoints();
                long requiredPoints = 0;

                // Xác định số điểm cần trừ ứng với loại thành viên và mức giảm
                
                if (discount > 0) {
                requiredPoints = (discount / 5) * 500;
                }

                if (requiredPoints > 0 && currentPoints >= requiredPoints) {
                    customer.setPoints(currentPoints - requiredPoints); // trừ điểm
                }
            }
            long currentPoints = customer.getPoints();
        if(currentPoints >= 5000) {
                customer.setMembership_type("Kim cương");
            } else if (currentPoints >= 2000) {
                customer.setMembership_type("Vàng");
            } else if (currentPoints >= 700) {
                customer.setMembership_type("Bạc");
            } else if(currentPoints >= 100) {
                customer.setMembership_type("Đồng");
            } else {
                customer.setMembership_type("Chưa có hạng");
            }

            customersRepository.save(customer);
        }
         
             
        // Cập nhật tổng tiền cho đơn hàng
        order.setTotal_amount(totalAmount);

        // Lưu lại đơn hàng với tổng tiền
        return ordersRepository.save(order);

    }

    // Cập nhật đơn hàng
    public Orders updateOrder(String id, Orders order) {
        Orders existing = ordersRepository.findById(id).orElse(null);
        if (existing == null)
            return null;

        if (order.getOrder_date() != null)
            existing.setOrder_date(order.getOrder_date());
        if (order.getTotal_amount() != null)
            existing.setTotal_amount(order.getTotal_amount());
        if (order.getEmployee() != null)
            existing.setEmployee(order.getEmployee());
        if (order.getCustomer() != null)
            existing.setCustomer(order.getCustomer());
        if (order.getOrder_detail() != null)
            existing.setOrder_detail(order.getOrder_detail());

        return ordersRepository.save(existing);
    }

    // Xóa đơn hàng
    public void deleteOrder(String id) {
        ordersRepository.deleteById(id);
    }

    // Kiểm tra đơn hàng có tồn tại không
    public boolean exists(String id) {
        return ordersRepository.existsById(id);
    }
}
