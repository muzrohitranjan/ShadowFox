import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class BankingSystem {
    private Map<String, BankAccount> accounts;
    private Map<String, LoanAccount> loans;
    private int nextAccountNumber;
    private int nextLoanId;

    public BankingSystem() {
        this.accounts = new HashMap<>();
        this.loans = new HashMap<>();
        this.nextAccountNumber = 1000;
        this.nextLoanId = 5000;
    }

    public BankAccount createAccount(String accountHolder, double initialBalance, AccountType accountType) {
        if (accountHolder == null || accountHolder.trim().isEmpty()) return null;
        if (initialBalance < 0 || initialBalance < accountType.getMinimumBalance()) return null;
        
        String accountNumber = String.valueOf(nextAccountNumber++);
        BankAccount account = new BankAccount(accountNumber, accountHolder, initialBalance, accountType);
        accounts.put(accountNumber, account);
        return account;
    }
    
    public BankAccount createAccount(String accountHolder, double initialBalance) {
        return createAccount(accountHolder, initialBalance, AccountType.CHECKING);
    }

    public LoanAccount createLoan(String accountNumber, double principal, double interestRate, int termMonths) {
        if (!accounts.containsKey(accountNumber) || principal <= 0) return null;
        
        String loanId = "L" + nextLoanId++;
        LoanAccount loan = new LoanAccount(loanId, accountNumber, principal, interestRate, termMonths);
        loans.put(loanId, loan);
        
        // Deposit loan amount to account
        BankAccount account = accounts.get(accountNumber);
        account.deposit(principal);
        
        return loan;
    }
    
    public boolean makeLoanPayment(String loanId, String accountNumber, double amount) {
        LoanAccount loan = loans.get(loanId);
        BankAccount account = accounts.get(accountNumber);
        
        if (loan == null || account == null || !loan.isActive()) return false;
        if (!account.withdraw(amount)) return false;
        
        return loan.makePayment(amount);
    }
    
    public List<BankAccount> getAccountsByType(AccountType type) {
        return accounts.values().stream()
                .filter(account -> account.getAccountType() == type)
                .collect(Collectors.toList());
    }
    
    public List<BankAccount> getHighValueAccounts(double threshold) {
        return accounts.values().stream()
                .filter(account -> account.getBalance() > threshold)
                .collect(Collectors.toList());
    }
    
    public double getTotalSystemBalance() {
        return accounts.values().stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();
    }
    
    public BankAccount getAccount(String accountNumber) { return accounts.get(accountNumber); }
    public LoanAccount getLoan(String loanId) { return loans.get(loanId); }
    
    public boolean closeAccount(String accountNumber) {
        BankAccount account = accounts.get(accountNumber);
        if (account == null || account.getBalance() != 0) return false;
        accounts.remove(accountNumber);
        return true;
    }
    
    public int getTotalAccounts() { return accounts.size(); }
    public List<BankAccount> getAllAccounts() { return new ArrayList<>(accounts.values()); }
    public List<LoanAccount> getAllLoans() { return new ArrayList<>(loans.values()); }
}