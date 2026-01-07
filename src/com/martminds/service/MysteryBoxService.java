package com.martminds.service;

import java.util.ArrayList;
import java.util.List;

import com.martminds.exception.InvalidOrderException;
import com.martminds.exception.OutOfStockException;
import com.martminds.model.common.Address;
import com.martminds.model.order.MysteryBoxOrder;
import com.martminds.model.product.MysteryBox;
import com.martminds.model.product.Product;
import com.martminds.util.RandomGenerator;
import com.martminds.util.FileHandler;
import com.martminds.util.Logger;

public class MysteryBoxService {
    private static MysteryBoxService instance;
    private List<MysteryBox> mysteryBoxes;
    private List<MysteryBoxOrder> mysteryBoxOrders;
    private static final String MYSTERYBOX_FILE = "mystery_boxes.csv";
    private static final String MYSTERYBOX_ORDER_FILE = "mystery_box_orders.csv";

    private MysteryBoxService() {
        this.mysteryBoxes = new ArrayList<>();
        this.mysteryBoxOrders = new ArrayList<>();
        loadFromFile();
        if (mysteryBoxes.isEmpty()) {
            createSampleMysteryBoxes();
            saveToFile();
        }
    }

    public static MysteryBoxService getInstance() {
        if (instance == null) {
            instance = new MysteryBoxService();
        }
        return instance;
    }

    private void loadFromFile() {

        List<String> boxLines = FileHandler.readFile(MYSTERYBOX_FILE);
        ProductService productService = ProductService.getInstance();

        for (String line : boxLines) {
            if (line.trim().isEmpty())
                continue;

            try {
                String[] fields = FileHandler.parseCSVLine(line);
                if (fields.length < 7)
                    continue;

                String boxId = fields[0];
                String name = fields[1];
                double price = Double.parseDouble(fields[2]);
                String category = fields[3];
                String description = fields[4];
                int stock = Integer.parseInt(fields[5]);
                String storeId = fields[6];

                MysteryBox box = new MysteryBox(boxId, name, price, category, description, stock, storeId);

                for (int i = 7; i < fields.length; i++) {
                    String productId = fields[i];
                    if (!productId.isEmpty()) {
                        Product product = productService.getProductById(productId);
                        if (product != null) {
                            box.addPossibleProduct(product);
                        }
                    }
                }

                mysteryBoxes.add(box);
            } catch (Exception e) {
                Logger.error("Error parsing mystery box line: " + line + " - " + e.getMessage());
            }
        }

        List<String> orderLines = FileHandler.readFile(MYSTERYBOX_ORDER_FILE);

        for (String line : orderLines) {
            if (line.trim().isEmpty())
                continue;

            try {
                String[] fields = FileHandler.parseCSVLine(line);
                if (fields.length < 11)
                    continue;

                String orderId = fields[0];
                String customerId = fields[1];
                String boxId = fields[2];
                String boxName = fields[3];
                double price = Double.parseDouble(fields[4]);

                String street = fields[5];
                String city = fields[6];
                String postalCode = fields[7];
                String district = fields[8];
                String province = fields[9];
                Address address = new Address(street, city, postalCode, district, province);

                MysteryBoxOrder order = new MysteryBoxOrder(orderId, customerId, boxId, boxName, price, address);

                String contentsStr = fields[10];
                if (!contentsStr.isEmpty()) {
                    String[] productIds = contentsStr.split(";");
                    List<Product> actualContents = new ArrayList<>();
                    for (String pid : productIds) {
                        Product product = productService.getProductById(pid.trim());
                        if (product != null) {
                            actualContents.add(product);
                        }
                    }
                    order.setActualContents(actualContents);
                }

                mysteryBoxOrders.add(order);
            } catch (Exception e) {
                Logger.error("Error parsing mystery box order line: " + line + " - " + e.getMessage());
            }
        }

        Logger.info("Loaded " + mysteryBoxes.size() + " mystery boxes and " +
                mysteryBoxOrders.size() + " orders from file");
    }

