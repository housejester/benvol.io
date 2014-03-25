package io.benvol.util;

import java.security.SecureRandom;

public class RandomString {

    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(int length, int base) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < length; i++) {
            b.append(CHARS.charAt(RANDOM.nextInt() % base));
        }
        return b.toString();
    }

    public static String generate(int length) {
        return generate(length, CHARS.length());
    }

}
