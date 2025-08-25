package com.example.backend.controller;

import com.example.backend.DTO.ProductRequestDTO;
import com.example.backend.model.Category;
import com.example.backend.model.Products;
import com.example.backend.model.Suppliers;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.SupplierRepository;
import com.example.backend.service.ProductsService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:8083")
public class ProductsController {
  @Autowired private ProductsService productsService;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private SupplierRepository supplierRepository;

  @GetMapping
  public List<Products> getAllProducts() {
    List<Products> products = productsService.getAllProducts();
    System.out.println("Số lượng sản phẩm trả về: " + products.size());
    return products;
  }

  @GetMapping("/{id}")
  public ResponseEntity<Products> getProductById(@PathVariable String id) {
    return productsService
        .getProductById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/search")
  public List<Products> searchByName(@RequestParam String name) {
    return productsService.searchByName(name);
  }

  @GetMapping("/category/{category_id}")
  public List<Products> getByCategory(@PathVariable String category_id) {
    return productsService.getProductByCategory(category_id);
  }

  @GetMapping("/supplier/{supplierId}")
  public List<Products> getBySupplier(@PathVariable String supplierId) {
    return productsService.getProductBySupplier(supplierId);
  }

  @GetMapping("/categories")
  public ResponseEntity<List<Category>> getCategories() {
    List<Category> categories = categoryRepository.findAll();
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/suppliers")
  public ResponseEntity<List<Suppliers>> getSuppliers() {
    List<Suppliers> suppliers = supplierRepository.findAll();
    return ResponseEntity.ok(suppliers);
  }

  @PostMapping
  public ResponseEntity<?> createProduct(@RequestBody ProductRequestDTO dto) {
    try {
      Products product = new Products();
      product.setProduct_id(dto.getProduct_id());
      product.setName(dto.getName());
      product.setQuantity(dto.getQuantity());
      product.setPrice(dto.getPrice());
      if (dto.getImg() == null || dto.getImg().isEmpty()) {
        product.setImg("default_image.png");
      } else {
        product.setImg(dto.getImg());
      }

      if (dto.getSell() == null) {
        product.setSell(0);
      } else {
        product.setSell(dto.getSell());
      }

      Category category =
          categoryRepository
              .findById(dto.getCategory_id())
              .orElseThrow(() -> new RuntimeException("❌ Category not found"));
      Suppliers supplier =
          supplierRepository
              .findById(dto.getSupplier_id())
              .orElseThrow(() -> new RuntimeException("❌ Supplier not found"));

      product.setCategory(category);
      product.setSupplier(supplier);

      product = productsService.saveProducts(product);
      return ResponseEntity.ok(product);
    } catch (Exception ex) {
      System.err.println("Error: " + ex.getMessage());
      ex.printStackTrace();
      return ResponseEntity.status(500).body("Lỗi server: " + ex.getMessage());
    }
  }

  @PostMapping("/importProduct")
  public ResponseEntity<?> importProduct(@RequestBody ProductRequestDTO dto) {
    try {
      Products product = new Products();
      product.setProduct_id(dto.getProduct_id());
      product.setName(dto.getName());
      product.setQuantity(0);
      product.setPrice(dto.getPrice());

      if (dto.getImg() == null || dto.getImg().isEmpty()) {
        product.setImg("default_image.png");
      } else {
        product.setImg(dto.getImg());
      }

      if (dto.getSell() == null) {
        product.setSell(0);
      } else {
        product.setSell(dto.getSell());
      }

      product.setStatus("DRAFT");

      Category category =
          categoryRepository
              .findById(dto.getCategory_id())
              .orElseThrow(() -> new RuntimeException("❌ Category not found"));
      Suppliers supplier =
          supplierRepository
              .findById(dto.getSupplier_id())
              .orElseThrow(() -> new RuntimeException("❌ Supplier not found"));

      product.setCategory(category);
      product.setSupplier(supplier);

      product = productsService.saveProducts(product);
      return ResponseEntity.ok(product);
    } catch (Exception ex) {
      System.err.println("Error: " + ex.getMessage());
      ex.printStackTrace();
      return ResponseEntity.status(500).body("Lỗi server: " + ex.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Products> updateProduct(
      @PathVariable String id, @RequestBody Products updatedProduct) {
    Optional<Products> existingProduct = productsService.getProductById(id);
    if (existingProduct.isPresent()) {
      updatedProduct.setProduct_id(id);
      return ResponseEntity.ok(productsService.saveProducts(updatedProduct));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
    try {
      productsService.deleteProduct(id);
      return ResponseEntity.ok().build();
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}