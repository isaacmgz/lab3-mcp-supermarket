package com.udea.supermarket.orders;

import com.udea.supermarket.catalog.CatalogService;
import com.udea.supermarket.catalog.Product;
import com.udea.supermarket.catalog.ProductRepository;
import com.udea.supermarket.customers.Customer;
import com.udea.supermarket.customers.CustomerService;
import com.udea.supermarket.shared.BusinessRuleException;
import com.udea.supermarket.shared.NotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orders;
    private final ProductRepository products;
    private final CustomerService customers;
    private final CatalogService catalog;

    public OrderService(OrderRepository orders, ProductRepository products, CustomerService customers, CatalogService catalog) {
        this.orders = orders;
        this.products = products;
        this.customers = customers;
        this.catalog = catalog;
    }

    public OrderView openOrder(String customerPhone) {
        Customer customer = customers.requireByPhone(customerPhone);
        CustomerOrder order = orders.findFirstByCustomerIdAndStatusOrderByCreatedAtDesc(customer.getId(), OrderStatus.DRAFT)
                .orElseGet(() -> orders.save(CustomerOrder.draftFor(customer)));
        return OrderView.from(order);
    }

    public OrderView addItem(String customerPhone, Long orderId, String sku, int quantity) {
        CustomerOrder order = requireOwnedOrder(customerPhone, orderId);
        Product product = catalog.findActiveBySku(sku);
        order.addItem(product, quantity);
        return OrderView.from(orders.saveAndFlush(order));
    }

    public OrderView removeItem(String customerPhone, Long orderId, String sku) {
        CustomerOrder order = requireOwnedOrder(customerPhone, orderId);
        order.removeItem(sku);
        return OrderView.from(orders.saveAndFlush(order));
    }

    @Transactional(readOnly = true)
    public OrderView getOrder(String customerPhone, Long orderId) {
        return OrderView.from(requireOwnedOrder(customerPhone, orderId));
    }

    public OrderView confirm(String customerPhone, Long orderId, PaymentMethod paymentMethod, String deliveryAddress) {
        CustomerOrder order = requireOwnedOrder(customerPhone, orderId);
        order.ensureConfirmable();
        forEachLockedProduct(order, Product::decreaseStock);
        String address = deliveryAddress == null || deliveryAddress.isBlank()
                ? order.getCustomer().deliveryAddress()
                : deliveryAddress.trim();
        order.confirm(paymentMethod, address);
        return OrderView.from(orders.saveAndFlush(order));
    }

    public OrderView cancel(String customerPhone, Long orderId) {
        CustomerOrder order = requireOwnedOrder(customerPhone, orderId);
        boolean wasConfirmed = order.getStatus() == OrderStatus.CONFIRMED;
        order.cancel();
        if (wasConfirmed) {
            forEachLockedProduct(order, Product::increaseStock);
        }
        return OrderView.from(orders.saveAndFlush(order));
    }

    @Transactional(readOnly = true)
    public List<OrderView> listForCustomer(String customerPhone) {
        Customer customer = customers.requireByPhone(customerPhone);
        return orders.findTop10ByCustomerIdOrderByCreatedAtDesc(customer.getId()).stream().map(OrderView::from).toList();
    }

    private void forEachLockedProduct(CustomerOrder order, BiConsumer<Product, Integer> action) {
        order.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getId()))
                .forEach(item -> {
                    Product locked = products.findByIdForUpdate(item.getProduct().getId())
                            .orElseThrow(() -> new NotFoundException("Product " + item.getProduct().getId() + " no longer exists."));
                    action.accept(locked, item.getQuantity());
                });
    }

    private CustomerOrder requireOwnedOrder(String customerPhone, Long orderId) {
        if (orderId == null) {
            throw new BusinessRuleException("An order id is required.");
        }
        Customer customer = customers.requireByPhone(customerPhone);
        return orders.findById(orderId)
                .filter(order -> order.belongsTo(customer))
                .orElseThrow(() -> new NotFoundException("Order #" + orderId + " was not found for customer phone " + customer.getPhone() + "."));
    }
}
