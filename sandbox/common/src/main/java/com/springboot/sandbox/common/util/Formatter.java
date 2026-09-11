package com.springboot.sandbox.common.util;

public class Formatter {
    public static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return phoneNumber;
        }

        if (phoneNumber.startsWith("0")) {
            return "+62" + phoneNumber.substring(1);
        }

        return phoneNumber;
    }

}
