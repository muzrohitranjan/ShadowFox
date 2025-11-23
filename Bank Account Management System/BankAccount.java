import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BankAccount {
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private AccountType accountType;
    private List<Transaction> transactionHistory;
    private boolean isActive;
    private LocalDate lastTransactionDate;
    private int dailyTransactionCount;
    private double dailyTransactionAmount;
    private LocalDate accountOpenDate;
    private double totalFees;

    public BankAccount(String accountNumber, String accountHolder, double initialBalance, AccountType accountType) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.accountType = accountType;
        this.transactionHistory = new ArrayList<>();
        this.isActive = true;
        this.lastTransactionDate = LocalDate.now();
        this.dailyTransactionCount = 0;
        this.dailyTransactionAmount = 0.0;
        this.accountOpenDate = LocalDate.now();
        this.totalFees = 0.0;
        
        if (initialBalance > 0) {
            transactionHistory.add(new Transaction("INITIAL", initialBalance, balance, "Account opened"));
        }
    }

    public boolean deposit(double amount) {
        if (amount <= 0 || !isActive || !checkDailyLimits(amount)) return false;
        
        balance += amount;
        updateDailyTracking(amount);
        transactionHistory.add(new Transaction("DEPOSIT", amount, balance, "Cash deposit"));
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || !isActive || !checkDailyLimits(amount)) return false;
        if (balance + accountType.getOverdraftLimit() < amount) return false;
        
        balance -= amount;
        updateDailyTracking(amount);
        
        // Apply overdraft fee if applicable
        if (balance < 0) {
            double overdraftFee = 35.0;
            balance -= overdraftFee;
            totalFees += overdraftFee;
            transactionHistory.add(new Transaction("FEE", overdraftFee, balance, "Overdraft fee"));
        }
        
        transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance, "Cash withdrawal"));
        return true;
    }

    public double getBalance() {
        return balance;
    }

    public void calculateInterest() {
        if (balance > accountType.getMinimumBalance()) {
            double interest = balance * (accountType.getInterestRate() / 12);
            balance += interest;
            transactionHistory.add(new Transaction("INTEREST", interest, balance, "Monthly interest"));
        } else if (balance > 0 && balance < accountType.getMinimumBalance()) {
            double maintenanceFee = 25.0;
            balance -= maintenanceFee;
            totalFees += maintenanceFee;
            transactionHistory.add(new Transaction("FEE", maintenanceFee, balance, "Minimum balance fee"));
        }
    }

    public boolean transfer(BankAccount targetAccount, double amount) {
        if (amount <= 0 || targetAccount == null || !isActive || !targetAccount.isActive) return false;
        if (!checkDailyLimits(amount)) return false;
        if (balance + accountType.getOverdraftLimit() < amount) return false;
        
        // Apply transfer fee for external transfers
        double transferFee = accountType == AccountType.PREMIUM ? 0.0 : 3.0;
        double totalAmount = amount + transferFee;
        
        if (balance + accountType.getOverdraftLimit() < totalAmount) return false;
        
        balance -= totalAmount;
        targetAccount.balance += amount;
        
        if (transferFee > 0) {
            totalFees += transferFee;
            transactionHistory.add(new Transaction("FEE", transferFee, balance, "Transfer fee"));
        }
        
        updateDailyTracking(amount);
        transactionHistory.add(new Transaction("TRANSFER_OUT", amount, balance, 
            "Transfer to " + targetAccount.accountNumber));
        targetAccount.transactionHistory.add(new Transaction("TRANSFER_IN", amount, 
            targetAccount.balance, "Transfer from " + this.accountNumber));
        
        return true;
    }

    private boolean checkDailyLimits(double amount) {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastTransactionDate)) {
            dailyTransactionCount = 0;
            dailyTransactionAmount = 0.0;
            lastTransactionDate = today;
        }
        
        int maxDailyTransactions = accountType == AccountType.BUSINESS ? 50 : 10;
        double maxDailyAmount = accountType == AccountType.BUSINESS ? 100000.0 : 
                               accountType == AccountType.PREMIUM ? 50000.0 : 10000.0;
        
        return dailyTransactionCount < maxDailyTransactions && 
               (dailyTransactionAmount + amount) <= maxDailyAmount;
    }
    
    private void updateDailyTracking(double amount) {
        dailyTransactionCount++;
        dailyTransactionAmount += amount;
        lastTransactionDate = LocalDate.now();
    }
    
    public boolean freezeAccount() {
        isActive = false;
        transactionHistory.add(new Transaction("FREEZE", 0, balance, "Account frozen"));
        return true;
    }
    
    public boolean unfreezeAccount() {
        isActive = true;
        transactionHistory.add(new Transaction("UNFREEZE", 0, balance, "Account unfrozen"));
        return true;
    }
    
    public double getAccountAge() {
        return ChronoUnit.DAYS.between(accountOpenDate, LocalDate.now());
    }
    
    public List<Transaction> getTransactionHistory() { return new ArrayList<>(transactionHistory); }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public AccountType getAccountType() { return accountType; }
    public boolean isActive() { return isActive; }
    public double getTotalFees() { return totalFees; }
    public int getDailyTransactionCount() { return dailyTransactionCount; }
    public double getDailyTransactionAmount() { return dailyTransactionAmount; }
}