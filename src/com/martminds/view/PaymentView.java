package com.martminds.view;

import com.martminds.enums.PaymentMethod;
import com.martminds.model.payment.Payment;
import com.martminds.model.user.User;
import com.martminds.util.Input;
import java.util.HashMap;
import java.util.Map;

public class PaymentView {

    public PaymentMethod selectPaymentMethod(User user) {
        System.out.println("\nSelect Payment Method\n");
        System.out.println("1. Cash on Delivery");
        System.out.println("   - Pay with cash when your order arrives");
        System.out.println("   - Requires Citizen ID verification");
        System.out.println();
        System.out.printf("2. MartMinds Balance (Current: Rp %.0f)\n", user.getBalance());
        System.out.println("   - Instant payment from your account balance");
        System.out.println("   - Fast and secure");
        System.out.println();
        System.out.println("3. External E-Wallet (OVO/GoPay/DANA)");
        System.out.println("   - Pay with your external e-wallet");
        System.out.println("   - Does not affect MartMinds Balance");
        System.out.println();
        System.out.println("4. Credit Card");
        System.out.println("   - Pay with your credit/debit card");
        System.out.println("   - Secure payment processing");
        System.out.println();

        int choice = Input.promptInt("Choose payment method (1-4): ");

        switch (choice) {
            case 1:
                return PaymentMethod.CASH;
            case 2:
                return PaymentMethod.EWALLET;
            case 3:
                return PaymentMethod.EWALLET;
            case 4:
                return PaymentMethod.CREDIT_CARD;
            default:
                System.out.println("\nInvalid choice. Defaulting to Cash on Delivery.");
                return PaymentMethod.CASH;
        }
    }

    public Map<String, String> getPaymentDetails(PaymentMethod method, User user) {
        Map<String, String> details = new HashMap<>();

        System.out.println("\nPayment Information\n");

        switch (method) {
            case CASH:
                System.out.println("Cash on Delivery - Verification Required");
                System.out.println("Please provide your Citizen ID (KTP) for verification.");
                System.out.println("Format: 16 digits (e.g. 3201234567890123)");
                System.out.println();

                String citizenId = Input.promptCitizenId("Citizen ID/KTP (16 digits, e.g. 3201234567890123): ");
                if ("exit".equals(citizenId)) {
                    return new HashMap<>();
                }
                details.put("citizenId", citizenId);
                System.out.println("\nVerification details recorded");
                break;

            case EWALLET:
                System.out.println("E-Wallet Payment Selection");
                System.out.println();
                System.out.println("1. MartMinds Balance (Internal)");
                System.out.printf("   Current Balance: Rp %.0f\n", user.getBalance());
                System.out.println("2. OVO");
                System.out.println("3. GoPay");
                System.out.println("4. DANA");
                System.out.println();

                int walletChoice = Input.promptInt("Choose e-wallet (1-4): ");
                String walletProvider;
                String walletId;

                switch (walletChoice) {
                    case 1:
                        walletProvider = "MartMinds Balance";
                        walletId = user.getUserId();

                        System.out.println("\nMartMinds Balance Payment");
                        System.out.printf("Current Balance: Rp %.0f\n", user.getBalance());
                        System.out.println("Your balance will be deducted automatically.");
                        break;

                    case 2:
                        walletProvider = "OVO";
                        System.out.println("\nOVO Payment (External)");
                        walletId = Input.promptPhone("OVO Phone Number (08xxxxxxxxxx): ");
                        if ("exit".equals(walletId)) {
                            return new HashMap<>();
                        }
                        System.out.println("Note: This will NOT affect your MartMinds Balance");
                        break;

                    case 3:
                        walletProvider = "GoPay";
                        System.out.println("\nGoPay Payment (External)");
                        walletId = Input.promptPhone("GoPay Phone Number (08xxxxxxxxxx): ");
                        if ("exit".equals(walletId)) {
                            return new HashMap<>();
                        }
                        System.out.println("Note: This will NOT affect your MartMinds Balance");
                        break;

                    case 4:
                        walletProvider = "DANA";
                        System.out.println("\nDANA Payment (External)");
                        walletId = Input.promptPhone("DANA Phone Number (08xxxxxxxxxx): ");
                        if ("exit".equals(walletId)) {
                            return new HashMap<>();
                        }
                        System.out.println("Note: This will NOT affect your MartMinds Balance");
                        break;

                    default:
                        System.out.println("Invalid choice. Defaulting to MartMinds Balance.");
                        walletProvider = "MartMinds Balance";
                        walletId = user.getUserId();
                }

                details.put("walletProvider", walletProvider);
                details.put("walletId", walletId);
                System.out.println();
                break;

            case CREDIT_CARD:
                System.out.println("Credit Card Payment");
                System.out.println("Please enter your card details:");
                System.out.println();

                String cardNumber = Input.promptCreditCard("Card Number (16 digits, e.g. 4532123456789012): ");
                if ("exit".equals(cardNumber)) {
                    return new HashMap<>();
                }

                String cardHolder = Input.promptStringWithExit("Card Holder Name (as on card): ");
                if ("exit".equals(cardHolder)) {
                    return new HashMap<>();
                }

                String expiry = Input.promptExpiryDate("Expiry Date (MM/YY, e.g. 12/25): ");
                if ("exit".equals(expiry)) {
                    return new HashMap<>();
                }

                String cvv = Input.promptCVV("CVV (3 digits on back of card, e.g. 123): ");
                if ("exit".equals(cvv)) {
                    return new HashMap<>();
                }

                details.put("cardNumber", cardNumber);
                details.put("cardHolder", cardHolder);
                details.put("expiry", expiry);
                details.put("cvv", cvv);
                System.out.println("\nCard details recorded securely");
                break;
        }

        return details;
    }

