package org.ch6techno.store.model;

import java.math.BigDecimal;

public class ItemSummary {
    private String id;
    private String name;
    private BigDecimal price;

    public ItemSummary() {}
    public ItemSummary(String id, String name, BigDecimal price) {
        this.id = id; this.name = name; this.price = price;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
