package com.martminds.util;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class RandomGenerator {
    private static final Random random = new Random();

    // UUID generator
    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    // Numeric ID generator with specified length
    public static String generateNumericId(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // Alphanumeric code generator with specified length
    public static String generateCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    // Select random items from a list
    public static <T> List<T> selectRandomItems(List<T> items, int count) {
        if (items == null || items.isEmpty() || count <= 0) {
            return new ArrayList<>();
        }

        List<T> shuffled = new ArrayList<>(items);
        List<T> selected = new ArrayList<>();

        int selectCount = Math.min(count, items.size());
        for (int i = 0; i < selectCount; i++) {
            int index = random.nextInt(shuffled.size());
            selected.add(shuffled.remove(index));
        }

        return selected;
    }

    // Generate random integer within a range
    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    // Generate random double within a range
    public static double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    // Generate random boolean value
    public static boolean randomBoolean() {
        return random.nextBoolean();
    }
}
