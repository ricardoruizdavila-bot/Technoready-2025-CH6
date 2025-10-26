package org.ch6techno.store.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Product {
    private String id;
    private String name;
    private BigDecimal price;
    private int stock;

    public Product() {}

    public Product(String name, BigDecimal price, int stock) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
