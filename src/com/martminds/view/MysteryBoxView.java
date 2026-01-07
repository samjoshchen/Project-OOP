package com.martminds.view;

import java.util.List;

import com.martminds.model.order.MysteryBoxOrder;
import com.martminds.model.product.MysteryBox;
import com.martminds.model.product.Product;
import com.martminds.util.Input;

public class MysteryBoxView {

    public void displayMysteryBoxList(List<MysteryBox> boxes) {
        System.out.println("\nMystery Box Collection\n");

        if (boxes == null || boxes.isEmpty()) {
            System.out.println("No mystery boxes available at the moment.\n");
            return;
        }

        System.out.printf("%-12s %-25s %-20s %-12s %-8s %-10s\n",
                "Box ID", "Name", "Category", "Price", "Stock", "Items");
        System.out.println(
                "------------+-------------------------+--------------------+------------+--------+----------");

        for (MysteryBox box : boxes) {
            String name = box.getName().length() > 25 ? box.getName().substring(0, 22) + "..." : box.getName();
            String category = box.getCategory().length() > 20 ? box.getCategory().substring(0, 17) + "..."
                    : box.getCategory();
            System.out.printf("%-12s %-25s %-20s Rp %-10.0f %-8d %-10d\n",
                    box.getBoxId(),
                    name,
                    category,
                    box.getPrice(),
                    box.getAvailableStock(),
                    box.getPossibleProducts().size());
        }
        System.out.println();
    }

    public void displayMysteryBoxDetails(MysteryBox box) {
        System.out.println("\nMystery Box Details\n");
        System.out.printf("Box ID       : %s\n", box.getBoxId());
        System.out.printf("Name         : %s\n", box.getName());
        System.out.printf("Category     : %s\n", box.getCategory());
        System.out.printf("Price        : Rp %.0f\n", box.getPrice());
        System.out.printf("Available    : %d boxes\n", box.getAvailableStock());
        System.out.printf("Description  : %s\n", box.getDescription());
        System.out.println("\nPossible Products in this Mystery Box:");

        for (Product product : box.getPossibleProducts()) {
            System.out.printf("- %s (Rp %.0f)\n", product.getName(), product.getPrice());
        }
        System.out.println();
    }

    public void displayMysteryBoxOrderList(List<MysteryBoxOrder> orders) {
        System.out.println("\nMy Mystery Box Orders\n");

        if (orders == null || orders.isEmpty()) {
            System.out.println("You haven't purchased any mystery boxes yet.\n");
            return;
        }

        System.out.printf("%-14s %-25s %-12s %-12s %-22s\n",
                "Order ID", "Box Name", "Status", "Price", "Contents");
        System.out.println("--------------+-------------------------+------------+------------+----------------------");

        for (MysteryBoxOrder order : orders) {
            String name = order.getBoxName().length() > 25 ? order.getBoxName().substring(0, 22) + "..."
                    : order.getBoxName();
            String contents = order.isContentsRevealed() ? ("Revealed (" + order.getActualContentsCount() + " items)")
                    : "Not revealed";
            System.out.printf("%-14s %-25s %-12s Rp %-10.0f %-22s\n",
                    order.getBoxOrderId(),
                    name,
                    order.getStatus(),
                    order.getPrice(),
                    contents);
        }
        System.out.println();
    }

    public void displayRevealedContents(List<Product> products) {
        System.out.println("\nMystery Box Contents Revealed!\n");
        System.out.println("You received the following items:");

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.printf("%d. %s\n", i + 1, product.getName());
            System.out.printf("   Category: %s | Value: Rp %.0f\n",
                    product.getCategory(), product.getPrice());
        }

        double totalValue = products.stream().mapToDouble(Product::getPrice).sum();
        System.out.printf("\nTotal Value: Rp %.0f\n\n", totalValue);
    }

    public String getMysteryBoxIdInput() {
        return Input.promptMysteryBoxId("\nEnter Mystery Box ID to purchase (e.g. MB001, MB002): ");
    }

    public String getOrderIdToReveal() {
        return Input.promptMysteryBoxOrderId("\nEnter Order ID to reveal contents (e.g. MBO001, MBO002): ");
    }

    public boolean confirmPurchase(MysteryBox box) {
        System.out.println("\nConfirm Purchase\n");
        System.out.printf("Mystery Box  : %s\n", box.getName());
        System.out.printf("Category     : %s\n", box.getCategory());
        System.out.printf("Price        : Rp %.0f\n", box.getPrice());
        System.out.println(
                "\nNote: Mystery box is non refundable and contents will be randomly selected from possible items and can only be revealed after delivery.");
        System.out.println();

        String confirm = Input.promptString("Proceed with purchase? (yes/no, y/n): ");
        return confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y");
    }
}
