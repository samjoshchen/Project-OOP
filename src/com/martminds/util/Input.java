package com.martminds.util;

import java.util.Scanner;
import java.util.regex.Pattern;
import com.martminds.enums.Category;

public class Input {
    public static final Scanner sc = new Scanner(System.in);
    private static final Pattern PRODUCT_ID_PATTERN = Pattern.compile("^[Pp]\\d{3}$");
    private static final Pattern MYSTERY_BOX_ID_PATTERN = Pattern.compile("^[Mm][Bb]\\d{3}$");
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("^[Oo][Rr][Dd]\\d{3}$");
    private static final Pattern MYSTERY_BOX_ORDER_ID_PATTERN = Pattern.compile("^[Mm][Bb][Oo]\\d{3}$");

    public static String promptString(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    public static String promptStringWithExit(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be empty. Type 'exit' to cancel.");
        }
    }

    public static int promptInt(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                return -1;
            }

            try {
                int value = Integer.parseInt(input);
                if (value >= 0)
                    return value;
                System.out.println("Please enter a number 0 or greater. Type 'exit' to cancel.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again. Type 'exit' to cancel.");
            }
        }
    }

    public static int promptIntWithExit(String message) {
        return promptInt(message);
    }

    public static int promptIntPositive(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                return -1;
            }

            try {
                int value = Integer.parseInt(input);
                if (value > 0)
                    return value;
                System.out.println("Please enter a number greater than 0. Type 'exit' to cancel.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again. Type 'exit' to cancel.");
            }
        }
    }

    public static double promptDouble(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                return -1;
            }

            try {
                double value = Double.parseDouble(input);
                if (value >= 0)
                    return value;
                System.out.println("Please enter a number 0 or greater. Type 'exit' to cancel.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again. Type 'exit' to cancel.");
            }
        }
    }

    public static double promptDoublePositive(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                return -1;
            }

            try {
                double value = Double.parseDouble(input);
                if (value > 0)
                    return value;
                System.out.println("Please enter a number greater than 0. Type 'exit' to cancel.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again. Type 'exit' to cancel.");
            }
        }
    }

    public static double promptPrice(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                return -1;
            }

            try {
                double value = Double.parseDouble(input);
                if (value >= 1000)
                    return value;
                System.out.println("Price must be at least Rp 1,000. Type 'exit' to cancel.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again. Type 'exit' to cancel.");
            }
        }
    }

    public static String promptProductId(String message) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            if (PRODUCT_ID_PATTERN.matcher(input).matches()) {
                return input;
            }
            System.out.println("Invalid format. Product ID must be P### (e.g. P001). Type 'exit' to cancel.");
        }
    }

    public static String promptMysteryBoxId(String message) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            if (MYSTERY_BOX_ID_PATTERN.matcher(input).matches()) {
                return input;
            }
            System.out.println("Invalid format. Mystery Box ID must be MB### (e.g. MB001). Type 'exit' to cancel.");
        }
    }

    public static String promptOrderId(String message) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("cancel")) {
                return "cancel";
            }
            if (ORDER_ID_PATTERN.matcher(input).matches()) {
                return input;
            }
            System.out.println(
                    "Invalid format. Order ID must be ORD### (e.g. ORD001). Type 'exit' or 'cancel' to go back.");
        }
    }

    public static String promptMysteryBoxOrderId(String message) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("cancel")) {
                return "cancel";
            }
            if (MYSTERY_BOX_ORDER_ID_PATTERN.matcher(input).matches()) {
                return input;
            }
            System.out.println(
                    "Invalid format. Mystery Box Order ID must be MBO### (e.g. MBO001). Type 'exit' or 'cancel' to go back.");
        }
    }

    public static String promptStringMaxLength(String message, int maxLength) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            if (input.length() <= maxLength) {
                return input;
            }
            System.out.println("Input exceeds maximum length of " + maxLength + " characters. Type 'exit' to cancel.");
        }
    }

    public static String promptValidCategory(String message) {
        Category[] categories = Category.values();

        System.out.println("\n" + message);
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i].getDisplayName());
        }
        System.out.println();

        while (true) {
            String input = promptString("Enter category number (1-" + categories.length + ") or name: ");
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }

            try {
                int choice = Integer.parseInt(input);
                Category category = Category.fromNumber(choice);
                if (category != null) {
                    return category.getDisplayName();
                }
                System.out.println("Invalid number. Please enter a number between 1 and " + categories.length
                        + ". Type 'exit' to cancel.");
            } catch (NumberFormatException e) {

                Category category = Category.fromDisplayName(input);
                if (category != null) {
                    return category.getDisplayName();
                }
                System.out.println("Invalid category. Please enter a number (1-" + categories.length
                        + ") or valid category name. Type 'exit' to cancel.");
            }
        }
    }

    public static String promptEmail(String message) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            if (ValidationUtil.isValidEmail(input)) {
                return input;
            }
            System.out.println("Invalid email format. Type 'exit' to cancel.");
        }
    }

    public static String promptPassword(String message) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            if (input.length() >= 6) {
                return input;
            }
            System.out.println("Password must be at least 6 characters. Type 'exit' to cancel.");
        }
    }

    public static String promptPhone(String message) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            if (ValidationUtil.isValidPhone(input)) {
                return input;
            }
            System.out.println("Invalid phone format. Must be 10-13 digits starting with 08. Type 'exit' to cancel.");
        }
    }

    public static String promptCreditCard(String message) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            if (input.matches("^\\d{16}$")) {
                return input;
            }
            System.out.println("Invalid card format. Card number must be exactly 16 digits. Type 'exit' to cancel.");
        }
    }

    public static String promptCVV(String message) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            if (input.matches("^\\d{3}$")) {
                return input;
            }
            System.out.println("Invalid CVV. Must be exactly 3 digits. Type 'exit' to cancel.");
        }
    }

    public static String promptExpiryDate(String message) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            if (input.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
                return input;
            }
            System.out.println("Invalid expiry date format. Must be MM/YY. Type 'exit' to cancel.");
        }
    }

    public static String promptCitizenId(String message) {
        while (true) {
            String input = promptString(message);
            if (input.equalsIgnoreCase("exit")) {
                return "exit";
            }
            if (input.matches("^\\d{16}$")) {
                return input;
            }
            System.out.println("Invalid Citizen ID. Must be exactly 16 digits. Type 'exit' to cancel.");
        }
    }
}
