package com.udea.supermarket.catalog;

import com.udea.supermarket.shared.Formats;
import java.math.BigDecimal;

public record ProductView(
        String sku,
        String name,
        String description,
        String unit,
        BigDecimal priceCop,
        int stock,
        String category) {

    static ProductView from(Product product) {
        return new ProductView(
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getUnit(),
                Formats.cop(product.getPrice()),
                product.getStock(),
                product.getCategory().getName());
    }
}
