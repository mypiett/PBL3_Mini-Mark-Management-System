package com.example.backend.service;

import com.example.backend.DTO.CashBookEntryDTO;
import com.example.backend.model.CashBook;
import com.example.backend.model.CashBookDetail;
import com.example.backend.model.Orders;
import com.example.backend.model.StockImport;
import com.example.backend.repository.CashBookDetailRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CashBookDetailService {

  @Autowired private CashBookDetailRepository detailRepository;

  public List<CashBookEntryDTO> getDetailsByCashBookInMonth(
      CashBook cashBook, int month, int year) {
    List<CashBookDetail> details = detailRepository.findByCashBookId(cashBook.getId());
    List<CashBookEntryDTO> dtos = new ArrayList<>();

    for (CashBookDetail detail : details) {
      if (detail.getOrder() != null) {
        Orders order = detail.getOrder();
        String sourceId = order.getOrder_id() != null ? order.getOrder_id() : "1";

        dtos.add(
            CashBookEntryDTO.builder()
                .entryId(detail.getId())
                .sourceId(sourceId)
                .type("ORDER")
                .direction("IN")
                .build());
      }

      if (detail.getStockImport() != null) {
        StockImport stock = detail.getStockImport();
        String sourceId = stock.getImportId() != null ? stock.getImportId() : "1";

        dtos.add(
            CashBookEntryDTO.builder()
                .entryId(detail.getId())
                .sourceId(sourceId)
                .type("IMPORT")
                .direction("OUT")
                .build());
      }
    }
    return dtos;
  }

  public void syncCashBookDetailsByMonth(
      int month,
      int year,
      List<Orders> ordersInMonth,
      List<StockImport> importsInMonth,
      CashBook cashBook) {

    List<CashBookDetail> toSave = new ArrayList<>();

    for (Orders order : ordersInMonth) {
      boolean exists = detailRepository.existsByCashBookIdAndOrder(cashBook.getId(), order);
      if (!exists) {
        CashBookDetail detail = new CashBookDetail();
        detail.setCashBook(cashBook);
        detail.setOrder(order);
        detail.setCreatedAt(order.getOrder_date());
        toSave.add(detail);
      }
    }

    for (StockImport stock : importsInMonth) {
      boolean exists = detailRepository.existsByCashBookIdAndStockImport(cashBook.getId(), stock);
      if (!exists) {
        CashBookDetail detail = new CashBookDetail();
        detail.setCashBook(cashBook);
        detail.setStockImport(stock);
        detail.setCreatedAt(stock.getImportDate());
        toSave.add(detail);
      }
    }

    for (CashBookDetail detail : toSave) {
      if (detail.getOrder() == null && detail.getStockImport() == null) {
        throw new IllegalArgumentException(
            "CashBookDetail must have at least an order or an import.");
      }
    }

    detailRepository.saveAll(toSave);
  }
}
