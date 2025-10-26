package org.ch6techno.store.model;

import java.math.BigDecimal;

public class Item {
    private String id;
    private String name;
    private BigDecimal price;
    private String description;

    public Item() {}
    public Item(String id, String name, BigDecimal price, String description) {
        this.id = id; this.name = name; this.price = price; this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getDescription() { return description; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
}
