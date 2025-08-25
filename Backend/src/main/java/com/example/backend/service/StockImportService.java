package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockImportService {

  @Autowired private StockImportRepository stockImportRepository;

  @Autowired private StockImportDetailsRepository detailRepository;

  @Autowired private ProductRepository productsRepository;

  public String generateNewImportId() {
    List<StockImport> allImports = stockImportRepository.findAll();
    int max = 0;
    HashSet<Integer> existing = new HashSet<>();

    for (StockImport si : allImports) {
      String id = si.getImportId();
      if (id != null && id.startsWith("imp")) {
        try {
          int num = Integer.parseInt(id.substring(3));
          existing.add(num);
          if (num > max) max = num;
        } catch (NumberFormatException ignored) {
        }
      }
    }

    for (int i = 1; i <= max; i++) {
      if (!existing.contains(i)) {
        return String.format("imp%02d", i);
      }
    }

    return String.format("imp%02d", max + 1);
  }

  private int findSmallestMissingNumber(HashSet<Integer> existing) {
    int i = 1;
    while (existing.contains(i)) i++;
    return i;
  }

  private HashSet<Integer> getExistingImportDetailNumbers() {
    List<StockImport> allImports = stockImportRepository.findAll();
    HashSet<Integer> existing = new HashSet<>();
    for (StockImport si : allImports) {
      if (si.getStockImportDetail() != null) {
        for (StockImportDetails detail : si.getStockImportDetail()) {
          String id = detail.getImportDetailId();
          if (id != null && id.startsWith("impd")) {
            try {
              int num = Integer.parseInt(id.substring(4));
              existing.add(num);
            } catch (NumberFormatException ignored) {
            }
          }
        }
      }
    }
    return existing;
  }

  public StockImport createStockImport(StockImport stockImport) {
    if (stockImport.getImportId() == null || stockImport.getImportId().startsWith("temp-")) {
      stockImport.setImportId("temp-" + UUID.randomUUID());
    }

    stockImport.setImportDate(new Timestamp(System.currentTimeMillis()));
    stockImport.setStatus("DRAFT");

    Employees employee = new Employees();
    employee.setEmployee_id(stockImport.getEmployeeId());
    stockImport.setEmployee(employee);

    for (StockImportDetails detail : stockImport.getStockImportDetail()) {
      if (detail.getImportDetailId() == null || detail.getImportDetailId().startsWith("temp-")) {
        detail.setImportDetailId("temp-" + UUID.randomUUID());
      }
      String productId = detail.getProduct().getProduct_id();
      Products product =
          productsRepository
              .findById(productId)
              .orElseThrow(
                  () -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
      detail.setProduct(product);
      detail.setStockImport(stockImport);
    }

    stockImport.setImportId(generateNewImportId());
    HashSet<Integer> existingDetailNums = getExistingImportDetailNumbers();

    for (StockImportDetails detail : stockImport.getStockImportDetail()) {
      int newIdNum = findSmallestMissingNumber(existingDetailNums);
      detail.setImportDetailId(String.format("impd%02d", newIdNum));
      existingDetailNums.add(newIdNum);
    }

    return stockImportRepository.save(stockImport);
  }

  public void updateTotalPrice(String importId) {
    List<StockImportDetails> details = detailRepository.findByStockImport_ImportId(importId);
    long total = details.stream().mapToLong(StockImportDetails::getTotalPrice).sum();
    StockImport importOrder =
        stockImportRepository
            .findById(importId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nhập với ID: " + importId));
    importOrder.setTotalCost(total);
    stockImportRepository.save(importOrder);
  }
}
