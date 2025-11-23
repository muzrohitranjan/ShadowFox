import java.util.Scanner;

public class AdvancedBankingApp {
    private static BankingSystem bankingSystem = new BankingSystem();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Advanced Bank Account Management System ===");
        
        while (true) {
            showMenu();
            int choice = getChoice();
            
            switch (choice) {
                case 1: createAccount(); break;
                case 2: accountOperations(); break;
                case 3: loanOperations(); break;
                case 4: systemReports(); break;
                case 5: 
                    System.out.println("Thank you for using Advanced Banking System!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n--- Advanced Banking Menu ---");
        System.out.println("1. Account Management");
        System.out.println("2. Account Operations");
        System.out.println("3. Loan Operations");
        System.out.println("4. System Reports");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void createAccount() {
        System.out.println("\n--- Account Types ---");
        System.out.println("1. Savings (2.5% interest, $1000 min, no overdraft)");
        System.out.println("2. Checking (0.1% interest, $500 min, $500 overdraft)");
        System.out.println("3. Premium (3.5% interest, $5000 min, $2000 overdraft, no fees)");
        System.out.println("4. Business (1.5% interest, $10000 min, $5000 overdraft)");
        
        System.out.print("Select account type (1-4): ");
        int typeChoice = getChoice();
        
        AccountType[] types = {AccountType.SAVINGS, AccountType.CHECKING, AccountType.PREMIUM, AccountType.BUSINESS};
        if (typeChoice < 1 || typeChoice > 4) {
            System.out.println("Invalid account type.");
            return;
        }
        
        AccountType accountType = types[typeChoice - 1];
        
        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine();
        System.out.print("Enter initial balance (min $" + accountType.getMinimumBalance() + "): ");
        
        try {
            double balance = Double.parseDouble(scanner.nextLine());
            BankAccount account = bankingSystem.createAccount(name, balance, accountType);
            
            if (account != null) {
                System.out.println("Account created successfully!");
                System.out.println("Account Number: " + account.getAccountNumber());
                System.out.println("Account Type: " + accountType);
            } else {
                System.out.println("Failed to create account. Check minimum balance requirement.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid balance amount.");
        }
    }

    private static void accountOperations() {
        System.out.print("Enter account number: ");
        String accountNum = scanner.nextLine();
        BankAccount account = bankingSystem.getAccount(accountNum);
        
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }
        
        while (true) {
            System.out.println("\n--- Account: " + account.getAccountNumber() + " ---");
            System.out.println("Balance: $" + account.getBalance());
            System.out.println("Type: " + account.getAccountType());
            System.out.println("Status: " + (account.isActive() ? "Active" : "Frozen"));
            System.out.println("Daily Transactions: " + account.getDailyTransactionCount());
            System.out.println("Total Fees: $" + account.getTotalFees());
            
            System.out.println("\n1. Deposit  2. Withdraw  3. Transfer  4. Interest  5. Freeze/Unfreeze  6. Back");
            System.out.print("Choice: ");
            
            int choice = getChoice();
            switch (choice) {
                case 1: deposit(account); break;
                case 2: withdraw(account); break;
                case 3: transfer(account); break;
                case 4: account.calculateInterest(); 
                       System.out.println("Interest calculated. New balance: $" + account.getBalance()); break;
                case 5: 
                    if (account.isActive()) {
                        account.freezeAccount();
                        System.out.println("Account frozen.");
                    } else {
                        account.unfreezeAccount();
                        System.out.println("Account unfrozen.");
                    }
                    break;
                case 6: return;
            }
        }
    }

    private static void loanOperations() {
        System.out.println("\n--- Loan Operations ---");
        System.out.println("1. Create Loan  2. Make Payment  3. View Loans");
        System.out.print("Choice: ");
        
        int choice = getChoice();
        switch (choice) {
            case 1: createLoan(); break;
            case 2: makeLoanPayment(); break;
            case 3: viewLoans(); break;
        }
    }

    private static void createLoan() {
        System.out.print("Enter account number: ");
        String accountNum = scanner.nextLine();
        
        if (bankingSystem.getAccount(accountNum) == null) {
            System.out.println("Account not found.");
            return;
        }
        
        System.out.print("Enter loan amount: ");
        double principal = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter annual interest rate (e.g., 0.05 for 5%): ");
        double rate = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter term in months: ");
        int term = Integer.parseInt(scanner.nextLine());
        
        LoanAccount loan = bankingSystem.createLoan(accountNum, principal, rate, term);
        if (loan != null) {
            System.out.println("Loan created! ID: " + loan.getLoanId());
            System.out.println("Monthly payment: $" + String.format("%.2f", loan.getNextPaymentAmount()));
        } else {
            System.out.println("Failed to create loan.");
        }
    }

    private static void makeLoanPayment() {
        System.out.print("Enter loan ID: ");
        String loanId = scanner.nextLine();
        System.out.print("Enter account number: ");
        String accountNum = scanner.nextLine();
        System.out.print("Enter payment amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        
        if (bankingSystem.makeLoanPayment(loanId, accountNum, amount)) {
            LoanAccount loan = bankingSystem.getLoan(loanId);
            System.out.println("Payment successful!");
            System.out.println("Remaining balance: $" + loan.getRemainingBalance());
        } else {
            System.out.println("Payment failed.");
        }
    }

    private static void systemReports() {
        System.out.println("\n--- System Reports ---");
        System.out.println("Total Accounts: " + bankingSystem.getTotalAccounts());
        System.out.println("Total System Balance: $" + bankingSystem.getTotalSystemBalance());
        System.out.println("High Value Accounts (>$25,000): " + bankingSystem.getHighValueAccounts(25000.0).size());
        
        System.out.println("\nAccounts by Type:");
        for (AccountType type : AccountType.values()) {
            System.out.println(type + ": " + bankingSystem.getAccountsByType(type).size());
        }
    }

    private static void viewLoans() {
        var loans = bankingSystem.getAllLoans();
        if (loans.isEmpty()) {
            System.out.println("No loans found.");
            return;
        }
        
        System.out.println("\n--- All Loans ---");
        for (LoanAccount loan : loans) {
            System.out.println("ID: " + loan.getLoanId() + 
                             " | Balance: $" + loan.getRemainingBalance() + 
                             " | Status: " + (loan.isActive() ? "Active" : "Paid Off"));
        }
    }

    private static void deposit(BankAccount account) {
        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        if (account.deposit(amount)) {
            System.out.println("Deposit successful! New balance: $" + account.getBalance());
        } else {
            System.out.println("Deposit failed.");
        }
    }

    private static void withdraw(BankAccount account) {
        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        if (account.withdraw(amount)) {
            System.out.println("Withdrawal successful! New balance: $" + account.getBalance());
        } else {
            System.out.println("Withdrawal failed.");
        }
    }

    private static void transfer(BankAccount account) {
        System.out.print("Enter target account number: ");
        String targetNum = scanner.nextLine();
        BankAccount target = bankingSystem.getAccount(targetNum);
        
        if (target == null) {
            System.out.println("Target account not found.");
            return;
        }
        
        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        
        if (account.transfer(target, amount)) {
            System.out.println("Transfer successful!");
        } else {
            System.out.println("Transfer failed.");
        }
    }

    private static int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}