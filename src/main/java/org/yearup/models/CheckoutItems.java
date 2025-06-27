package org.yearup.models;

import java.math.BigDecimal;

public class CheckoutItems
{
    private Product product;
    private int quantity;

    public CheckoutItems() {
    }

    public CheckoutItems(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}