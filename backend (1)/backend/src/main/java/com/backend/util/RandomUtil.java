package com.backend.util;

import java.security.SecureRandom;

public class RandomUtil {
    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp(int digits) {
        int max = (int) Math.pow(10, digits) - 1;
        int min = (int) Math.pow(10, digits - 1);
        int num = random.nextInt(max - min + 1) + min;
        return String.valueOf(num);
    }
}
