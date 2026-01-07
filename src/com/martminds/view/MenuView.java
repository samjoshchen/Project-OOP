package com.martminds.view;

import com.martminds.util.Input;

public class MenuView {

    public int displayHomeMenu() {
        System.out.println("\nMartMinds System");
        System.out.println("Your Smart Grocery Shopping Solution\n");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.println();
        return Input.promptInt("Choose option: ");
    }

    public int displayCustomerMenu() {
        System.out.println("\nCustomer Menu\n");
        System.out.println("1. Browse Products & Shop");
        System.out.println("2. Mystery Box");
        System.out.println("3. View My Orders");
        System.out.println("4. View Order History");
        System.out.println("5. Manage Balance");
        System.out.println("6. View Profile");
        System.out.println("7. Logout");
        System.out.println();
        return Input.promptInt("Choose option: ");
    }

    public int displayAdminMenu() {
        System.out.println("\nAdmin Menu\n");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. Update Product");
        System.out.println("4. Delete Product");
        System.out.println("5. View All Orders");
        System.out.println("6. View All Users");
        System.out.println("7. Logout");
        System.out.println();
        return Input.promptInt("Choose option: ");
    }

    public void displayWelcome(String username, String role) {
        System.out.printf("\nWelcome, %s\n", username);
        System.out.printf("Role: %s\n", role);
    }

    public void displayMessage(String message) {
        System.out.println("\n>> " + message);
    }

    public void displayError(String error) {
        System.err.println("\nERROR: " + error);
    }

    public void displaySuccess(String message) {
        System.out.println("\nSUCCESS: " + message);
    }

    public void pressEnterToContinue() {
        Input.promptString("\nPress Enter to continue...");
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}
