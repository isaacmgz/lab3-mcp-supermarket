package com.udea.supermarket.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class Formats {

    private static final ZoneId STORE_ZONE = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Formats() {
    }

    public static BigDecimal cop(BigDecimal amount) {
        return amount == null ? null : amount.setScale(0, RoundingMode.HALF_UP);
    }

    public static String dateTime(OffsetDateTime value) {
        return value == null ? null : value.atZoneSameInstant(STORE_ZONE).format(DATE_TIME);
    }
}