    private void saveToFile() {

        List<String> boxLines = new ArrayList<>();

        for (MysteryBox box : mysteryBoxes) {
            List<String> fields = new ArrayList<>();
            fields.add(box.getBoxId());
            fields.add(box.getName());
            fields.add(String.valueOf(box.getPrice()));
            fields.add(box.getCategory());
            fields.add(box.getDescription());
            fields.add(String.valueOf(box.getAvailableStock()));
            fields.add(box.getStoreId());

            for (Product product : box.getPossibleProducts()) {
                fields.add(product.getProductId());
            }

            String line = FileHandler.formatCSVLine(fields.toArray(new String[0]));
            boxLines.add(line);
        }

        FileHandler.writeFile(MYSTERYBOX_FILE, boxLines);

        List<String> orderLines = new ArrayList<>();

        for (MysteryBoxOrder order : mysteryBoxOrders) {
            Address addr = order.getDeliveryAddress();

            StringBuilder contentsStr = new StringBuilder();
            for (Product product : order.getActualContents()) {
                if (contentsStr.length() > 0) {
                    contentsStr.append(";");
                }
                contentsStr.append(product.getProductId());
            }

            String line = FileHandler.formatCSVLine(
                    order.getBoxOrderId(),
                    order.getCustomerId(),
                    order.getBoxId(),
                    order.getBoxName(),
                    String.valueOf(order.getPrice()),
                    addr.getStreet(),
                    addr.getCity(),
                    addr.getPostalCode(),
                    addr.getDistrict(),
                    addr.getProvince(),
                    contentsStr.toString());
            orderLines.add(line);
        }

        FileHandler.writeFile(MYSTERYBOX_ORDER_FILE, orderLines);
    }

    public List<MysteryBox> getAllMysteryBoxes() {
        return new ArrayList<>(mysteryBoxes);
    }

    public MysteryBox getMysteryBoxById(String boxId) {
        return mysteryBoxes.stream()
                .filter(box -> box.getBoxId().equals(boxId))
                .findFirst()
                .orElse(null);
    }

    public boolean addMysteryBox(MysteryBox box) {
        if (box == null || getMysteryBoxById(box.getBoxId()) != null) {
            return false;
        }
        mysteryBoxes.add(box);
        saveToFile();
        return true;
    }

    public MysteryBoxOrder createMysteryBoxOrder(String customerId, String boxId, Address deliveryAddress)
            throws InvalidOrderException, OutOfStockException {
        MysteryBox box = getMysteryBoxById(boxId);

        if (box == null) {
            throw new IllegalArgumentException("Mystery box not found: " + boxId);
        }

        if (!box.isAvailable()) {
            throw new OutOfStockException("Mystery box is out of stock: " + box.getName());
        }

        List<Product> selectedProducts = RandomGenerator.selectRandomItems(
                box.getPossibleProducts(),
                RandomGenerator.randomInt(3, 5));

        String orderId = "MBO-" + RandomGenerator.generateCode(8);
        MysteryBoxOrder order = new MysteryBoxOrder(
                orderId, customerId, boxId, box.getName(), box.getPrice(), deliveryAddress);

        order.setActualContents(selectedProducts);

        box.updateStock(-1);

        mysteryBoxOrders.add(order);
        saveToFile();
        return order;
    }

    public MysteryBoxOrder findMysteryBoxOrderById(String orderId) {
        return mysteryBoxOrders.stream()
                .filter(order -> order.getBoxOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public List<MysteryBoxOrder> getMysteryBoxOrdersByCustomer(String customerId) {
        return mysteryBoxOrders.stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<MysteryBoxOrder> getAllMysteryBoxOrders() {
        return new ArrayList<>(mysteryBoxOrders);
    }

    private void createSampleMysteryBoxes() {
        ProductService productService = ProductService.getInstance();

        MysteryBox snackBox = new MysteryBox(
                "MB001", "Snack Surprise Box", 50000, "Snack",
                "Random selection of premium snacks", 10, "S001");
        snackBox.addPossibleProduct(productService.getProductById("P003"));
        snackBox.addPossibleProduct(productService.getProductById("P005"));
        snackBox.addPossibleProduct(productService.getProductById("P009"));
        mysteryBoxes.add(snackBox);

        MysteryBox beverageBox = new MysteryBox(
                "MB002", "Beverage Bundle Box", 40000, "Beverage",
                "Random selection of refreshing drinks", 15, "S001");
        beverageBox.addPossibleProduct(productService.getProductById("P002"));
        beverageBox.addPossibleProduct(productService.getProductById("P004"));
        beverageBox.addPossibleProduct(productService.getProductById("P006"));
        beverageBox.addPossibleProduct(productService.getProductById("P010"));
        mysteryBoxes.add(beverageBox);
    }
}
