package com.example.backend.DTO;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashBookMonthSummaryDTO {
  private Long id;
  private int month;
  private int year;

  private BigDecimal totalIncome;
  private BigDecimal totalExpense;
  private BigDecimal salary;
  private BigDecimal electricity;
  private BigDecimal water;
  private BigDecimal rent;
  private BigDecimal profitBeforeCost;
  private BigDecimal actualProfit;
  private boolean isCurrentMonthYear;

  private List<CashBookEntryDTO> entries;
}
