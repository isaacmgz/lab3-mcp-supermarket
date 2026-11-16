package com.udea.supermarket.catalog;

import com.udea.supermarket.shared.BusinessRuleException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;

    private String name;

    private String description;

    private String unit;

    private BigDecimal price;

    private int stock;

    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    protected Product() {
    }

    public Product(String sku, String name, String unit, BigDecimal price, int stock, Category category) {
        this.sku = sku;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.active = true;
    }

    public boolean canSupply(int quantity) {
        return active && stock >= quantity;
    }

    public void decreaseStock(int quantity) {
        if (!canSupply(quantity)) {
            throw new BusinessRuleException("Not enough stock for " + name + " (" + sku + "). Requested " + quantity + ", available " + stock + ".");
        }
        stock -= quantity;
    }

    public void increaseStock(int quantity) {
        stock += quantity;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public boolean isActive() {
        return active;
    }

    public Category getCategory() {
        return category;
    }
}
