package com.udea.supermarket.mcp;

import com.udea.supermarket.orders.OrderService;
import com.udea.supermarket.orders.OrderView;
import com.udea.supermarket.orders.PaymentMethod;
import java.util.List;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class OrderTools {

    private static final String PHONE = "Mobile phone of the customer who owns the order";

    private final OrderService orders;

    public OrderTools(OrderService orders) {
        this.orders = orders;
    }

    @Tool(description = "Open a draft delivery order (shopping cart) for a registered customer. Reuses the customer's current draft if one exists.")
    public OrderView createOrder(@ToolParam(description = PHONE) String customerPhone) {
        return orders.openOrder(customerPhone);
    }

    @Tool(description = "Add a product to a draft order by SKU. If the product is already in the order, the quantity is added to the existing line. Validates stock.")
    public OrderView addItemToOrder(
            @ToolParam(description = PHONE) String customerPhone,
            @ToolParam(description = "Order id") Long orderId,
            @ToolParam(description = "Product SKU obtained from searchProducts") String sku,
            @ToolParam(description = "Units to add, between 1 and 50") int quantity) {
        return orders.addItem(customerPhone, orderId, sku, quantity);
    }

    @Tool(description = "Remove a product line from a draft order.")
    public OrderView removeItemFromOrder(
            @ToolParam(description = PHONE) String customerPhone,
            @ToolParam(description = "Order id") Long orderId,
            @ToolParam(description = "SKU of the product to remove") String sku) {
        return orders.removeItem(customerPhone, orderId, sku);
    }

    @Tool(description = "Get an order with its items, subtotal, delivery fee, total in COP, status and notes.")
    public OrderView getOrder(
            @ToolParam(description = PHONE) String customerPhone,
            @ToolParam(description = "Order id") Long orderId) {
        return orders.getOrder(customerPhone, orderId);
    }

    @Tool(description = """
            Confirm a draft order for home delivery and reserve its stock. Only call it after showing the customer \
            the full summary and receiving explicit confirmation. Minimum subtotal is COP 20000; delivery costs COP 7000 \
            and is free from COP 150000.""")
    public OrderView confirmOrder(
            @ToolParam(description = PHONE) String customerPhone,
            @ToolParam(description = "Order id") Long orderId,
            @ToolParam(description = "Payment method on delivery") PaymentMethod paymentMethod,
            @ToolParam(description = "Delivery address if different from the registered one", required = false) String deliveryAddress) {
        return orders.confirm(customerPhone, orderId, paymentMethod, deliveryAddress);
    }

    @Tool(description = "Cancel a draft or confirmed order. Stock of confirmed orders is returned. Ask the customer to confirm before calling it.")
    public OrderView cancelOrder(
            @ToolParam(description = PHONE) String customerPhone,
            @ToolParam(description = "Order id") Long orderId) {
        return orders.cancel(customerPhone, orderId);
    }

    @Tool(description = "List the 10 most recent orders of a customer, newest first.")
    public List<OrderView> listCustomerOrders(@ToolParam(description = PHONE) String customerPhone) {
        return orders.listForCustomer(customerPhone);
    }
}
