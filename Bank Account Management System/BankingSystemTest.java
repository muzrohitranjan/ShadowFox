import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankingSystemTest {
    private BankingSystem bankingSystem;

    @BeforeEach
    void setUp() {
        bankingSystem = new BankingSystem();
    }

    // Create Account Tests (5 test cases)
    @Test
    void testCreateAccountValid() {
        BankAccount account = bankingSystem.createAccount("John Doe", 1000.0);
        assertNotNull(account);
        assertEquals("John Doe", account.getAccountHolder());
        assertEquals(1000.0, account.getBalance(), 0.01);
        assertEquals(AccountType.CHECKING, account.getAccountType());
    }

    @Test
    void testCreateAccountZeroBalance() {
        BankAccount account = bankingSystem.createAccount("Jane Smith", 500.0); // Minimum for checking
        assertNotNull(account);
        assertEquals(500.0, account.getBalance(), 0.01);
    }

    @Test
    void testCreateAccountNullName() {
        BankAccount account = bankingSystem.createAccount(null, 1000.0);
        assertNull(account);
    }

    @Test
    void testCreateAccountEmptyName() {
        BankAccount account = bankingSystem.createAccount("", 1000.0);
        assertNull(account);
    }

    @Test
    void testCreateAccountNegativeBalance() {
        BankAccount account = bankingSystem.createAccount("Bob Johnson", -500.0);
        assertNull(account);
    }

    // Get Account Tests (5 test cases)
    @Test
    void testGetExistingAccount() {
        BankAccount created = bankingSystem.createAccount("Alice Brown", 2000.0);
        BankAccount retrieved = bankingSystem.getAccount(created.getAccountNumber());
        assertNotNull(retrieved);
        assertEquals(created.getAccountNumber(), retrieved.getAccountNumber());
    }

    @Test
    void testGetNonExistentAccount() {
        BankAccount account = bankingSystem.getAccount("99999");
        assertNull(account);
    }

    @Test
    void testGetAccountAfterMultipleCreations() {
        BankAccount account1 = bankingSystem.createAccount("User1", 1000.0);
        BankAccount account2 = bankingSystem.createAccount("User2", 2000.0);
        BankAccount account3 = bankingSystem.createAccount("User3", 3000.0);
        
        assertEquals(account2, bankingSystem.getAccount(account2.getAccountNumber()));
    }

    @Test
    void testGetAccountWithNullNumber() {
        BankAccount account = bankingSystem.getAccount(null);
        assertNull(account);
    }

    @Test
    void testGetAccountSequentialNumbers() {
        BankAccount account1 = bankingSystem.createAccount("User1", 1000.0);
        BankAccount account2 = bankingSystem.createAccount("User2", 2000.0);
        
        int num1 = Integer.parseInt(account1.getAccountNumber());
        int num2 = Integer.parseInt(account2.getAccountNumber());
        assertEquals(1, num2 - num1);
    }

    // Close Account Tests (5 test cases)
    @Test
    void testCloseAccountWithZeroBalance() {
        BankAccount account = bankingSystem.createAccount("Test User", 1000.0);
        account.withdraw(1000.0); // Make balance zero
        assertTrue(bankingSystem.closeAccount(account.getAccountNumber()));
        assertNull(bankingSystem.getAccount(account.getAccountNumber()));
    }

    @Test
    void testCloseAccountWithPositiveBalance() {
        BankAccount account = bankingSystem.createAccount("Test User", 1000.0);
        assertFalse(bankingSystem.closeAccount(account.getAccountNumber()));
        assertNotNull(bankingSystem.getAccount(account.getAccountNumber()));
    }

    @Test
    void testCloseAccountWithNegativeBalance() {
        BankAccount account = bankingSystem.createAccount("Test User", 1000.0);
        account.withdraw(1200.0); // Make balance negative
        assertFalse(bankingSystem.closeAccount(account.getAccountNumber()));
        assertNotNull(bankingSystem.getAccount(account.getAccountNumber()));
    }

    @Test
    void testCloseNonExistentAccount() {
        assertFalse(bankingSystem.closeAccount("99999"));
    }

    @Test
    void testCloseAccountReducesTotalCount() {
        BankAccount account1 = bankingSystem.createAccount("User1", 0.0);
        BankAccount account2 = bankingSystem.createAccount("User2", 0.0);
        
        assertEquals(2, bankingSystem.getTotalAccounts());
        bankingSystem.closeAccount(account1.getAccountNumber());
        assertEquals(1, bankingSystem.getTotalAccounts());
    }

    // Total Accounts Tests (5 test cases)
    @Test
    void testInitialTotalAccounts() {
        assertEquals(0, bankingSystem.getTotalAccounts());
    }

    @Test
    void testTotalAccountsAfterCreation() {
        bankingSystem.createAccount("User1", 1000.0);
        bankingSystem.createAccount("User2", 2000.0);
        assertEquals(2, bankingSystem.getTotalAccounts());
    }

    @Test
    void testTotalAccountsAfterFailedCreation() {
        bankingSystem.createAccount("User1", 1000.0);
        bankingSystem.createAccount(null, 2000.0); // Should fail
        assertEquals(1, bankingSystem.getTotalAccounts());
    }

    @Test
    void testTotalAccountsAfterClosure() {
        BankAccount account1 = bankingSystem.createAccount("User1", 0.0);
        BankAccount account2 = bankingSystem.createAccount("User2", 1000.0);
        
        bankingSystem.closeAccount(account1.getAccountNumber());
        assertEquals(1, bankingSystem.getTotalAccounts());
    }

    // Advanced Banking System Tests (10 test cases)
    @Test
    void testCreateLoan() {
        BankAccount account = bankingSystem.createAccount("Borrower", 5000.0);
        LoanAccount loan = bankingSystem.createLoan(account.getAccountNumber(), 10000.0, 0.05, 12);
        
        assertNotNull(loan);
        assertEquals(10000.0, loan.getRemainingBalance(), 0.01);
        assertEquals(15000.0, account.getBalance(), 0.01); // Original + loan amount
    }
    
    @Test
    void testMakeLoanPayment() {
        BankAccount account = bankingSystem.createAccount("Borrower", 5000.0);
        LoanAccount loan = bankingSystem.createLoan(account.getAccountNumber(), 10000.0, 0.05, 12);
        
        assertTrue(bankingSystem.makeLoanPayment(loan.getLoanId(), account.getAccountNumber(), 1000.0));
        assertTrue(loan.getRemainingBalance() < 10000.0);
    }
    
    @Test
    void testGetAccountsByType() {
        bankingSystem.createAccount("User1", 1000.0, AccountType.SAVINGS);
        bankingSystem.createAccount("User2", 2000.0, AccountType.PREMIUM);
        bankingSystem.createAccount("User3", 1500.0, AccountType.SAVINGS);
        
        var savingsAccounts = bankingSystem.getAccountsByType(AccountType.SAVINGS);
        assertEquals(2, savingsAccounts.size());
    }
    
    @Test
    void testGetHighValueAccounts() {
        bankingSystem.createAccount("Rich1", 50000.0, AccountType.PREMIUM);
        bankingSystem.createAccount("Poor1", 1000.0);
        bankingSystem.createAccount("Rich2", 75000.0, AccountType.BUSINESS);
        
        var highValueAccounts = bankingSystem.getHighValueAccounts(25000.0);
        assertEquals(2, highValueAccounts.size());
    }
    
    @Test
    void testGetTotalSystemBalance() {
        bankingSystem.createAccount("User1", 1000.0);
        bankingSystem.createAccount("User2", 2000.0);
        bankingSystem.createAccount("User3", 3000.0);
        
        assertEquals(6000.0, bankingSystem.getTotalSystemBalance(), 0.01);
    }
    
    @Test
    void testCreateAccountWithSpecificType() {
        BankAccount premiumAccount = bankingSystem.createAccount("Premium User", 10000.0, AccountType.PREMIUM);
        assertNotNull(premiumAccount);
        assertEquals(AccountType.PREMIUM, premiumAccount.getAccountType());
    }
    
    @Test
    void testCreateAccountBelowMinimumBalance() {
        BankAccount account = bankingSystem.createAccount("Poor User", 100.0, AccountType.PREMIUM);
        assertNull(account); // Should fail due to minimum balance requirement
    }
    
    @Test
    void testGetAllLoans() {
        BankAccount account1 = bankingSystem.createAccount("Borrower1", 5000.0);
        BankAccount account2 = bankingSystem.createAccount("Borrower2", 6000.0);
        
        bankingSystem.createLoan(account1.getAccountNumber(), 10000.0, 0.05, 12);
        bankingSystem.createLoan(account2.getAccountNumber(), 15000.0, 0.06, 24);
        
        assertEquals(2, bankingSystem.getAllLoans().size());
    }
    
    @Test
    void testTotalAccountsLargeNumber() {
        for (int i = 0; i < 50; i++) {
            bankingSystem.createAccount("User" + i, 1000.0);
        }
        assertEquals(50, bankingSystem.getTotalAccounts());
    }
}