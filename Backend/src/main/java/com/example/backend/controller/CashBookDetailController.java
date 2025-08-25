package com.example.backend.controller;

import com.example.backend.DTO.CashBookEntryDTO;
import com.example.backend.model.CashBook;
import com.example.backend.repository.CashBookRepository;
import com.example.backend.service.CashBookDetailService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cashbook-details")
@CrossOrigin(origins = "http://localhost:5173")
public class CashBookDetailController {

  @Autowired private CashBookDetailService detailService;

  @Autowired private CashBookRepository cashBookRepository;

  // GET cash book details by ID and month/year
  @GetMapping("/{cashBookId}")
  public ResponseEntity<List<CashBookEntryDTO>> getDetailsByCashBookId(
      @PathVariable Long cashBookId, @RequestParam int month, @RequestParam int year) {

    CashBook book =
        cashBookRepository
            .findById(cashBookId)
            .orElseThrow(() -> new RuntimeException("Sổ quỹ không tồn tại: ID = " + cashBookId));

    List<CashBookEntryDTO> entries = detailService.getDetailsByCashBookInMonth(book, month, year);
    return ResponseEntity.ok(entries);
  }
}
