package com.udea.supermarket.customers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    private String phone;

    private String email;

    private String address;

    private String neighborhood;

    private String city;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Customer() {
    }

    public Customer(String fullName, String phone, String email, String address, String neighborhood, String city) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.neighborhood = neighborhood;
        this.city = city;
    }

    public String deliveryAddress() {
        StringBuilder value = new StringBuilder(address);
        if (neighborhood != null && !neighborhood.isBlank()) {
            value.append(", ").append(neighborhood);
        }
        return value.append(", ").append(city).toString();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }
}
