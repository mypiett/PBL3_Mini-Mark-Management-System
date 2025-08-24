package com.example.backend.controller;

import com.example.backend.model.StockImport;
import com.example.backend.model.StockImportDetails;
import com.example.backend.repository.StockImportDetailsRepository;
import com.example.backend.repository.StockImportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/StockImportDetails")
@CrossOrigin(origins = "http://localhost:5173")
public class StockImportDetailsController {

    @Autowired
    private StockImportDetailsRepository detailsRepository;

    @Autowired
    private StockImportRepository stockImportRepository;

    // Lấy danh sách các productId từ một đơn nhập
    @GetMapping("/productIds/{importId}")
    public List<String> getProductIdsByImportId(@PathVariable String importId) {
        return detailsRepository.findProductIdsByImportId(importId);
    }

    // Lấy chi tiết đơn nhập dựa trên importId
    @GetMapping("/details/{importId}")
    public List<StockImportDetails> getDetailsByImportId(@PathVariable String importId) {
        return detailsRepository.findByImportId(importId);
    }

    // Xóa chi tiết đơn nhập
    @DeleteMapping("/delete/{importDetailId}")
    public ResponseEntity<String> deleteStockImportDetail(@PathVariable String importDetailId) {
        try {
            // Tìm chi tiết cần xoá
            Optional<StockImportDetails> detailOpt = detailsRepository.findById(importDetailId);
            if (!detailOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy chi tiết đơn nhập với ID: " + importDetailId);
            }


            StockImportDetails detail = detailOpt.get();
            StockImport parentImport = detail.getStockImport();


            // Xóa chi tiết khỏi danh sách
            parentImport.getStockImportDetail().remove(detail);
            stockImportRepository.save(parentImport);  // Lưu lại StockImport sau khi xoá chi tiết


            detailsRepository.deleteById(importDetailId);  // Xóa chi tiết khỏi DB


            return ResponseEntity.ok("Đã xoá chi tiết đơn nhập với ID: " + importDetailId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xoá chi tiết đơn nhập: " + e.getMessage());
        }
    }

}