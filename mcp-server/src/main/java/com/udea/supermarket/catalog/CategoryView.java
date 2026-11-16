package com.udea.supermarket.catalog;

public record CategoryView(String name, String description) {

    static CategoryView from(Category category) {
        return new CategoryView(category.getName(), category.getDescription());
    }
}
