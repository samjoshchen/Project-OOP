package com.martminds;

import com.martminds.controller.*;
import com.martminds.view.*;
import com.martminds.model.user.User;
import com.martminds.model.product.Product;
import com.martminds.model.order.Order;
import com.martminds.model.order.OrderItem;
import com.martminds.model.common.Address;
import com.martminds.model.payment.Payment;
import com.martminds.enums.UserRole;
import com.martminds.enums.PaymentMethod;
import com.martminds.service.*;
import com.martminds.util.Session;
import com.martminds.util.Input;
import com.martminds.util.RandomGenerator;

import java.util.List;
import java.util.Map;

public class Main {
	private static AuthController authController;
	private static ProductController productController;
	private static OrderController orderController;
	private static PaymentController paymentController;
	private static DriverController driverController;

	private static MenuView menuView;
	private static UserView userView;
	private static ProductView productView;
	private static OrderView orderView;
	private static PaymentView paymentView;
	private static AdminView adminView;
	private static DriverView driverView;

	public static void main(String[] args) {
		System.out.println("Welcome to MartMinds!");

		com.martminds.util.FileHandler.ensureDataDirectoryExists();

		initialize();
		run();

		System.out.println("\nThank you for using MartMinds!");
	}

	private static void initialize() {
		authController = new AuthController();
		productController = new ProductController();
		orderController = new OrderController(OrderService.getInstance());
		paymentController = new PaymentController();
		driverController = new DriverController();

		menuView = new MenuView();
		userView = new UserView();
		productView = new ProductView();
		orderView = new OrderView();
		paymentView = new PaymentView();
		adminView = new AdminView();
		driverView = new DriverView();
	}

	private static void run() {
		boolean running = true;

		while (running) {
			try {
				if (Session.getInstance().isLoggedIn()) {
					User currentUser = Session.getInstance().getCurrentUser();
					menuView.displayWelcome(currentUser.getName(), currentUser.getRole().toString());

					if (Session.getInstance().isCustomer()) {
						handleCustomerMenu();
					} else if (Session.getInstance().isDriver()) {
						handleDriverMenu();
					} else if (Session.getInstance().isAdmin()) {
						handleAdminMenu();
					} else {
						menuView.displayError("Unknown user role");
						authController.logout();
					}
				} else {
					int choice = menuView.displayHomeMenu();
					switch (choice) {
						case 1:
							handleLogin();
							break;
						case 2:
							handleRegister();
							break;
						case 3:
							running = false;
							break;
						default:
							menuView.displayError("Invalid option. Please try again.");
					}
				}
			} catch (Exception e) {
				menuView.displayError(e.getMessage());
				menuView.pressEnterToContinue();
			}
		}
	}

