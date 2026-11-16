package com.udea.supermarket.orders;

import static com.udea.supermarket.shared.Formats.cop;
import static com.udea.supermarket.shared.Formats.dateTime;

import java.math.BigDecimal;
import java.util.List;

public record OrderView(
        Long orderId,
        OrderStatus status,
        String customerName,
        String customerPhone,
        String deliveryAddress,
        PaymentMethod paymentMethod,
        List<Line> items,
        BigDecimal subtotalCop,
        BigDecimal deliveryFeeCop,
        BigDecimal totalCop,
        String createdAt,
        String confirmedAt,
        String cancelledAt,
        String notes) {

    public record Line(String sku, String name, String unit, int quantity, BigDecimal unitPriceCop, BigDecimal lineTotalCop) {
    }

    static OrderView from(CustomerOrder order) {
        List<Line> lines = order.getItems().stream()
                .map(item -> new Line(
                        item.getProduct().getSku(),
                        item.getProduct().getName(),
                        item.getProduct().getUnit(),
                        item.getQuantity(),
                        cop(item.getUnitPrice()),
                        cop(item.getLineTotal())))
                .toList();
        String address = order.getDeliveryAddress() != null
                ? order.getDeliveryAddress()
                : order.getCustomer().deliveryAddress();
        return new OrderView(
                order.getId(),
                order.getStatus(),
                order.getCustomer().getFullName(),
                order.getCustomer().getPhone(),
                address,
                order.getPaymentMethod(),
                lines,
                cop(order.getSubtotal()),
                cop(order.getDeliveryFee()),
                cop(order.getTotal()),
                dateTime(order.getCreatedAt()),
                dateTime(order.getConfirmedAt()),
                dateTime(order.getCancelledAt()),
                notesFor(order));
    }

    private static String notesFor(CustomerOrder order) {
        if (order.getStatus() != OrderStatus.DRAFT) {
            return null;
        }
        BigDecimal subtotal = order.getSubtotal();
        if (subtotal.compareTo(OrderPolicy.MINIMUM_SUBTOTAL) < 0) {
            return "Add COP " + cop(OrderPolicy.MINIMUM_SUBTOTAL.subtract(subtotal)) + " more to reach the minimum order of COP " + OrderPolicy.MINIMUM_SUBTOTAL + ".";
        }
        if (subtotal.compareTo(OrderPolicy.FREE_DELIVERY_FROM) < 0) {
            return "Ready to confirm. Add COP " + cop(OrderPolicy.FREE_DELIVERY_FROM.subtract(subtotal)) + " more to get free delivery.";
        }
        return "Ready to confirm with free delivery.";
    }
}
