package com.martminds.util;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class RandomGenerator {
    private static final Random random = new Random();

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static String generateNumericId(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static String generateCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

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

    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }
}