    public double getCashAmount(double totalAmount) {
        System.out.println("\nCash Payment Amount\n");
        System.out.printf("Total Amount Due: Rp %.0f\n", totalAmount);
        System.out.println();

        double receivedAmount = Input.promptDoublePositive(
                String.format("Cash Received (min Rp %.0f): Rp ", totalAmount));

        while (receivedAmount < totalAmount) {
            System.out.printf("\nInsufficient amount. Need at least Rp %.0f\n", totalAmount);
            receivedAmount = Input.promptDoublePositive(
                    String.format("Cash Received (min Rp %.0f): Rp ", totalAmount));
        }

        double change = receivedAmount - totalAmount;
        if (change > 0) {
            System.out.printf("\nChange to return: Rp %.0f\n", change);
        }

        return receivedAmount;
    }

    public void displayPaymentSuccess(Payment payment) {
        System.out.println("\nPayment Successful\n");
        System.out.printf("Payment ID    : %s\n", payment.getPaymentId());
        System.out.printf("Amount        : Rp %.0f\n", payment.getAmount());
        System.out.printf("Method        : %s\n", payment.getMethod());
        System.out.printf("Status        : %s\n", payment.getStatus());
        System.out.printf("Date          : %s\n", payment.getCreatedAt());
        System.out.println();
        System.out.println("Your order will be processed shortly.\n");
    }

    public void displayPaymentFailed(String reason) {
        System.out.println("\nPayment Failed\n");
        System.out.println("Reason: " + reason);
        System.out.println();
        System.out.println("Please try again or choose a different payment method.\n");
    }

    public void displayInsufficientBalance(double required, double current) {
        System.out.println("\nInsufficient Balance\n");
        System.out.printf("Required Amount : Rp %.0f\n", required);
        System.out.printf("Current Balance : Rp %.0f\n", current);
        System.out.printf("Shortfall       : Rp %.0f\n", (required - current));
        System.out.println();
        System.out.println("Please top up your balance or use a different payment method.\n");
    }

    public boolean confirmPayment(double amount, PaymentMethod method) {
        System.out.println("\nConfirm Payment\n");
        System.out.printf("Total Amount  : Rp %.0f\n", amount);
        System.out.printf("Payment Method: %s\n", method);
        System.out.println();

        String confirm = Input.promptString("Proceed with payment? (yes/no): ");
        return confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y");
    }
}
