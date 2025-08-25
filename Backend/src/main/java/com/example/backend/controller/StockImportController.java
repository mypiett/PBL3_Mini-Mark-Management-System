package com.example.backend.controller;

import com.example.backend.model.Orders;
import com.example.backend.model.Products;
import com.example.backend.model.StockImport;
import com.example.backend.model.StockImportDetails;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.StockImportRepository;
import com.example.backend.service.CashBookService;
import com.example.backend.service.StockImportService;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/StockImport")
public class StockImportController {

  @Autowired private StockImportRepository stockImportRepository;

  @Autowired private OrderRepository orderRepository;

  @Autowired private CashBookService cashBookService;
  @Autowired private ProductRepository productRepository;

  @Autowired private StockImportService stockImportService;

  @GetMapping
  public List<StockImport> getAllStockImports() {
    return stockImportRepository.findAll();
  }

  @GetMapping("/{importId}")
  public StockImport getStockImportById(@PathVariable String importId) {
    return stockImportRepository.findById(importId).orElse(null);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createImport(@RequestBody StockImport stockImport) {
    try {
      StockImport saved = stockImportService.createStockImport(stockImport);
      return ResponseEntity.ok("Tạo đơn nhập thành công với ID: " + saved.getImportId());
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
    }
  }

  @PutMapping("/{importId}/import-stock")
  public ResponseEntity<?> importStock(@PathVariable String importId) {
    Optional<StockImport> stockImportOptional = stockImportRepository.findById(importId);

    if (stockImportOptional.isEmpty()) {
      return ResponseEntity.badRequest().body("Không tìm thấy đơn nhập hàng.");
    }

    StockImport stockImport = stockImportOptional.get();

    if (!"DRAFT".equals(stockImport.getStatus())) {
      return ResponseEntity.badRequest().body("Đơn nhập hàng không có trạng thái DRAFT.");
    }

    stockImport.setStatus("ACTIVE");

    for (StockImportDetails detail : stockImport.getStockImportDetail()) {
      int totalPrice = detail.getUnitPrice() * detail.getQuantity();
      detail.setTotalPrice(totalPrice);

      Products product = detail.getProduct();
      product.setQuantity(product.getQuantity() + detail.getQuantity());

      if ("DRAFT".equals(product.getStatus())) {
        product.setStatus("ACTIVE");
      }
      productRepository.save(product);
    }

    int totalCost =
        stockImport.getStockImportDetail().stream()
            .mapToInt(StockImportDetails::getTotalPrice)
            .sum();

    stockImport.setTotalCost(totalCost);
    stockImport.setStatus("ACTIVE");
    stockImportRepository.save(stockImport);

    Calendar cal = Calendar.getInstance();
    cal.setTime(stockImport.getImportDate());
    int month = cal.get(Calendar.MONTH) + 1;
    int year = cal.get(Calendar.YEAR);

    List<Orders> ordersInMonth = orderRepository.findByMonthAndYear(month, year);
    List<StockImport> activeImports =
        stockImportRepository.findByMonthAndYear(month, year).stream()
            .filter(i -> "ACTIVE".equalsIgnoreCase(i.getStatus()))
            .toList();

    cashBookService.ensureCashBookIntegrity(month, year, ordersInMonth, activeImports);

    return ResponseEntity.ok("Nhập kho thành công!");
  }

  @PutMapping("/update-total/{importId}")
  public ResponseEntity<Void> updateTotal(@PathVariable String importId) {
    stockImportService.updateTotalPrice(importId);
    return ResponseEntity.noContent().build();
  }
}
