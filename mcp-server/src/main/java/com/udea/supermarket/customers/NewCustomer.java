package com.udea.supermarket.customers;

public record NewCustomer(
        String fullName,
        String phone,
        String email,
        String address,
        String neighborhood,
        String city) {
}
