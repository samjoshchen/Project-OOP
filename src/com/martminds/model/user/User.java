package com.martminds.model.user;

import com.martminds.enums.*;
import com.martminds.model.common.Address;

public abstract class User {
	private String userId;
	private String name;
	private String email;
	private String password;
	private String phone;
	private double balance;
	private UserRole role;
	private Address address;

	public User(String userId, String name, String email, String password, String phone, double balance,
			UserRole role) {
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.balance = balance;
		this.role = role;
		this.address = null;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public boolean login(String email, String password) {
		return this.email.equals(email) && this.password.equals(password);
	}

	public void updateProfile(String name, String phone) {
		this.name = name;
		this.phone = phone;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void addFunds(double amount) {
		if (amount > 0) {
			this.balance += amount;
		}
	}

	public boolean withdrawFunds(double amount) {
		if (amount > 0 && amount <= this.balance) {
			this.balance -= amount;
			return true;
		}
		return false;
	}
}
