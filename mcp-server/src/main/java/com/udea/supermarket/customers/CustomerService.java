package com.udea.supermarket.customers;

import com.udea.supermarket.shared.BusinessRuleException;
import com.udea.supermarket.shared.NotFoundException;
import com.udea.supermarket.shared.PhoneNumbers;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final CustomerRepository customers;

    public CustomerService(CustomerRepository customers) {
        this.customers = customers;
    }

    public CustomerView findByPhone(String phone) {
        return CustomerView.from(requireByPhone(phone));
    }

    public Customer requireByPhone(String phone) {
        String normalized = PhoneNumbers.normalize(phone);
        return customers.findByPhone(normalized)
                .orElseThrow(() -> new NotFoundException("No customer registered with phone " + normalized + ". Register the customer first."));
    }

    @Transactional
    public CustomerView register(NewCustomer request) {
        String phone = PhoneNumbers.normalize(request.phone());
        if (customers.existsByPhone(phone)) {
            throw new BusinessRuleException("A customer with phone " + phone + " is already registered.");
        }
        String fullName = required(request.fullName(), "full name");
        String address = required(request.address(), "address");
        String city = required(request.city(), "city");
        String email = optional(request.email());
        if (email != null && !EMAIL.matcher(email).matches()) {
            throw new BusinessRuleException("Invalid email '" + email + "'.");
        }
        Customer saved = customers.save(new Customer(fullName, phone, email, address, optional(request.neighborhood()), city));
        return CustomerView.from(saved);
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("The customer's " + field + " is required.");
        }
        return value.trim();
    }

    private static String optional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
