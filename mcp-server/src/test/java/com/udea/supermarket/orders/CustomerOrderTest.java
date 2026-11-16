package com.udea.supermarket.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.udea.supermarket.catalog.Product;
import com.udea.supermarket.customers.Customer;
import com.udea.supermarket.shared.BusinessRuleException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class CustomerOrderTest {

    private final Customer customer = withId(new Customer("Ana Pérez", "3001112233", null, "Calle 1 # 2-3", "Centro", "Medellín"), 1L);
    private final Product milk = product(1L, "LAC-001", "4900", 100);
    private final Product coffee = product(2L, "BEB-001", "18900", 5);
    private final Product detergent = product(3L, "ASE-001", "24900", 10);

    @Test
    void addingTheSameProductTwiceMergesTheLine() {
        CustomerOrder order = CustomerOrder.draftFor(customer);

        order.addItem(milk, 2);
        order.addItem(milk, 3);

        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().getFirst().getQuantity()).isEqualTo(5);
        assertThat(order.getSubtotal()).isEqualByComparingTo("24500");
    }

    @Test
    void rejectsQuantitiesAboveAvailableStock() {
        CustomerOrder order = CustomerOrder.draftFor(customer);

        assertThatThrownBy(() -> order.addItem(coffee, 6))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("available 5");
        assertThat(order.getItems()).isEmpty();
    }

    @Test
    void rejectsMoreThanTheMaximumUnitsPerProduct() {
        CustomerOrder order = CustomerOrder.draftFor(customer);
        order.addItem(milk, 50);

        assertThatThrownBy(() -> order.addItem(milk, 1))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("maximum per product is 50");
    }

    @Test
    void rejectsNonPositiveQuantities() {
        CustomerOrder order = CustomerOrder.draftFor(customer);

        assertThatThrownBy(() -> order.addItem(milk, 0)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void chargesDeliveryBelowTheThresholdAndWaivesItFromTheThreshold() {
        CustomerOrder order = CustomerOrder.draftFor(customer);
        order.addItem(detergent, 2);

        assertThat(order.getDeliveryFee()).isEqualByComparingTo("7000");
        assertThat(order.getTotal()).isEqualByComparingTo("56800");

        order.addItem(detergent, 4);

        assertThat(order.getSubtotal()).isEqualByComparingTo("149400");
        assertThat(order.getDeliveryFee()).isEqualByComparingTo("7000");

        order.addItem(milk, 1);

        assertThat(order.getSubtotal()).isEqualByComparingTo("154300");
        assertThat(order.getDeliveryFee()).isZero();
        assertThat(order.getTotal()).isEqualByComparingTo("154300");
    }

    @Test
    void cannotConfirmAnEmptyOrder() {
        CustomerOrder order = CustomerOrder.draftFor(customer);

        assertThatThrownBy(() -> order.confirm(PaymentMethod.CASH_ON_DELIVERY, "Calle 1"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void cannotConfirmBelowTheMinimumSubtotal() {
        CustomerOrder order = CustomerOrder.draftFor(customer);
        order.addItem(milk, 2);

        assertThatThrownBy(() -> order.confirm(PaymentMethod.CASH_ON_DELIVERY, "Calle 1"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("minimum order subtotal");
    }

    @Test
    void aConfirmedOrderCannotBeModified() {
        CustomerOrder order = CustomerOrder.draftFor(customer);
        order.addItem(coffee, 2);
        order.confirm(PaymentMethod.CARD_ON_DELIVERY, "Calle 1");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getConfirmedAt()).isNotNull();
        assertThatThrownBy(() -> order.addItem(milk, 1))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("can no longer be modified");
    }

    @Test
    void anOrderCannotBeCancelledTwice() {
        CustomerOrder order = CustomerOrder.draftFor(customer);
        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThatThrownBy(order::cancel).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void deliveryFeePolicyHandlesEdges() {
        assertThat(OrderPolicy.deliveryFeeFor(BigDecimal.ZERO)).isZero();
        assertThat(OrderPolicy.deliveryFeeFor(new BigDecimal("149999"))).isEqualByComparingTo("7000");
        assertThat(OrderPolicy.deliveryFeeFor(new BigDecimal("150000"))).isZero();
    }

    private static Product product(long id, String sku, String price, int stock) {
        return withId(new Product(sku, sku, "1 unidad", new BigDecimal(price), stock, null), id);
    }

    private static <T> T withId(T entity, long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }
}
