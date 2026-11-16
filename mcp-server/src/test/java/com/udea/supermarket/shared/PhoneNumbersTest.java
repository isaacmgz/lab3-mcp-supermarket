package com.udea.supermarket.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PhoneNumbersTest {

    @ParameterizedTest
    @ValueSource(strings = {"3001234567", "300 123 4567", "300-123-4567", "+57 300 123 4567", "573001234567"})
    void normalizesColombianMobileNumbers(String input) {
        assertThat(PhoneNumbers.normalize(input)).isEqualTo("3001234567");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "12345", "6041234567", "30012345678"})
    void rejectsInvalidNumbers(String input) {
        assertThatThrownBy(() -> PhoneNumbers.normalize(input)).isInstanceOf(BusinessRuleException.class);
    }
}
