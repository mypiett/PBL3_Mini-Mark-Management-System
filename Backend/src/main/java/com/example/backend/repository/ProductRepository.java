package com.example.backend.repository;

import com.example.backend.model.Products;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Products, String> {
  List<Products> findByNameContainingIgnoreCase(String name);

  List<Products> findByCategory_CategoryId(String categoryId);

  List<Products> findBySupplier_SupplierId(String supplierId);

  @Query("SELECT p.productId FROM Products p")
  List<String> findAllProductIds();
}
