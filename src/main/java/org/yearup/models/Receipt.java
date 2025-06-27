package org.yearup.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Receipt {
    private int receiptId;
    private int userId;
    private LocalDateTime date;
    private BigDecimal total;
    private List<CheckoutItems> items;

    public Receipt(int receiptId, int userId, BigDecimal total, LocalDateTime date, List<CheckoutItems> items)
    {
        this.receiptId = receiptId;
        this.userId = userId;
        this.total = total;
        this.date = date;
        this.items = items;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<CheckoutItems> getItems() {
        return items;
    }

    public void setItems(List<CheckoutItems> items) {
        this.items = items;
    }
}