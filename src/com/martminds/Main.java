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

	private static MenuView menuView;
	private static UserView userView;
	private static ProductView productView;
	private static OrderView orderView;
	private static PaymentView paymentView;
	private static AdminView adminView;

	public static void main(String[] args) {
		System.out.println("Welcome to MartMinds!");

		initialize();
		run();

		System.out.println("\nThank you for using MartMinds!");
	}

	private static void initialize() {
		authController = new AuthController();
		productController = new ProductController();
		orderController = new OrderController(OrderService.getInstance());
		paymentController = new PaymentController();

		menuView = new MenuView();
		userView = new UserView();
		productView = new ProductView();
		orderView = new OrderView();
		paymentView = new PaymentView();
		adminView = new AdminView();
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
			Map<String, String> data = userView.getRegistrationData();
			String userId = "C" + RandomGenerator.generateId().substring(0, 3);

			authController.register(
					userId,
					data.get("name"),
					data.get("email"),
					data.get("password"),
					data.get("phone"),
					UserRole.CUSTOMER,
					0.0);

			menuView.displaySuccess("Registration successful! You can now login.");
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
				handleViewMyOrders();
				break;
			case 3:
				handleViewOrderHistory();
				break;
			case 4:
				handleManageBalance();
				break;
			case 5:
				handleViewProfile();
				break;
			case 6:
				handleLogout();
				break;
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

			Address address = orderView.getDeliveryAddress();
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
			if (!paymentView.confirmPayment(order.getTotalPrice())) {
				menuView.displayMessage("Payment cancelled.");
				menuView.pressEnterToContinue();
				return;
			}

			PaymentMethod method = paymentView.selectPaymentMethod();
			Map<String, String> details = paymentView.getPaymentDetails(method);

			String[] paymentDetails = new String[0];
			if (method == PaymentMethod.EWALLET && details.containsKey("walletId")) {
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
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Failed to load profile: " + e.getMessage());
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
				handleLogout();
				break;
			default:
				menuView.displayError("Invalid option");
				menuView.pressEnterToContinue();
		}
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

	private static void handleLogout() {
		try {
			authController.logout();
			menuView.displaySuccess("Logged out successfully!");
			menuView.pressEnterToContinue();
		} catch (Exception e) {
			menuView.displayError("Logout failed: " + e.getMessage());
			menuView.pressEnterToContinue();
		}
	}
}
