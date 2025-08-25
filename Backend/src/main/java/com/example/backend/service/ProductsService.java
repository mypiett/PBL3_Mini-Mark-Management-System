package com.example.backend.service;

import com.example.backend.model.Products;
import com.example.backend.repository.ProductRepository;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductsService {
  @Autowired private ProductRepository productRepository;

  public List<Products> getAllProducts() {
    return productRepository.findAll();
  }

  public Optional<Products> getProductById(String id) {
    return productRepository.findById(id);
  }

  public List<Products> searchByName(String name) {
    return productRepository.findByNameContainingIgnoreCase(name);
  }

  public Products saveProducts(Products product) {
    if (product.getProduct_id() == null || product.getProduct_id().isEmpty()) {
      product.setProduct_id(generateNewProductId());
    }
    try {
      return productRepository.save(product);
    } catch (Exception e) {
      System.err.println("Lỗi khi lưu sản phẩm: " + e.getMessage());
      throw new RuntimeException("Không thể lưu sản phẩm. Chi tiết: " + e.getMessage());
    }
  }

  public String generateNewProductId() {
    List<String> allIds = productRepository.findAllProductIds();

    Set<Integer> numbers = new HashSet<>();
    int max = 0;

    for (String id : allIds) {
      if (id.startsWith("SP")) {
        try {
          int num = Integer.parseInt(id.substring(2));
          numbers.add(num);
          if (num > max) max = num;
        } catch (NumberFormatException e) {
        }
      }
    }

    for (int i = 1; i <= max; i++) {
      if (!numbers.contains(i)) {
        return String.format("SP%02d", i);
      }
    }

    return String.format("SP%02d", max + 1);
  }

  public void deleteProduct(String product_id) {
    Optional<Products> product = productRepository.findById(product_id);
    if (product.isPresent()) {
      productRepository.deleteById(product_id);
    } else {
      throw new NoSuchElementException("Sản phẩm không tồn tại");
    }
  }

  public List<Products> getProductByCategory(String category_id) {
    return productRepository.findByCategory_CategoryId(category_id);
  }

  public List<Products> getProductBySupplier(String supplierId) {
    return productRepository.findBySupplier_SupplierId(supplierId);
  }
}
