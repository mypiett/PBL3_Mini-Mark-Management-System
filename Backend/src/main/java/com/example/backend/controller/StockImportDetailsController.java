package com.example.backend.controller;

import com.example.backend.model.StockImport;
import com.example.backend.model.StockImportDetails;
import com.example.backend.repository.StockImportDetailsRepository;
import com.example.backend.repository.StockImportRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/StockImportDetails")
@CrossOrigin(origins = "http://localhost:5173")
public class StockImportDetailsController {

  @Autowired private StockImportDetailsRepository detailsRepository;

  @Autowired private StockImportRepository stockImportRepository;

  @GetMapping("/productIds/{importId}")
  public List<String> getProductIdsByImportId(@PathVariable String importId) {
    return detailsRepository.findProductIdsByImportId(importId);
  }

  @GetMapping("/details/{importId}")
  public List<StockImportDetails> getDetailsByImportId(@PathVariable String importId) {
    return detailsRepository.findByImportId(importId);
  }

  @DeleteMapping("/delete/{importDetailId}")
  public ResponseEntity<String> deleteStockImportDetail(@PathVariable String importDetailId) {
    try {
      Optional<StockImportDetails> detailOpt = detailsRepository.findById(importDetailId);
      if (!detailOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Không tìm thấy chi tiết đơn nhập với ID: " + importDetailId);
      }

      StockImportDetails detail = detailOpt.get();
      StockImport parentImport = detail.getStockImport();

      parentImport.getStockImportDetail().remove(detail);
      stockImportRepository.save(parentImport);
      detailsRepository.deleteById(importDetailId);
      return ResponseEntity.ok("Đã xoá chi tiết đơn nhập với ID: " + importDetailId);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Lỗi khi xoá chi tiết đơn nhập: " + e.getMessage());
    }
  }
}
