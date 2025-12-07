package com.martminds.view;

import com.martminds.util.Input;

public class MenuView {

    public int displayHomeMenu() {
        System.out.println("\nMartMinds\n");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");
        return Input.promptInt("");
    }

    public int displayCustomerMenu() {
        System.out.println("\nCustomer Menu\n");
        System.out.println("1. Browse Products & Shop");
        System.out.println("2. View My Orders");
        System.out.println("3. View Order History");
        System.out.println("4. Manage Balance");
        System.out.println("5. View Profile");
        System.out.println("6. Logout");
        System.out.print("Choose option: ");
        return Input.promptInt("");
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
        System.out.print("Choose option: ");
        return Input.promptInt("");
    }

    public void displayWelcome(String username, String role) {
        System.out.println("Welcome, " + username + " (" + role + ")!");
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
    }
}
