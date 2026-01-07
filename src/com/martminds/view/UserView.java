package com.martminds.view;

import com.martminds.util.Input;
import com.martminds.model.user.User;
import com.martminds.model.common.Address;
import java.util.HashMap;
import java.util.Map;

public class UserView {

    public String[] getLoginCredentials() {
        System.out.println("\nLogin\n");
        String email = Input.promptEmail("Email (valid email format, e.g. user@mail.com): ");
        if ("exit".equals(email))
            return new String[] { "exit", "" };
        String password = Input.promptString("Password: ");
        return new String[] { email, password };
    }

    public int selectUserRole() {
        System.out.println("\nSelect Your Role\n");
        System.out.println("1. Customer - Shop for products and place orders");
        System.out.println("2. Driver - Deliver orders to customers");
        System.out.println();
        return Input.promptInt("Choose role (1-2): ");
    }

    public Map<String, String> getRegistrationData() {
        Map<String, String> data = new HashMap<>();

        System.out.println("\nRegistration (type 'exit' to cancel)\n");
        data.put("name", Input.promptStringWithExit("Full Name (max 50 characters): "));
        if ("exit".equals(data.get("name")))
            return data;

        data.put("email", Input.promptEmail("Email (valid email format, e.g. user@mail.com): "));
        if ("exit".equals(data.get("email")))
            return data;

        data.put("password", Input.promptPassword("Password (min 6 characters): "));
        if ("exit".equals(data.get("password")))
            return data;

        data.put("phone", Input.promptPhone("Phone Number (format: 08xxxxxxxxxx, 10-13 digits): "));
        if ("exit".equals(data.get("phone")))
            return data;

        System.out.println("\nEnter Your Address (mandatory)\n");

        return data;
    }

    public Address getAddressFromInput() {
        while (true) {
            try {
                System.out.println("\nEnter Address (type 'exit' to cancel)\n");
                String street = Input.promptStringWithExit("Street Address (e.g. Jl. Sudirman No. 123): ");
                if ("exit".equals(street))
                    return null;

                String city = Input.promptStringWithExit("City (e.g. Jakarta, Bandung): ");
                if ("exit".equals(city))
                    return null;

                String postalCode = Input.promptString("Postal Code (5 digits, e.g. 12170): ");
                if ("exit".equals(postalCode))
                    return null;
                if (!postalCode.matches("^[0-9]{5}$")) {
                    System.out.println("Invalid postal code. Must be exactly 5 digits.");
                    continue;
                }

                String district = Input.promptStringWithExit("District/Kecamatan (e.g. Kebayoran Baru): ");
                if ("exit".equals(district))
                    return null;

                String province = Input.promptStringWithExit("Province (e.g. DKI Jakarta): ");
                if ("exit".equals(province))
                    return null;

                return new Address(street, city, postalCode, district, province);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + ". Please try again.");
            }
        }
    }

    public void displayUserProfile(User user) {
        System.out.println("\nUser Profile\n");
        System.out.println("ID      : " + user.getUserId());
        System.out.println("Name    : " + user.getName());
        System.out.println("Email   : " + user.getEmail());
        System.out.println("Phone   : " + user.getPhone());
        System.out.println("Balance : " + user.getBalance());
        System.out.println("Role    : " + user.getRole());

        if (user.getAddress() != null) {
            System.out.println("\nRegistered Address:");
            System.out.println(user.getAddress().toString());
        } else {
            System.out.println("\nRegistered Address: Not set");
        }
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
        return Input.promptDoublePositive("Enter amount to top up (min Rp 10,000): ");
    }

    public boolean confirmAddressUpdate(Address currentAddress) {
        if (currentAddress != null) {
            System.out.println("\nUpdate Address\n");
            System.out.println("Current Address:");
            System.out.println(currentAddress.toString());
            System.out.println();
            String confirm = Input.promptString("Do you want to update your address? (yes/no): ");
            return confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y");
        }
        return true;
    }
}
