package com.udea.supermarket.orders;

import java.math.BigDecimal;

public final class OrderPolicy {

    public static final int MAX_QUANTITY_PER_PRODUCT = 50;
    public static final BigDecimal MINIMUM_SUBTOTAL = new BigDecimal("20000");
    public static final BigDecimal DELIVERY_FEE = new BigDecimal("7000");
    public static final BigDecimal FREE_DELIVERY_FROM = new BigDecimal("150000");

    private OrderPolicy() {
    }

    public static BigDecimal deliveryFeeFor(BigDecimal subtotal) {
        if (subtotal.signum() == 0 || subtotal.compareTo(FREE_DELIVERY_FROM) >= 0) {
            return BigDecimal.ZERO;
        }
        return DELIVERY_FEE;
    }
}
