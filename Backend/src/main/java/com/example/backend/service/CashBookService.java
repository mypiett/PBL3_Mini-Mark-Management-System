package com.example.backend.service;

import com.example.backend.DTO.CashBookEntryDTO;
import com.example.backend.DTO.CashBookMonthSummaryDTO;
import com.example.backend.model.CashBook;
import com.example.backend.model.CashBookDetail;
import com.example.backend.model.Orders;
import com.example.backend.model.StockImport;
import com.example.backend.repository.CashBookDetailRepository;
import com.example.backend.repository.CashBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.StockImportRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;

@Service
public class CashBookService {

    @Autowired
    private CashBookRepository cashBookRepository;

    @Autowired
    private CashBookDetailRepository detailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CashBookDetailService cashBookDetailService;

    @Autowired
    private StockImportRepository stockImportRepository;
    @Autowired
    private EmployeeService employeeService;

    public List<Integer> getAvailableMonths() {
        return cashBookRepository.findDistinctMonths();
    }

    public List<Integer> getAvailableYears() {
        return cashBookRepository.findDistinctYears();
    }
    public CashBookMonthSummaryDTO getCashBookByMonth(int month, int year) {
    // Tìm hoặc tạo sổ quỹ
    List<CashBook> books = cashBookRepository.findByMonthAndYear(month, year);
CashBook cashBook;

if (books.isEmpty()) {
    cashBook = new CashBook();
    cashBook.setMonth(month);
    cashBook.setYear(year);
    cashBook.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    cashBook = cashBookRepository.save(cashBook);
} else {
    cashBook = books.get(0);
}

    // Lấy dữ liệu orders và imports trong tháng
    List<Orders> ordersInMonth = orderRepository.findByMonthAndYear(month, year);
    List<StockImport> importsInMonth = stockImportRepository.findActiveByMonthAndYear(month, year);


    // Đồng bộ chi tiết sổ quỹ
    cashBookDetailService.syncCashBookDetailsByMonth(month, year, ordersInMonth, importsInMonth, cashBook);

    // Kiểm tra nếu là tháng hiện tại thì cập nhật chi phí + lợi nhuận
    Calendar now = Calendar.getInstance();
    boolean isCurrentMonthYear = (now.get(Calendar.MONTH) + 1 == month) && (now.get(Calendar.YEAR) == year);

    if (isCurrentMonthYear) {
        Integer salaryInt = employeeService.getTotalSalary();
        BigDecimal salary = BigDecimal.valueOf(salaryInt != null ? salaryInt : 0);

        BigDecimal rent = cashBook.getRent() != null ? cashBook.getRent() : BigDecimal.valueOf(3_000_000);
        BigDecimal electricity = cashBook.getElectricity() != null ? cashBook.getElectricity() : BigDecimal.valueOf(1_000_000);
        BigDecimal water = cashBook.getWater() != null ? cashBook.getWater() : BigDecimal.valueOf(800_000);

        BigDecimal totalIncome = ordersInMonth.stream()
                .map(o -> o.getTotal_amount() != null ? BigDecimal.valueOf(o.getTotal_amount()) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = importsInMonth.stream()
                .map(i -> BigDecimal.valueOf(i.getTotalCost()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profitBeforeCost = totalIncome.subtract(totalExpense);
        BigDecimal actualProfit = profitBeforeCost.subtract(rent.add(electricity).add(water).add(salary));

        cashBook.setSalary(salary);
        cashBook.setRent(rent);
        cashBook.setElectricity(electricity);
        cashBook.setWater(water);
        cashBook.setTotalIncome(totalIncome);
        cashBook.setTotalExpense(totalExpense);
        cashBook.setProfitBeforeCost(profitBeforeCost);
        cashBook.setActualProfit(actualProfit);

        cashBookRepository.save(cashBook);
    }

    // Tạo danh sách entry chi tiết
    List<CashBookDetail> details = detailRepository.findByCashBookId(cashBook.getId());
    List<CashBookEntryDTO> entries = new ArrayList<>();

    for (CashBookDetail detail : details) {
        String sourceId, type, direction;
        if (detail.getOrder() != null) {
            sourceId = detail.getOrder().getOrder_id() != null ? detail.getOrder().getOrder_id() : "UNKNOWN_ORDER";
            type = "ORDER";
            direction = "IN";
        } else if (detail.getStockImport() != null) {
            sourceId = detail.getStockImport().getImportId() != null ? detail.getStockImport().getImportId() : "UNKNOWN_IMPORT";
            type = "IMPORT";
            direction = "OUT";
        } else {
            sourceId = "NO_SOURCE";
            type = "UNKNOWN";
            direction = "UNKNOWN";
        }

        entries.add(
            CashBookEntryDTO.builder()
                .entryId(detail.getId())
                .sourceId(sourceId)
                .type(type)
                .direction(direction)
                .build()
        );
    }

    return CashBookMonthSummaryDTO.builder()
            .id(cashBook.getId())
            .month(month)
            .year(year)
            .totalIncome(cashBook.getTotalIncome())
            .totalExpense(cashBook.getTotalExpense())
            .salary(cashBook.getSalary())
            .electricity(cashBook.getElectricity())
            .water(cashBook.getWater())
            .rent(cashBook.getRent())
            .profitBeforeCost(cashBook.getProfitBeforeCost())
            .actualProfit(cashBook.getActualProfit())
            .entries(entries)
            .isCurrentMonthYear(isCurrentMonthYear)
            .build();
}

    // public CashBookMonthSummaryDTO getCashBookByMonth(int month, int year) {
    // List<CashBook> books = cashBookRepository.findByMonthAndYear(month, year);
    // if (books.isEmpty()) return null;

    // CashBook cashBook = books.get(0);
    
    // // Kiểm tra xem có phải tháng/năm hiện tại không
    // Calendar now = Calendar.getInstance();
    // boolean isCurrentMonthYear = (now.get(Calendar.MONTH) + 1 == month) && (now.get(Calendar.YEAR) == year);
    
    // if (isCurrentMonthYear) {
    //     // Tính lại lương từ EmployeeService
    //     Integer salaryInt = employeeService.getTotalSalary();
    //     BigDecimal salary = BigDecimal.valueOf(salaryInt != null ? salaryInt : 0);
    //     cashBook.setSalary(salary);
        
    //     // Tính lại lợi nhuận
    //     BigDecimal totalIncome = cashBook.getTotalIncome() != null ? cashBook.getTotalIncome() : BigDecimal.ZERO;
    //     BigDecimal totalExpense = cashBook.getTotalExpense() != null ? cashBook.getTotalExpense() : BigDecimal.ZERO;
    //     BigDecimal rent = cashBook.getRent() != null ? cashBook.getRent() : BigDecimal.ZERO;
    //     BigDecimal electricity = cashBook.getElectricity() != null ? cashBook.getElectricity() : BigDecimal.ZERO;
    //     BigDecimal water = cashBook.getWater() != null ? cashBook.getWater() : BigDecimal.ZERO;
        
    //     BigDecimal profitBeforeCost = totalIncome.subtract(totalExpense);
    //     BigDecimal actualProfit = profitBeforeCost.subtract(rent.add(electricity).add(water).add(salary));
        
    //     cashBook.setProfitBeforeCost(profitBeforeCost);
    //     cashBook.setActualProfit(actualProfit);
        
    //     cashBookRepository.save(cashBook); // Lưu sổ quỹ đã cập nhật
    // }

//     List<CashBookDetail> details = detailRepository.findByCashBookId(cashBook.getId());
//     List<CashBookEntryDTO> entries = new ArrayList<>();

//     for (CashBookDetail detail : details) {
//         String type = null;
//         String direction = null;
//         String sourceId;

//         if (detail.getOrder() != null) {
//             sourceId = detail.getOrder().getOrder_id() != null ? detail.getOrder().getOrder_id() : "UNKNOWN_ORDER";
//             type = "ORDER";
//             direction = "IN";
//         } else if (detail.getStockImport() != null) {
//             sourceId = detail.getStockImport().getImportId() != null ? detail.getStockImport().getImportId() : "UNKNOWN_IMPORT";
//             type = "IMPORT";
//             direction = "OUT";
//         } else {
//             sourceId = "NO_SOURCE";
//             type = "UNKNOWN";
//             direction = "UNKNOWN";
//         }

//         entries.add(
//             CashBookEntryDTO.builder()
//                 .entryId(detail.getId())
//                 .sourceId(sourceId)
//                 .type(type)
//                 .direction(direction)
//                 .build()
//         );
//     }

//     return CashBookMonthSummaryDTO.builder()
//             .id(cashBook.getId())
//             .month(month)
//             .year(year)
//             .totalIncome(cashBook.getTotalIncome())
//             .totalExpense(cashBook.getTotalExpense())
//             .salary(cashBook.getSalary())
//             .electricity(cashBook.getElectricity())
//             .water(cashBook.getWater())
//             .rent(cashBook.getRent())
//             .profitBeforeCost(cashBook.getProfitBeforeCost())
//             .actualProfit(cashBook.getActualProfit())
//             .entries(entries)
//             .isCurrentMonthYear(isCurrentMonthYear)
//             .build();
// }


    public CashBook updateCashBook(Long id, CashBook updated) {
        CashBook existing = cashBookRepository.findById(id).orElseThrow();
        existing.setSalary(updated.getSalary());
        existing.setElectricity(updated.getElectricity());
        existing.setWater(updated.getWater());
        existing.setRent(updated.getRent());
        existing.setTotalIncome(updated.getTotalIncome());
        existing.setTotalExpense(updated.getTotalExpense());
        existing.setProfitBeforeCost(updated.getProfitBeforeCost());
        existing.setActualProfit(updated.getActualProfit());
        return cashBookRepository.save(existing);
    }

    private boolean isInMonthYear(Date date, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1 == month && cal.get(Calendar.YEAR) == year;
    }

    public void ensureCashBookIntegrity(int month, int year, List<Orders> orders, List<StockImport> imports) {
    List<CashBook> books = cashBookRepository.findByMonthAndYear(month, year);
    Calendar now = Calendar.getInstance();
    boolean isCurrentMonthYear = (now.get(Calendar.MONTH) + 1 == month) && (now.get(Calendar.YEAR) == year);

    if (!books.isEmpty()) {
        CashBook existing = books.get(0);
        System.out.println("Đã tồn tại sổ quỹ cho " + month + "/" + year + " → đồng bộ lại chi tiết.");
        cashBookDetailService.syncCashBookDetailsByMonth(month, year, orders, imports, existing);

        if (isCurrentMonthYear) {
            // Cập nhật lương cho tháng hiện tại
            Integer salaryInt = employeeService.getTotalSalary();
            BigDecimal salary = BigDecimal.valueOf(salaryInt != null ? salaryInt : 0);
            existing.setSalary(salary);

            // Tính lại lợi nhuận
            BigDecimal totalIncome = existing.getTotalIncome() != null ? existing.getTotalIncome() : BigDecimal.ZERO;
            BigDecimal totalExpense = existing.getTotalExpense() != null ? existing.getTotalExpense() : BigDecimal.ZERO;
            BigDecimal rent = existing.getRent() != null ? existing.getRent() : BigDecimal.ZERO;
            BigDecimal electricity = existing.getElectricity() != null ? existing.getElectricity() : BigDecimal.ZERO;
            BigDecimal water = existing.getWater() != null ? existing.getWater() : BigDecimal.ZERO;

            BigDecimal profitBeforeCost = totalIncome.subtract(totalExpense);
            BigDecimal actualProfit = profitBeforeCost.subtract(rent.add(electricity).add(water).add(salary));

            existing.setProfitBeforeCost(profitBeforeCost);
            existing.setActualProfit(actualProfit);

            cashBookRepository.save(existing);
        }
        return;
    }

    // Code hiện có để tạo mới CashBook giữ nguyên
    CashBook cashBook = new CashBook();
    cashBook.setMonth(month);
    cashBook.setYear(year);
    cashBook = cashBookRepository.save(cashBook);

    List<CashBookDetail> detailsToSave = new ArrayList<>();
    for (Orders order : orders) {
        if (isInMonthYear(order.getOrder_date(), month, year)) {
            CashBookDetail detail = new CashBookDetail();
            detail.setCashBook(cashBook);
            detail.setOrder(order);
            detail.setCreatedAt(new Timestamp(order.getOrder_date().getTime()));
            detailsToSave.add(detail);
        }
    }

    for (StockImport stockImport : imports) {
        if (isInMonthYear(stockImport.getImportDate(), month, year)) {
            CashBookDetail detail = new CashBookDetail();
            detail.setCashBook(cashBook);
            detail.setStockImport(stockImport);
            detail.setCreatedAt(new Timestamp(stockImport.getImportDate().getTime()));
            detailsToSave.add(detail);
        }
    }

    detailRepository.saveAll(detailsToSave);

    BigDecimal rent = BigDecimal.valueOf(3_000_000);
    BigDecimal electricity = BigDecimal.valueOf(1_000_000);
    BigDecimal water = BigDecimal.valueOf(800_000);
    Integer salaryInt = employeeService.getTotalSalary();
    BigDecimal salary = BigDecimal.valueOf(salaryInt != null ? salaryInt : 0);

    BigDecimal totalIncome = orders.stream()
            .filter(o -> isInMonthYear(o.getOrder_date(), month, year))
            .map(o -> o.getTotal_amount() != null ? BigDecimal.valueOf(o.getTotal_amount()) : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalExpense = imports.stream()
            .filter(i -> isInMonthYear(i.getImportDate(), month, year))
            .map(i -> BigDecimal.valueOf(i.getTotalCost()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal profitBeforeCost = totalIncome.subtract(totalExpense);
    BigDecimal actualProfit = profitBeforeCost.subtract(rent.add(electricity).add(water).add(salary));

    cashBook.setRent(rent);
    cashBook.setElectricity(electricity);
    cashBook.setWater(water);
    cashBook.setSalary(salary);
    cashBook.setTotalIncome(totalIncome);
    cashBook.setTotalExpense(totalExpense);
    cashBook.setProfitBeforeCost(profitBeforeCost);
    cashBook.setActualProfit(actualProfit);

    cashBookRepository.save(cashBook);
}

}

