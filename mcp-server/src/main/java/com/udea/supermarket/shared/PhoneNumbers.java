package com.udea.supermarket.shared;

import java.util.regex.Pattern;

public final class PhoneNumbers {

    private static final Pattern COLOMBIAN_MOBILE = Pattern.compile("^3\\d{9}$");

    private PhoneNumbers() {
    }

    public static String normalize(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new BusinessRuleException("A phone number is required.");
        }
        String digits = phone.replaceAll("\\D", "");
        if (digits.startsWith("57") && digits.length() == 12) {
            digits = digits.substring(2);
        }
        if (!COLOMBIAN_MOBILE.matcher(digits).matches()) {
            throw new BusinessRuleException("Invalid phone number '" + phone + "'. Use a 10-digit Colombian mobile number starting with 3.");
        }
        return digits;
    }
}
