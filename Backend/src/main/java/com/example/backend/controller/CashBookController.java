package com.example.backend.controller;

import com.example.backend.DTO.CashBookMonthSummaryDTO;
import com.example.backend.model.CashBook;
import com.example.backend.model.Orders;
import com.example.backend.model.StockImport;
import com.example.backend.repository.CashBookRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.StockImportRepository;
import com.example.backend.service.CashBookService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cashbook")
@CrossOrigin(origins = "http://localhost:5173")
public class CashBookController {

  @Autowired private CashBookService cashBookService;
  @Autowired private OrderRepository ordersRepository;
  @Autowired private CashBookRepository cashBookRepository;
  @Autowired private StockImportRepository stockImportRepository;

  // GET list of months with cash book entries
  @GetMapping("/months")
  public ResponseEntity<List<Integer>> getAvailableMonths() {
    return ResponseEntity.ok(cashBookService.getAvailableMonths());
  }

  // GET list of years with cash book entries
  @GetMapping("/years")
  public ResponseEntity<List<Integer>> getAvailableYears() {
    return ResponseEntity.ok(cashBookService.getAvailableYears());
  }

  @GetMapping("/generate/{month}/{year}")
  public ResponseEntity<String> generateCashBookIfMissing(
      @PathVariable int month, @PathVariable int year) {
    List<Orders> orders = ordersRepository.findByMonthAndYear(month, year);
    List<StockImport> imports = stockImportRepository.findByMonthAndYear(month, year);

    System.out.println("Đơn hàng tháng " + month + "/" + year + ": " + orders.size());
    System.out.println("Nhập kho tháng " + month + "/" + year + ": " + imports.size());

    cashBookService.ensureCashBookIntegrity(month, year, orders, imports);
    return ResponseEntity.ok("Đã tạo hoặc cập nhật sổ quỹ.");
  }

  // GET cash book details by month and year
  @GetMapping("/{month}/{year}")
  public ResponseEntity<CashBookMonthSummaryDTO> getCashBookByMonth(
      @PathVariable int month, @PathVariable int year) {
    return ResponseEntity.ok(cashBookService.getCashBookByMonth(month, year));
  }

  // PUT update cash book by ID
  @PutMapping("/{id}")
  public ResponseEntity<CashBook> updateCashBook(
      @PathVariable Long id, @RequestBody CashBook updatedData) {
    return ResponseEntity.ok(cashBookService.updateCashBook(id, updatedData));
  }

  @PostMapping("/generate-auto")
  public ResponseEntity<String> generateAuto() {
    try {
      List<Orders> allOrders = ordersRepository.findAll();
      List<StockImport> allImports = stockImportRepository.findAll();

      // Collect all month-year pairs from orders and imports
      Set<String> allKeys = new HashSet<>();
      for (Orders o : allOrders) {
        if (o.getOrder_date() != null) {
          int m = o.getOrder_date().getMonth() + 1;
          int y = o.getOrder_date().getYear() + 1900;
          allKeys.add(m + "-" + y);
        }
      }
      for (StockImport i : allImports) {
        if (i.getImportDate() != null) {
          int m = i.getImportDate().getMonth() + 1;
          int y = i.getImportDate().getYear() + 1900;
          allKeys.add(m + "-" + y);
        }
      }

      // Avoid duplicates: store processed keys
      Set<String> processed = new HashSet<>();

      for (String key : allKeys) {
        if (processed.contains(key)) continue;
        processed.add(key);

        String[] parts = key.split("-");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        if (!cashBookRepository.findByMonthAndYear(month, year).isEmpty()) {
          System.out.println("Bỏ qua vì đã có sổ quỹ cho: " + month + "/" + year);
          continue;
        }

        List<Orders> orders =
            allOrders.stream()
                .filter(
                    o ->
                        o.getOrder_date() != null
                            && o.getOrder_date().getMonth() + 1 == month
                            && o.getOrder_date().getYear() + 1900 == year)
                .toList();

        List<StockImport> imports =
            allImports.stream()
                .filter(
                    i ->
                        i.getImportDate() != null
                            && i.getImportDate().getMonth() + 1 == month
                            && i.getImportDate().getYear() + 1900 == year)
                .toList();

        cashBookService.ensureCashBookIntegrity(month, year, orders, imports);
      }
      return ResponseEntity.ok("Đã tạo tất cả sổ quỹ từ dữ liệu order và import.");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
    }
  }
}
