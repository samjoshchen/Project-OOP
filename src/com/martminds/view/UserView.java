package com.martminds.view;

import com.martminds.util.Input;
import com.martminds.model.user.User;
import java.util.HashMap;
import java.util.Map;

public class UserView {

    public String[] getLoginCredentials() {
        System.out.println("\nLogin\n");
        String email = Input.promptString("Email: ");
        String password = Input.promptString("Password: ");
        return new String[] { email, password };
    }

    public Map<String, String> getRegistrationData() {
        Map<String, String> data = new HashMap<>();

        System.out.println("\nRegistration\n");
        data.put("name", Input.promptString("Full Name: "));
        data.put("email", Input.promptString("Email: "));
        data.put("password", Input.promptString("Password (min 6 chars): "));
        data.put("phone", Input.promptString("Phone (08xxxxxxxxxx): "));

        return data;
    }

    public void displayUserProfile(User user) {
        System.out.println("\nUser Profile\n");
        System.out.println("ID      : " + user.getUserId());
        System.out.println("Name    : " + user.getName());
        System.out.println("Email   : " + user.getEmail());
        System.out.println("Phone   : " + user.getPhone());
        System.out.println("Balance : " + user.getBalance());
        System.out.println("Role    : " + user.getRole());
        System.out.println();

    }

    public int displayBalanceMenu() {
        System.out.println("\nBalance Management\n");
        System.out.println("1. Add Funds");
        System.out.println("2. Check Balance");
        System.out.println("3. Back");
        return Input.promptInt("Choose option: ");
    }

    public double getTopUpAmount() {
        return Double.parseDouble(Input.promptString("Enter amount to top up: "));
    }
}
