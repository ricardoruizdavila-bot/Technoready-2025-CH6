package org.ch6techno.store.model;

import java.math.BigDecimal;

public class OfferIn {
    private String itemId;
    private String bidder;
    private String email;
    private BigDecimal amount;

    public String getItemId() { return itemId; }
    public String getBidder() { return bidder; }
    public String getEmail() { return email; }
    public BigDecimal getAmount() { return amount; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setBidder(String bidder) { this.bidder = bidder; }
    public void setEmail(String email) { this.email = email; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
