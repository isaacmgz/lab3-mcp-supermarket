package com.udea.supermarket.mcp;

import com.udea.supermarket.customers.CustomerService;
import com.udea.supermarket.customers.CustomerView;
import com.udea.supermarket.customers.NewCustomer;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class CustomerTools {

    private final CustomerService customers;

    public CustomerTools(CustomerService customers) {
        this.customers = customers;
    }

    @Tool(description = "Find a registered customer by mobile phone number. Fails if the phone is not registered.")
    public CustomerView findCustomerByPhone(
            @ToolParam(description = "10-digit Colombian mobile number, e.g. 3001234567") String phone) {
        return customers.findByPhone(phone);
    }

    @Tool(description = "Register a new customer for home delivery. Ask the customer for every required field before calling it.")
    public CustomerView registerCustomer(
            @ToolParam(description = "Customer full name") String fullName,
            @ToolParam(description = "10-digit Colombian mobile number, e.g. 3001234567") String phone,
            @ToolParam(description = "Email address", required = false) String email,
            @ToolParam(description = "Street address for deliveries, e.g. 'Calle 50 # 40-20 Apto 301'") String address,
            @ToolParam(description = "Neighborhood", required = false) String neighborhood,
            @ToolParam(description = "City") String city) {
        return customers.register(new NewCustomer(fullName, phone, email, address, neighborhood, city));
    }
}
