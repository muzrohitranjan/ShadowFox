// Banking System Classes (JavaScript version)
class Transaction {
    constructor(type, amount, balanceAfter, description) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = new Date();
        this.description = description;
    }
}

class BankAccount {
    constructor(accountNumber, accountHolder, initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.interestRate = 0.02;
        this.overdraftLimit = 500.0;
        this.transactionHistory = [];
        
        if (initialBalance > 0) {
            this.transactionHistory.push(new Transaction("INITIAL", initialBalance, this.balance, "Account opened"));
        }
    }

    deposit(amount) {
        if (amount <= 0) return false;
        this.balance += amount;
        this.transactionHistory.push(new Transaction("DEPOSIT", amount, this.balance, "Cash deposit"));
        return true;
    }

    withdraw(amount) {
        if (amount <= 0) return false;
        if (this.balance + this.overdraftLimit < amount) return false;
        this.balance -= amount;
        this.transactionHistory.push(new Transaction("WITHDRAWAL", amount, this.balance, "Cash withdrawal"));
        return true;
    }

    calculateInterest() {
        if (this.balance > 0) {
            const interest = this.balance * (this.interestRate / 12);
            this.balance += interest;
            this.transactionHistory.push(new Transaction("INTEREST", interest, this.balance, "Monthly interest"));
            return interest;
        }
        return 0;
    }

    transfer(targetAccount, amount) {
        if (amount <= 0 || !targetAccount) return false;
        if (this.balance + this.overdraftLimit < amount) return false;
        
        this.balance -= amount;
        targetAccount.balance += amount;
        
        this.transactionHistory.push(new Transaction("TRANSFER_OUT", amount, this.balance, 
            `Transfer to ${targetAccount.accountNumber}`));
        targetAccount.transactionHistory.push(new Transaction("TRANSFER_IN", amount, 
            targetAccount.balance, `Transfer from ${this.accountNumber}`));
        
        return true;
    }
}

class BankingSystem {
    constructor() {
        this.accounts = new Map();
        this.nextAccountNumber = 1000;
        this.loadFromStorage();
    }

    createAccount(accountHolder, initialBalance) {
        if (!accountHolder || accountHolder.trim() === '' || initialBalance < 0) return null;
        
        const accountNumber = String(this.nextAccountNumber++);
        const account = new BankAccount(accountNumber, accountHolder, initialBalance);
        this.accounts.set(accountNumber, account);
        this.saveToStorage();
        return account;
    }

    getAccount(accountNumber) {
        return this.accounts.get(accountNumber);
    }

    getAllAccounts() {
        return Array.from(this.accounts.values());
    }

    saveToStorage() {
        const data = {
            accounts: Array.from(this.accounts.entries()),
            nextAccountNumber: this.nextAccountNumber
        };
        localStorage.setItem('bankingSystem', JSON.stringify(data));
    }

    loadFromStorage() {
        const data = localStorage.getItem('bankingSystem');
        if (data) {
            const parsed = JSON.parse(data);
            this.nextAccountNumber = parsed.nextAccountNumber || 1000;
            
            if (parsed.accounts) {
                parsed.accounts.forEach(([accountNumber, accountData]) => {
                    const account = new BankAccount(accountData.accountNumber, accountData.accountHolder, 0);
                    account.balance = accountData.balance;
                    account.interestRate = accountData.interestRate;
                    account.overdraftLimit = accountData.overdraftLimit;
                    account.transactionHistory = accountData.transactionHistory.map(t => {
                        const transaction = new Transaction(t.type, t.amount, t.balanceAfter, t.description);
                        transaction.timestamp = new Date(t.timestamp);
                        return transaction;
                    });
                    this.accounts.set(accountNumber, account);
                });
            }
        }
    }
}

// Global banking system instance
const bankingSystem = new BankingSystem();