	private static void handleLogin() {
		try {
			String[] credentials = userView.getLoginCredentials();
			User user = authController.login(credentials[0], credentials[1]);
			menuView.displaySuccess("Login successful! Welcome " + user.getName());
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Login failed: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleRegister() {
		try {

			int roleChoice = userView.selectUserRole();
			UserRole role;
			if (roleChoice == 1) {
				role = UserRole.CUSTOMER;
			} else if (roleChoice == 2) {
				role = UserRole.DRIVER;
			} else {
				menuView.displayError("Invalid role selection.");
				menuView.pressEnterToContinue();
				return;
			}

			Map<String, String> data = userView.getRegistrationData();

			if ("exit".equals(data.get("name")) || "exit".equals(data.get("email")) ||
					"exit".equals(data.get("password")) || "exit".equals(data.get("phone"))) {
				menuView.displayMessage("Registration cancelled.");
				menuView.pressEnterToContinue();
				return;
			}

			com.martminds.model.common.Address address = null;
			if (role == UserRole.CUSTOMER) {
				while (address == null) {
					address = userView.getAddressFromInput();
					if (address == null) {
						menuView.displayMessage("Address is mandatory for customers. Try again?");
						int retry = Input.promptInt("1=Yes, 0=Cancel: ");
						if (retry != 1) {
							menuView.displayMessage("Registration cancelled.");
							menuView.pressEnterToContinue();
							return;
						}
					}
				}
			} else if (role == UserRole.DRIVER) {

				int addAddressChoice = Input.promptInt("\nAdd delivery address? (1=Yes, 0=Skip): ");
				if (addAddressChoice == 1) {
					address = userView.getAddressFromInput();
				}
			}

			String userIdPrefix = (role == UserRole.CUSTOMER) ? "C" : "D";
			String userId = userIdPrefix + RandomGenerator.generateId().substring(0, 3);

			double initialBalance = 0.0;

			User newUser = authController.register(
					userId,
					data.get("name"),
					data.get("email"),
					data.get("password"),
					data.get("phone"),
					role,
					initialBalance);

			if (address != null) {
				newUser.setAddress(address);
			}

			menuView.displaySuccess("Registration successful! You are registered as a " + role.toString().toLowerCase()
					+ ". You can now login.");
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Registration failed: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleCustomerMenu() {
		int choice = menuView.displayCustomerMenu();

		switch (choice) {
			case 1:
				handleBrowseAndShop();
				break;
			case 2:
				handleMysteryBox();
				break;
			case 3:
				handleViewMyOrders();
				break;
			case 4:
				handleViewOrderHistory();
				break;
			case 5:
				handleManageBalance();
				break;
			case 6:
				handleViewProfile();
				break;
			case 7:
				return;
			default:
				menuView.displayError("Invalid option");
				menuView.pressEnterToContinue();
		}
	}

	private static void handleBrowseAndShop() {
		try {
			List<Product> products = productController.getAllProducts();
			productView.displayProductList(products);

			List<OrderItem> items = orderView.collectCartItems();
			if (items.isEmpty()) {
				menuView.displayMessage("No items added. Returning to menu.");
				menuView.pressEnterToContinue();
				return;
			}

			User currentUser = Session.getInstance().getCurrentUser();
			Address address = orderView.selectDeliveryAddress(currentUser);
			if (address == null) {
				menuView.displayMessage("Address selection cancelled.");
				menuView.pressEnterToContinue();
				return;
			}

			Order order = orderController.createOrder("S001", items, address);
			menuView.displaySuccess("Order created! ID: " + order.getOrderId());

			handlePaymentForOrder(order);

		} catch (Exception e) {
			menuView.displayError("Shopping failed: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handlePaymentForOrder(Order order) {
		try {
			User currentUser = Session.getInstance().getCurrentUser();
			PaymentMethod method = paymentView.selectPaymentMethod(currentUser);

			if (!paymentView.confirmPayment(order.getTotalPrice(), method)) {
				menuView.displayMessage("Payment cancelled.");
				menuView.pressEnterToContinue();
				return;
			}

			Map<String, String> details = paymentView.getPaymentDetails(method, currentUser);

			String[] paymentDetails = new String[0];
			if (method == PaymentMethod.CASH && details.containsKey("citizenId")) {
				paymentDetails = new String[] { details.get("citizenId") };
			} else if (method == PaymentMethod.EWALLET && details.containsKey("walletId")) {
				paymentDetails = new String[] { details.get("walletId") };
			} else if (method == PaymentMethod.CREDIT_CARD) {
				paymentDetails = new String[] {
						details.get("cardNumber"),
						details.get("cardHolder"),
						details.get("expiry"),
						details.get("cvv")
				};
			}

			Payment payment = paymentController.createPayment(
					Session.getInstance().getCurrentUserId(),
					order.getOrderId(),
					order.getTotalPrice(),
					method,
					paymentDetails);

			boolean success = paymentController.processPayment(payment.getPaymentId());

			if (success) {
				paymentView.displayPaymentSuccess(payment);
			} else {
				paymentView.displayPaymentFailed("Payment processing failed");
			}

		} catch (Exception e) {
			paymentView.displayPaymentFailed(e.getMessage());
		} finally {
			menuView.pressEnterToContinue();
		}
	}

	private static void handleViewMyOrders() {
		try {
			List<Order> orders = orderController.getMyOrders();
			orderView.displayOrderList(orders);
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to load orders: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleViewOrderHistory() {
		try {
			List<Order> orders = orderController.getMyOrders();
			if (orders.isEmpty()) {
				menuView.displayMessage("No order history found.");
			} else {
				orderView.displayOrderList(orders);
			}
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to load order history: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleManageBalance() {
		try {
			int choice = userView.displayBalanceMenu();
			User currentUser = Session.getInstance().getCurrentUser();

			switch (choice) {
				case 1:
					double amount = userView.getTopUpAmount();
					currentUser.addFunds(amount);
					menuView.displaySuccess("Successfully added Rp " + String.format("%,.0f", amount));
					menuView.displayMessage("New balance: Rp " + String.format("%,.0f", currentUser.getBalance()));
					break;
				case 2:
					menuView.displayMessage("Current balance: Rp " + String.format("%,.0f", currentUser.getBalance()));
					break;
				case 3:
					return;
				default:
					menuView.displayError("Invalid option");
			}
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Balance operation failed: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleViewProfile() {
		try {
			User currentUser = Session.getInstance().getCurrentUser();
			userView.displayUserProfile(currentUser);

			if (Session.getInstance().isCustomer()) {
				int choice = Input.promptInt("1=Change Address, 0=Back: ");
				if (choice == 1) {
					handleChangeAddress();
				}
			} else {
				menuView.pressEnterToContinue();
			}
		} catch (Exception e) {
			menuView.displayError("Failed to load profile: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleChangeAddress() {
		try {
			User currentUser = Session.getInstance().getCurrentUser();
			System.out.println("\nChanging Address for: " + currentUser.getName());

			com.martminds.model.common.Address newAddress = userView.getAddressFromInput();

			if (newAddress == null) {
				menuView.displayMessage("Address change cancelled.");
				menuView.pressEnterToContinue();
				return;
			}

			currentUser.setAddress(newAddress);
			menuView.displaySuccess("Address updated successfully!");
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to change address: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleAdminMenu() {
		int choice = menuView.displayAdminMenu();

		switch (choice) {
			case 1:
				handleAddProduct();
				break;
			case 2:
				handleViewAllProducts();
				break;
			case 3:
				handleUpdateProduct();
				break;
			case 4:
				handleDeleteProduct();
				break;
			case 5:
				handleViewAllOrders();
				break;
			case 6:
				handleViewAllUsers();
				break;
			case 7:
				return;
			default:
				menuView.displayError("Invalid choice. Please try again.");
		}

		menuView.pressEnterToContinue();
	}

	private static void handleAddProduct() {
		try {
			Map<String, String> data = productView.getProductInput();

			Product product = productController.createProduct(
					data.get("id"),
					data.get("name"),
					Double.parseDouble(data.get("price")),
					Integer.parseInt(data.get("stock")),
					data.get("description"),
					"S001",
					data.get("category"));

			menuView.displaySuccess("Product added successfully!");
			productView.displayProductDetails(product);
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to add product: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleViewAllProducts() {
		try {
			List<Product> products = productController.getAllProducts();
			productView.displayProductList(products);
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to load products: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleUpdateProduct() {
		try {
			String productId = productView.getProductIdInput();
			Product product = productController.getProductById(productId);

			if (product == null) {
				menuView.displayError("Product not found!");
				menuView.pressEnterToContinue();
				return;
			}

			productView.displayProductDetails(product);
			Map<String, String> updates = productView.getProductUpdateInput();

			String name = updates.get("name").isEmpty() ? product.getName() : updates.get("name");
			double price = updates.get("price").isEmpty() ? product.getPrice()
					: Double.parseDouble(updates.get("price"));
			String description = updates.get("description").isEmpty() ? product.getDescription()
					: updates.get("description");
			String category = updates.get("category").isEmpty() ? product.getCategory() : updates.get("category");

			boolean success = productController.updateProduct(productId, name, price, description, category);

			if (success) {
				menuView.displaySuccess("Product updated successfully!");
			} else {
				menuView.displayError("Failed to update product");
			}

			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Update failed: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleDeleteProduct() {
		try {
			String productId = productView.getProductIdInput();
			Product product = productController.getProductById(productId);

			if (product == null) {
				menuView.displayError("Product not found!");
				menuView.pressEnterToContinue();
				return;
			}

			productView.displayProductDetails(product);
			String confirm = Input.promptString("Are you sure you want to delete this product? (yes/no): ");

			if (confirm.equalsIgnoreCase("yes")) {
				boolean success = productController.deleteProduct(productId);
				if (success) {
					menuView.displaySuccess("Product deleted successfully!");
				} else {
					menuView.displayError("Failed to delete product");
				}
			} else {
				menuView.displayMessage("Delete cancelled.");
			}

			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Delete failed: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleViewAllOrders() {
		try {
			List<Order> orders = OrderService.getInstance().getAllOrders();
			adminView.displayAllOrders(orders);
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to load orders: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleViewAllUsers() {
		try {
			List<User> users = UserService.getInstance().getAllUsers();
			adminView.displayAllUsers(users);
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to load users: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleMysteryBox() {
		try {
			System.out.println("\nMystery Box\n");
			System.out.println("Enter your budget and get randomly selected products");
			System.out.println("with 10% automatic discount!\n");

			double budget = Input.promptDoublePositive("Enter your budget (min Rp 10,000): ");

			if (budget < 10000) {
				menuView.displayError("Minimum budget is Rp 10,000");
				menuView.pressEnterToContinue();
				return;
			}

			List<Product> availableProducts = productController.getAllProducts();
			List<Product> selectedProducts = new java.util.ArrayList<>();
			double totalCost = 0;
			double discountedTotal = 0;

			java.util.Collections.shuffle(availableProducts, new java.util.Random());

			for (Product product : availableProducts) {
				if (product.isAvailable()) {
					double discountedPrice = product.getPrice() * 0.9;
					if (discountedTotal + discountedPrice <= budget) {
						selectedProducts.add(product);
						totalCost += product.getPrice();
						discountedTotal += discountedPrice;
					}
				}
			}

			if (selectedProducts.isEmpty()) {
				menuView.displayMessage("No products available within your budget.");
				menuView.pressEnterToContinue();
				return;
			}

			System.out.println("\nMystery Box Contents\n");
			for (Product product : selectedProducts) {
				System.out.printf("- %s (Rp %.0f -> Rp %.0f with 10%% discount)%n",
						product.getName(), product.getPrice(), product.getPrice() * 0.9);
			}
			System.out.println();
			System.out.printf("Original Total   : Rp %.0f%n", totalCost);
			System.out.printf("Discounted Total : Rp %.0f%n", discountedTotal);
			System.out.printf("You save         : Rp %.0f (10%% discount)%n\n", totalCost - discountedTotal);

			String confirm = Input.promptString("Proceed with purchase? (yes/no): ");
			if (!confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("y")) {
				menuView.displayMessage("Purchase cancelled.");
				menuView.pressEnterToContinue();
				return;
			}

			User currentUser = Session.getInstance().getCurrentUser();
			Address address = orderView.selectDeliveryAddress(currentUser);
			if (address == null) {
				menuView.displayMessage("Address selection cancelled.");
				menuView.pressEnterToContinue();
				return;
			}

			List<OrderItem> items = new java.util.ArrayList<>();
			for (Product product : selectedProducts) {
				try {
					OrderItem item = new OrderItem(
							java.util.UUID.randomUUID().toString(),
							product.getProductId(),
							product.getName(),
							1,
							product.getPrice() * 0.9);
					items.add(item);
				} catch (Exception e) {
					menuView.displayError("Error creating order item: " + e.getMessage());
				}
			}

			Order order = orderController.createOrder("S001", items, address);
			menuView.displaySuccess("Mystery Box order created! ID: " + order.getOrderId());

			handlePaymentForOrder(order);

		} catch (Exception e) {
			menuView.displayError("Mystery box failed: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleDriverMenu() {
		int choice = driverView.displayDriverMenu();

		switch (choice) {
			case 1:
				handleViewAvailableOrders();
				break;
			case 2:
				handleViewMyDeliveries();
				break;
			case 3:
				handleAcceptOrder();
				break;
			case 4:
				handleUpdateDeliveryStatus();
				break;
			case 5:
				handleViewProfile();
				break;
			case 6:
				return;
			default:
				menuView.displayError("Invalid option");
				menuView.pressEnterToContinue();
		}
	}

	private static void handleViewAvailableOrders() {
		try {
			List<Order> orders = driverController.getAvailableOrders();
			driverView.displayAvailableOrders(orders);
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to load available orders: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleViewMyDeliveries() {
		try {
			List<Order> orders = driverController.getMyDeliveries();
			driverView.displayMyDeliveries(orders);
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to load deliveries: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleAcceptOrder() {
		try {
			List<Order> orders = driverController.getAvailableOrders();
			driverView.displayAvailableOrders(orders);

			String orderId = driverView.selectOrderToAccept();
			if (orderId.equalsIgnoreCase("cancel")) {
				return;
			}

			boolean success = driverController.acceptOrder(orderId);
			if (success) {
				menuView.displaySuccess("Order accepted successfully!");
			} else {
				menuView.displayError("Failed to accept order");
			}
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to accept order: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}

	private static void handleUpdateDeliveryStatus() {
		try {
			List<Order> orders = driverController.getMyDeliveries();
			driverView.displayMyDeliveries(orders);

			String orderId = driverView.selectOrderToUpdate();
			if (orderId.equalsIgnoreCase("cancel")) {
				return;
			}

			int statusChoice = driverView.selectDeliveryStatus();
			com.martminds.enums.OrderStatus newStatus = null;

			switch (statusChoice) {
				case 1:
					newStatus = com.martminds.enums.OrderStatus.OUT_FOR_DELIVERY;
					break;
				case 2:
					newStatus = com.martminds.enums.OrderStatus.DELIVERED;
					break;
				case 3:
					return;
				default:
					menuView.displayError("Invalid status choice");
					menuView.pressEnterToContinue();
					return;
			}

			boolean success = driverController.updateDeliveryStatus(orderId, newStatus);
			if (success) {
				menuView.displaySuccess("Delivery status updated successfully!");
			} else {
				menuView.displayError("Failed to update delivery status");
			}
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to update status: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}
}
