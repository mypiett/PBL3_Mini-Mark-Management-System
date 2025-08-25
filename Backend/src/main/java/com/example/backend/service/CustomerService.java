package com.example.backend.service;

import com.example.backend.model.Customers;
import com.example.backend.repository.CustomerRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
  @Autowired private CustomerRepository repo;

  public List<Customers> getAll() {
    return repo.findAll();
  }

  public Customers getById(String id) {
    return repo.findById(id).orElse(null);
  }

  private int getLevel(String membership) {
    if (membership == null) return 1;

    return switch (membership) {
      case "Kim cương" -> 5;
      case "Vàng" -> 4;
      case "Bạc" -> 3;
      case "Đồng" -> 2;
      case "Chưa có hạng" -> 1;
      default -> 0;
    };
  }

  public String determineMembershipType(Long points, String currentMembership) {
    if (points == null) return currentMembership != null ? currentMembership : "Chưa có hạng";

    String newMembership;
    if (points > 5000) newMembership = "Kim cương";
    else if (points > 2000) newMembership = "Vàng";
    else if (points > 700) newMembership = "Bạc";
    else if (points > 100) newMembership = "Đồng";
    else newMembership = "Chưa có hạng";

    if (currentMembership == null) return newMembership;
    int currentLevel = getLevel(currentMembership);
    int newLevel = getLevel(newMembership);

    return (newLevel > currentLevel) ? newMembership : currentMembership;
  }

  public Customers create(Customers c) {
    Long maxInd = repo.findMaxInd();
    if (maxInd == null) {
      maxInd = 0L;
    }
    Long newInd = maxInd + 1;
    String customerId = "KH" + String.format("%03d", newInd);

    c.setCustomer_id(customerId);
    c.setInd(newInd);
    c.setMembership_type(determineMembershipType(c.getPoints(), null));
    System.out.println("Generated customer_id: " + customerId);
    System.out.println("Generated ind: " + newInd);

    return repo.save(c);
  }

  public Customers update(String id, Customers c) {
    Customers existing = repo.findById(id).orElse(null);
    if (existing == null) return null;

    if (c.getName() != null) existing.setName(c.getName());
    if (c.getPhone() != null) existing.setPhone(c.getPhone());
    if (c.getDate_of_birth() != null) existing.setDate_of_birth(c.getDate_of_birth());
    if (c.getCustomer_id() != null) existing.setCustomer_id(c.getCustomer_id());

    return repo.save(existing);
  }

  public Customers updatePoints(String id, Long points) {
    Customers existing = repo.findById(id).orElse(null);
    if (existing == null) return null;
    if (points != null) {
      existing.setPoints(points);
      String updatedMembership = determineMembershipType(points, existing.getMembership_type());
      existing.setMembership_type(updatedMembership);
    }

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
