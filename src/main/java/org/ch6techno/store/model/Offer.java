package org.ch6techno.store.model;

import java.math.BigDecimal;

public class Offer {
    private String id;
    private String itemId;
    private String bidder;
    private String email;
    private BigDecimal amount;
    private String createdAt;

    public Offer() {}
    public Offer(String id, String itemId, String bidder, String email, BigDecimal amount, String createdAt) {
        this.id = id; this.itemId = itemId; this.bidder = bidder; this.email = email;
        this.amount = amount; this.createdAt = createdAt;
    }
    public String getId() { return id; }
    public String getItemId() { return itemId; }
    public String getBidder() { return bidder; }
    public String getEmail() { return email; }
    public BigDecimal getAmount() { return amount; }
    public String getCreatedAt() { return createdAt; }
    public void setId(String id) { this.id = id; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setBidder(String bidder) { this.bidder = bidder; }
    public void setEmail(String email) { this.email = email; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
