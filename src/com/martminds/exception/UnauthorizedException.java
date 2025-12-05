package com.martminds.exception;

// When user attempts an unauthorized action
public class UnauthorizedException extends Exception {
    private String userId;
    private String attemptedAction;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, String userId, String attemptedAction) {
        super(message);
        this.userId = userId;
        this.attemptedAction = attemptedAction;
    }

    public String getUserId() {
        return userId;
    }

    public String getAttemptedAction() {
        return attemptedAction;
    }
}
