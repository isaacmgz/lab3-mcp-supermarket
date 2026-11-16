package com.udea.supermarket.customers;

public record CustomerView(
        String fullName,
        String phone,
        String email,
        String address,
        String neighborhood,
        String city) {

    static CustomerView from(Customer customer) {
        return new CustomerView(
                customer.getFullName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getNeighborhood(),
                customer.getCity());
    }
}
