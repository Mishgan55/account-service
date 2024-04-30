package com.example.accountservice.utill;

import java.math.BigDecimal;

public class PropertyUtil {
    public static final String USER_NOT_FOUND = "User not found with ID: %s";
    public static final String ACCOUNT_NOT_FOUND = "Account not found with ID: %s";
    public static final String FAILED_TO_UPDATE_MESSAGE = "Failed to update account after %s retries.";
    public static final String DUPLICATE_USER_DOCUMENT = "User with this document already exists";
    public static final String DELETE_SUCCESSFUL = "Delete successful";
    public static final String EDIT_SUCCESSFUL = "Edit successful";
    public static final String DEPOSIT_SUCCESSFUL = "Deposit successful";
    public static final String ADD_SUCCESSFUL = "Added successful";
    public static final int MAX_RETRIES = 5;
    public static final String TRANSFER_SUCCESSFUL = "Transfer successful!";
    public static final BigDecimal COMMISSION_VALUE = BigDecimal.valueOf(0.07);
    public static final String ACCOUNTS_NOT_FOUND_FOR_USER = "User with id: %s doesn't have any accounts";
    public static final String OPEN_API_URL = "https://open.er-api.com/v6/latest";
    public static final String NOT_ENOUGH_BALANCE = "Your balance with Id: %s is not enough for this transaction";
    public static final String VALIDATION_MESSAGE = "Validation error(s): ";

}
