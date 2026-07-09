package com.backend.investment.common;

public class ConstantResponse {
    
    public static final int SUCCESS = 200;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_SERVER_ERROR = 500;

     public static final String PHONE_REQUIRED = "Phone number is required.";

    public static final String PASSWORD_REQUIRED = "Password is required.";

    public static final String WITHDRAW_PASSWORD_REQUIRED = "Withdraw password is required.";

    public static final String PHONE_ALREADY_EXISTS = "Phone number already registered.";

    public static final String INVALID_LOGIN = "Invalid phone number or password.";

    public static final String REGISTRATION_SUCCESS = "Registration Successful.";

}
