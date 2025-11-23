package com.banking.controller;

import com.banking.dto.AccountRequest;
import com.banking.entity.Account;
import com.banking.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Account Management", description = "Account management endpoints")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    @Operation(summary = "Create new bank account")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountRequest request, Authentication auth) {
        Account account = accountService.createAccount(request, auth.getName());
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account details by ID")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/my-accounts")
    @Operation(summary = "Get all accounts for current user")
    public ResponseEntity<List<Account>> getUserAccounts(Authentication auth) {
        List<Account> accounts = accountService.getUserAccounts(auth.getName());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "Get account by account number")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }
}