package com.banking.service;

import com.banking.dto.AccountRequest;
import com.banking.entity.Account;
import com.banking.entity.User;
import com.banking.exception.CustomExceptions;
import com.banking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;

    public Account createAccount(AccountRequest request, String username) {
        User user = userService.findByUsername(username);
        
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setUser(user);
        
        return accountRepository.save(account);
    }

    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomExceptions.AccountNotFoundException("Account not found"));
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CustomExceptions.AccountNotFoundException("Account not found"));
    }

    public List<Account> getUserAccounts(String username) {
        User user = userService.findByUsername(username);
        return accountRepository.findByUserUserId(user.getUserId());
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "ACC" + String.format("%010d", new Random().nextInt(1000000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}