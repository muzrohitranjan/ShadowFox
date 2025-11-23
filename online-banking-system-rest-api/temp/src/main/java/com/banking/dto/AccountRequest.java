package com.banking.dto;

import com.banking.entity.Account;
import jakarta.validation.constraints.NotNull;

public class AccountRequest {
    @NotNull
    private Account.AccountType accountType;

    public AccountRequest() {}

    public AccountRequest(Account.AccountType accountType) {
        this.accountType = accountType;
    }

    public Account.AccountType getAccountType() { return accountType; }
    public void setAccountType(Account.AccountType accountType) { this.accountType = accountType; }
}