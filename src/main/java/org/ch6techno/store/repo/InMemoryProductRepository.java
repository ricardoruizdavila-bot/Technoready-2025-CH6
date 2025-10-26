package org.ch6techno.store.repo;

import org.ch6techno.store.model.Product;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProductRepository {
    private final Map<String, Product> data = new ConcurrentHashMap<>();

    public InMemoryProductRepository() {
        // Seed de ejemplo
        save(new Product("Basic T-Shirt", new BigDecimal("199.90"), 50));
        save(new Product("Sneakers", new BigDecimal("1299.00"), 15));
    }

    public List<Product> findAll() { return new ArrayList<>(data.values()); }

    public Optional<Product> findById(String id) { return Optional.ofNullable(data.get(id)); }

    public Product save(Product p) {
        if (p.getId() == null || p.getId().isBlank()) {
            p.setId(java.util.UUID.randomUUID().toString());
        }
        data.put(p.getId(), p);
        return p;
    }

    public boolean delete(String id) { return data.remove(id) != null; }
}
