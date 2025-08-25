package com.example.backend.service;

import com.example.backend.model.Suppliers;
import com.example.backend.repository.SupplierRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {
  @Autowired private SupplierRepository repo;

  public List<Suppliers> getAll() {
    return repo.findAll();
  }

  public Suppliers getById(String id) {
    return repo.findById(id).orElse(null);
  }

  public Suppliers create(Suppliers e) {
    Long maxInd = repo.findMaxInd();
    if (maxInd == null) {
      maxInd = 0L;
    }

    Long newInd = maxInd + 1;
    String supplierId = "NCC" + String.format("%03d", newInd);
    e.setSupplier_id(supplierId);
    e.setInd(newInd);

    return repo.save(e);
  }

  public Suppliers update(String id, Suppliers s) {
    Suppliers existing = repo.findById(id).orElse(null);
    if (existing == null) return null;

    if (s.getName() != null) existing.setName(s.getName());
    if (s.getPhone() != null) existing.setPhone(s.getPhone());
    if (s.getAddress() != null) existing.setAddress(s.getAddress());
    return repo.save(existing);
  }

  public void delete(String id) {
    repo.deleteById(id);
  }

  public boolean existsById(String id) {
    return repo.existsById(id);
  }

  public boolean existByPhone(String phone) {
    return repo.existsByPhone(phone);
  }
}
