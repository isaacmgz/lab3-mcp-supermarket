package com.udea.supermarket.orders;

import com.udea.supermarket.catalog.Product;
import com.udea.supermarket.customers.Customer;
import com.udea.supermarket.shared.BusinessRuleException;
import com.udea.supermarket.shared.Formats;
import com.udea.supermarket.shared.NotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "customer_order")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    private BigDecimal subtotal;

    @Column(name = "delivery_fee")
    private BigDecimal deliveryFee;

    private BigDecimal total;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "confirmed_at")
    private OffsetDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    @Version
    private long version;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<OrderItem> items = new ArrayList<>();

    protected CustomerOrder() {
    }

    private CustomerOrder(Customer customer) {
        this.customer = customer;
        this.status = OrderStatus.DRAFT;
        this.createdAt = OffsetDateTime.now();
        recalculate();
    }

    public static CustomerOrder draftFor(Customer customer) {
        return new CustomerOrder(customer);
    }

    public void addItem(Product product, int quantity) {
        requireDraft();
        if (quantity < 1) {
            throw new BusinessRuleException("Quantity must be at least 1.");
        }
        Optional<OrderItem> existing = items.stream().filter(item -> item.isFor(product)).findFirst();
        int newQuantity = existing.map(OrderItem::getQuantity).orElse(0) + quantity;
        if (newQuantity > OrderPolicy.MAX_QUANTITY_PER_PRODUCT) {
            throw new BusinessRuleException("The maximum per product is " + OrderPolicy.MAX_QUANTITY_PER_PRODUCT + " units.");
        }
        if (!product.canSupply(newQuantity)) {
            throw new BusinessRuleException("Not enough stock for " + product.getName() + " (" + product.getSku() + "). Requested " + newQuantity + ", available " + product.getStock() + ".");
        }
        existing.ifPresentOrElse(
                item -> item.changeQuantity(newQuantity),
                () -> items.add(new OrderItem(this, product, newQuantity)));
        recalculate();
    }

    public void removeItem(String sku) {
        requireDraft();
        OrderItem item = items.stream()
                .filter(candidate -> candidate.getProduct().getSku().equalsIgnoreCase(sku))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Order #" + id + " has no product with SKU '" + sku + "'."));
        items.remove(item);
        recalculate();
    }

    public void ensureConfirmable() {
        requireDraft();
        if (items.isEmpty()) {
            throw new BusinessRuleException("Order #" + id + " is empty. Add products before confirming.");
        }
        if (subtotal.compareTo(OrderPolicy.MINIMUM_SUBTOTAL) < 0) {
            throw new BusinessRuleException("The minimum order subtotal is COP " + OrderPolicy.MINIMUM_SUBTOTAL + ". Current subtotal is COP " + Formats.cop(subtotal) + ".");
        }
    }

    public void confirm(PaymentMethod method, String address) {
        ensureConfirmable();
        if (method == null) {
            throw new BusinessRuleException("A payment method is required to confirm the order.");
        }
        this.paymentMethod = method;
        this.deliveryAddress = address;
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = OffsetDateTime.now();
    }

    public void cancel() {
        if (status == OrderStatus.CANCELLED) {
            throw new BusinessRuleException("Order #" + id + " is already cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = OffsetDateTime.now();
    }

    public boolean belongsTo(Customer candidate) {
        return customer.getId().equals(candidate.getId());
    }

    private void requireDraft() {
        if (status != OrderStatus.DRAFT) {
            throw new BusinessRuleException("Order #" + id + " is " + status + " and can no longer be modified.");
        }
    }

    private void recalculate() {
        subtotal = items.stream().map(OrderItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        deliveryFee = OrderPolicy.deliveryFeeFor(subtotal);
        total = subtotal.add(deliveryFee);
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
