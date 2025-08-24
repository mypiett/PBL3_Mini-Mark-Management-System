package com.example.backend.DTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class StockImportRequest {
    private String importId;
    private String employeeId;
    private Integer totalCost;
    private List<StockImportDetailRequest> stockImportDetail;

    public String getImportId() {
        return importId;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    public List<StockImportDetailRequest> getStockImportDetail() {
        return stockImportDetail;
    }

    public void setStockImportDetail(List<StockImportDetailRequest> stockImportDetail) {
        this.stockImportDetail = stockImportDetail;
    }
}