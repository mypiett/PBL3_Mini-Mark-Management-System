package com.example.backend.service;


import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


@Service
public class StockImportService {


    @Autowired
    private StockImportRepository stockImportRepository;


    @Autowired
    private StockImportDetailsRepository detailRepository;


    @Autowired
    private ProductRepository productsRepository;


    // Sinh importId mới: imp01, imp02,...
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
                    if (num > max)
                        max = num;
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


    // Tìm số nhỏ nhất chưa dùng
    private int findSmallestMissingNumber(HashSet<Integer> existing) {
        int i = 1;
        while (existing.contains(i))
            i++;
        return i;
    }


    // Lấy các số ID đã dùng cho chi tiết đơn nhập
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


    // Tạo mới đơn nhập
    public StockImport createStockImport(StockImport stockImport) {
        // Gán ID tạm thời nếu chưa có
        if (stockImport.getImportId() == null || stockImport.getImportId().startsWith("temp-")) {
            stockImport.setImportId("temp-" + UUID.randomUUID());
        }


        stockImport.setImportDate(new Timestamp(System.currentTimeMillis()));
        stockImport.setStatus("DRAFT");


        // Gán nhân viên
        Employees employee = new Employees();
        employee.setEmployee_id(stockImport.getEmployeeId());
        stockImport.setEmployee(employee);


        // Gán dữ liệu sản phẩm cho từng chi tiết
        for (StockImportDetails detail : stockImport.getStockImportDetail()) {
            if (detail.getImportDetailId() == null || detail.getImportDetailId().startsWith("temp-")) {
                detail.setImportDetailId("temp-" + UUID.randomUUID());
            }


            String productId = detail.getProduct().getProduct_id();
            Products product = productsRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));


            detail.setProduct(product);
            detail.setStockImport(stockImport);
        }


        // Gán ID chính thức cho đơn và chi tiết
        stockImport.setImportId(generateNewImportId());
        HashSet<Integer> existingDetailNums = getExistingImportDetailNumbers();


        for (StockImportDetails detail : stockImport.getStockImportDetail()) {
            int newIdNum = findSmallestMissingNumber(existingDetailNums);
            detail.setImportDetailId(String.format("impd%02d", newIdNum));
            existingDetailNums.add(newIdNum);
        }


        // Lưu đơn nhập
        return stockImportRepository.save(stockImport);
    }


    // Cập nhật tổng tiền cho đơn nhập
    public void updateTotalPrice(String importId) {
        // Lấy tất cả chi tiết còn lại thuộc đơn nhập này
        List<StockImportDetails> details = detailRepository.findByStockImport_ImportId(importId);


        // Tổng tiền là tổng của các totalPrice từng dòng chi tiết
        long total = details.stream()
                .mapToLong(StockImportDetails::getTotalPrice)
                .sum();


        // Lấy đơn nhập tương ứng
        StockImport importOrder = stockImportRepository.findById(importId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nhập với ID: " + importId));


        // Cập nhật lại tổng tiền và lưu
        importOrder.setTotalCost(total);
        stockImportRepository.save(importOrder);
    }


}



