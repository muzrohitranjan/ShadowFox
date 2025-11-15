import java.util.Scanner;

public class BankingApp {
    private static BankingSystem bankingSystem = new BankingSystem();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Bank Account Management System ===");
        
        while (true) {
            showMenu();
            int choice = getChoice();
            
            switch (choice) {
                case 1: createAccount(); break;
                case 2: deposit(); break;
                case 3: withdraw(); break;
                case 4: checkBalance(); break;
                case 5: viewTransactionHistory(); break;
                case 6: transfer(); break;
                case 7: calculateInterest(); break;
                case 8: closeAccount(); break;
                case 9: 
                    System.out.println("Thank you for using Bank Account Management System!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n--- Banking Menu ---");
        System.out.println("1. Create Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Check Balance");
        System.out.println("5. View Transaction History");
        System.out.println("6. Transfer Money");
        System.out.println("7. Calculate Interest");
        System.out.println("8. Close Account");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void createAccount() {
        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine();
        System.out.print("Enter initial balance: ");
        
        try {
            double balance = Double.parseDouble(scanner.nextLine());
            BankAccount account = bankingSystem.createAccount(name, balance);
            
            if (account != null) {
                System.out.println("Account created successfully!");
                System.out.println("Account Number: " + account.getAccountNumber());
            } else {
                System.out.println("Failed to create account. Check your inputs.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid balance amount.");
        }
    }

    private static void deposit() {
        BankAccount account = getAccountFromUser();
        if (account == null) return;
        
        System.out.print("Enter deposit amount: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            if (account.deposit(amount)) {
                System.out.println("Deposit successful! New balance: $" + account.getBalance());
            } else {
                System.out.println("Deposit failed. Invalid amount.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        }
    }

    private static void withdraw() {
        BankAccount account = getAccountFromUser();
        if (account == null) return;
        
        System.out.print("Enter withdrawal amount: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            if (account.withdraw(amount)) {
                System.out.println("Withdrawal successful! New balance: $" + account.getBalance());
            } else {
                System.out.println("Withdrawal failed. Insufficient funds or invalid amount.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        }
    }

    private static void checkBalance() {
        BankAccount account = getAccountFromUser();
        if (account != null) {
            System.out.println("Current balance: $" + account.getBalance());
        }
    }

    private static void viewTransactionHistory() {
        BankAccount account = getAccountFromUser();
        if (account == null) return;
        
        System.out.println("\n--- Transaction History ---");
        for (Transaction transaction : account.getTransactionHistory()) {
            System.out.println(transaction);
        }
    }

    private static void transfer() {
        System.out.print("Enter source account number: ");
        String sourceAccountNum = scanner.nextLine();
        BankAccount sourceAccount = bankingSystem.getAccount(sourceAccountNum);
        
        if (sourceAccount == null) {
            System.out.println("Source account not found.");
            return;
        }
        
        System.out.print("Enter target account number: ");
        String targetAccountNum = scanner.nextLine();
        BankAccount targetAccount = bankingSystem.getAccount(targetAccountNum);
        
        if (targetAccount == null) {
            System.out.println("Target account not found.");
            return;
        }
        
        System.out.print("Enter transfer amount: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            if (sourceAccount.transfer(targetAccount, amount)) {
                System.out.println("Transfer successful!");
                System.out.println("Source balance: $" + sourceAccount.getBalance());
                System.out.println("Target balance: $" + targetAccount.getBalance());
            } else {
                System.out.println("Transfer failed. Check amount and account balances.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        }
    }

    private static void calculateInterest() {
        BankAccount account = getAccountFromUser();
        if (account == null) return;
        
        double balanceBefore = account.getBalance();
        account.calculateInterest();
        double balanceAfter = account.getBalance();
        
        System.out.println("Interest calculated!");
        System.out.println("Balance before: $" + balanceBefore);
        System.out.println("Balance after: $" + balanceAfter);
        System.out.println("Interest earned: $" + (balanceAfter - balanceBefore));
    }

    private static void closeAccount() {
        System.out.print("Enter account number to close: ");
        String accountNum = scanner.nextLine();
        
        if (bankingSystem.closeAccount(accountNum)) {
            System.out.println("Account closed successfully!");
        } else {
            System.out.println("Failed to close account. Account not found or has non-zero balance.");
        }
    }

    private static BankAccount getAccountFromUser() {
        System.out.print("Enter account number: ");
        String accountNum = scanner.nextLine();
        BankAccount account = bankingSystem.getAccount(accountNum);
        
        if (account == null) {
            System.out.println("Account not found.");
        }
        return account;
    }
}