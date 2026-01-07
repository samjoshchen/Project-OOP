package com.martminds.util;

import com.martminds.model.user.User;
import com.martminds.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class Session {
    private static Session instance;

    private User currentUser;
    private String sessionId;
    private LocalDateTime loginTimestamp;

    private Session() {
        this.currentUser = null;
        this.sessionId = null;
        this.loginTimestamp = null;
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    @Deprecated
    public static Session getSession() {
        return getInstance();
    }

    public void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot login null user");
        }

        if (this.currentUser != null) {
            Logger.warning("User already logged in. Logging out previous user.");
            logout();
        }

        this.currentUser = user;
        this.sessionId = UUID.randomUUID().toString();
        this.loginTimestamp = DateTimeUtil.now();

        Logger.info("User logged in: " + user.getEmail() + " (Role: " + user.getRole() + ")");
    }

    public void logout() {
        if (currentUser != null) {
            Logger.info("User logged out: " + currentUser.getEmail());
        }

        this.currentUser = null;
        this.sessionId = null;
        this.loginTimestamp = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public UserRole getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public String getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }

    public String getSessionId() {
        return sessionId;
    }

    public LocalDateTime getLoginTimestamp() {
        return loginTimestamp;
    }

    public boolean isCustomer() {
        return isLoggedIn() && currentUser.getRole() == UserRole.CUSTOMER;
    }

    public boolean isDriver() {
        return isLoggedIn() && currentUser.getRole() == UserRole.DRIVER;
    }

    public boolean isAdmin() {
        return isLoggedIn() && currentUser.getRole() == UserRole.ADMIN;
    }

    public void requireLogin() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("You must be logged in to perform this action");
        }
    }

    public void requireRole(UserRole role) {
        requireLogin();
        if (currentUser.getRole() != role) {
            throw new IllegalStateException("Access denied. Required role: " + role);
        }
    }

    public void requireAdmin() {
        requireRole(UserRole.ADMIN);
    }

    public void requireCustomer() {
        requireRole(UserRole.CUSTOMER);
    }

    public void requireDriver() {
        requireRole(UserRole.DRIVER);
    }

    public long getSessionDurationMinutes() {
        if (!isLoggedIn() || loginTimestamp == null) {
            return 0;
        }

        try {
            LocalDateTime currentTime = DateTimeUtil.now();
            return DateTimeUtil.minutesBetween(loginTimestamp, currentTime);
        } catch (Exception e) {
            Logger.error("Failed to calculate session duration: " + e.getMessage());
            return 0;
        }
    }

    public String getSessionInfo() {
        if (!isLoggedIn()) {
            return "No active session";
        }

        return String.format("Session ID: %s\nUser: %s (%s)\nRole: %s\nLogged in at: %s\nDuration: %d minutes",
                sessionId,
                currentUser.getName(),
                currentUser.getEmail(),
                currentUser.getRole(),
                loginTimestamp,
                getSessionDurationMinutes());
    }
}
