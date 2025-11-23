import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankAccountTest {
    private BankAccount account;

    @BeforeEach
    void setUp() {
        account = new BankAccount("12345", "John Doe", 1000.0, AccountType.CHECKING);
    }

    // Deposit Tests (5 test cases)
    @Test
    void testDepositValidAmount() {
        assertTrue(account.deposit(500.0));
        assertEquals(1500.0, account.getBalance(), 0.01);
    }

    @Test
    void testDepositZeroAmount() {
        assertFalse(account.deposit(0.0));
        assertEquals(1000.0, account.getBalance(), 0.01);
    }

    @Test
    void testDepositNegativeAmount() {
        assertFalse(account.deposit(-100.0));
        assertEquals(1000.0, account.getBalance(), 0.01);
    }

    @Test
    void testDepositLargeAmount() {
        assertTrue(account.deposit(1000000.0));
        assertEquals(1001000.0, account.getBalance(), 0.01);
    }

    @Test
    void testDepositSmallAmount() {
        assertTrue(account.deposit(0.01));
        assertEquals(1000.01, account.getBalance(), 0.01);
    }

    // Withdrawal Tests (5 test cases)
    @Test
    void testWithdrawValidAmount() {
        assertTrue(account.withdraw(300.0));
        assertEquals(700.0, account.getBalance(), 0.01);
    }

    @Test
    void testWithdrawExactBalance() {
        assertTrue(account.withdraw(1000.0));
        assertEquals(0.0, account.getBalance(), 0.01);
    }

    @Test
    void testWithdrawWithOverdraft() {
        assertTrue(account.withdraw(1200.0)); // Uses overdraft
        assertEquals(-200.0, account.getBalance(), 0.01);
    }

    @Test
    void testWithdrawExceedsOverdraftLimit() {
        assertFalse(account.withdraw(1600.0)); // Exceeds balance + overdraft
        assertEquals(1000.0, account.getBalance(), 0.01);
    }

    @Test
    void testWithdrawNegativeAmount() {
        assertFalse(account.withdraw(-50.0));
        assertEquals(1000.0, account.getBalance(), 0.01);
    }

    // Balance Inquiry Tests (5 test cases)
    @Test
    void testGetInitialBalance() {
        assertEquals(1000.0, account.getBalance(), 0.01);
    }

    @Test
    void testGetBalanceAfterDeposit() {
        account.deposit(250.0);
        assertEquals(1250.0, account.getBalance(), 0.01);
    }

    @Test
    void testGetBalanceAfterWithdrawal() {
        account.withdraw(400.0);
        assertEquals(600.0, account.getBalance(), 0.01);
    }

    @Test
    void testGetBalanceAfterMultipleTransactions() {
        account.deposit(500.0);
        account.withdraw(200.0);
        account.deposit(100.0);
        assertEquals(1400.0, account.getBalance(), 0.01);
    }

    @Test
    void testGetBalanceWithNegativeBalance() {
        account.withdraw(1200.0);
        assertEquals(-200.0, account.getBalance(), 0.01);
    }

    // Transaction History Tests (5 test cases)
    @Test
    void testInitialTransactionHistory() {
        assertEquals(1, account.getTransactionHistory().size());
        assertEquals("INITIAL", account.getTransactionHistory().get(0).getType());
    }

    @Test
    void testTransactionHistoryAfterDeposit() {
        account.deposit(500.0);
        assertEquals(2, account.getTransactionHistory().size());
        assertEquals("DEPOSIT", account.getTransactionHistory().get(1).getType());
        assertEquals(500.0, account.getTransactionHistory().get(1).getAmount(), 0.01);
    }

    @Test
    void testTransactionHistoryAfterWithdrawal() {
        account.withdraw(300.0);
        assertEquals(2, account.getTransactionHistory().size());
        assertEquals("WITHDRAWAL", account.getTransactionHistory().get(1).getType());
        assertEquals(300.0, account.getTransactionHistory().get(1).getAmount(), 0.01);
    }

    @Test
    void testTransactionHistoryMultipleOperations() {
        account.deposit(200.0);
        account.withdraw(150.0);
        account.deposit(100.0);
        assertEquals(4, account.getTransactionHistory().size());
    }

    @Test
    void testTransactionHistoryImmutable() {
        var history = account.getTransactionHistory();
        int originalSize = history.size();
        history.clear(); // Should not affect original
        assertEquals(originalSize, account.getTransactionHistory().size());
    }

    // Interest Calculation Tests (5 test cases)
    @Test
    void testCalculateInterestPositiveBalance() {
        double initialBalance = account.getBalance();
        account.calculateInterest();
        double expectedInterest = initialBalance * (0.02 / 12);
        assertEquals(initialBalance + expectedInterest, account.getBalance(), 0.01);
    }

    @Test
    void testCalculateInterestZeroBalance() {
        account.withdraw(1000.0); // Balance becomes 0
        account.calculateInterest();
        assertEquals(0.0, account.getBalance(), 0.01);
    }

    @Test
    void testCalculateInterestNegativeBalance() {
        account.withdraw(1200.0); // Balance becomes -200
        double balanceBeforeInterest = account.getBalance();
        account.calculateInterest();
        assertEquals(balanceBeforeInterest, account.getBalance(), 0.01); // No interest on negative
    }

    @Test
    void testCalculateInterestAddsTransaction() {
        int transactionsBefore = account.getTransactionHistory().size();
        account.calculateInterest();
        assertEquals(transactionsBefore + 1, account.getTransactionHistory().size());
        assertEquals("INTEREST", account.getTransactionHistory().get(transactionsBefore).getType());
    }

    @Test
    void testCalculateInterestWithCustomRate() {
        account.setInterestRate(0.05); // 5% annual
        double initialBalance = account.getBalance();
        account.calculateInterest();
        double expectedInterest = initialBalance * (0.05 / 12);
        assertEquals(initialBalance + expectedInterest, account.getBalance(), 0.01);
    }

    // Transfer Tests (5 test cases)
    @Test
    void testTransferValidAmount() {
        BankAccount targetAccount = new BankAccount("67890", "Jane Smith", 500.0, AccountType.CHECKING);
        assertTrue(account.transfer(targetAccount, 300.0));
        assertEquals(694.0, account.getBalance(), 0.01); // 700 - 3 (transfer fee) - 3 (fee transaction)
        assertEquals(800.0, targetAccount.getBalance(), 0.01);
    }

    // Account Type and Advanced Features Tests (10 test cases)
    @Test
    void testAccountTypeProperties() {
        assertEquals(AccountType.CHECKING, account.getAccountType());
        assertTrue(account.isActive());
    }
    
    @Test
    void testPremiumAccountNoTransferFee() {
        BankAccount premiumAccount = new BankAccount("99999", "Premium User", 10000.0, AccountType.PREMIUM);
        BankAccount targetAccount = new BankAccount("88888", "Target User", 1000.0, AccountType.CHECKING);
        
        premiumAccount.transfer(targetAccount, 500.0);
        assertEquals(9500.0, premiumAccount.getBalance(), 0.01); // No transfer fee
    }
    
    @Test
    void testAccountFreeze() {
        assertTrue(account.freezeAccount());
        assertFalse(account.isActive());
        assertFalse(account.deposit(100.0)); // Should fail when frozen
    }
    
    @Test
    void testAccountUnfreeze() {
        account.freezeAccount();
        assertTrue(account.unfreezeAccount());
        assertTrue(account.isActive());
        assertTrue(account.deposit(100.0)); // Should work after unfreeze
    }
    
    @Test
    void testOverdraftFee() {
        account.withdraw(1200.0); // Triggers overdraft
        assertTrue(account.getBalance() < -200.0); // Balance should be less due to overdraft fee
        assertTrue(account.getTotalFees() > 0);
    }
    
    @Test
    void testMinimumBalanceFee() {
        account.withdraw(600.0); // Balance becomes 400, below minimum for checking
        account.calculateInterest(); // This should trigger minimum balance fee
        assertTrue(account.getTotalFees() > 0);
    }
    
    @Test
    void testDailyTransactionLimits() {
        // Make multiple small deposits to test daily limits
        for (int i = 0; i < 15; i++) {
            account.deposit(100.0);
        }
        // Should eventually fail due to daily transaction limit
        assertTrue(account.getDailyTransactionCount() <= 10);
    }
    
    @Test
    void testAccountAge() {
        assertTrue(account.getAccountAge() >= 0);
    }
    
    @Test
    void testTransferToNullAccount() {
        assertFalse(account.transfer(null, 300.0));
        assertEquals(1000.0, account.getBalance(), 0.01);
    }

    @Test
    void testTransferNegativeAmount() {
        BankAccount targetAccount = new BankAccount("67890", "Jane Smith", 500.0, AccountType.CHECKING);
        assertFalse(account.transfer(targetAccount, -100.0));
        assertEquals(1000.0, account.getBalance(), 0.01);
        assertEquals(500.0, targetAccount.getBalance(), 0.01);
    }

    @Test
    void testTransferExceedsBalance() {
        BankAccount targetAccount = new BankAccount("67890", "Jane Smith", 500.0, AccountType.CHECKING);
        assertFalse(account.transfer(targetAccount, 1600.0)); // Exceeds balance + overdraft
        assertEquals(1000.0, account.getBalance(), 0.01);
        assertEquals(500.0, targetAccount.getBalance(), 0.01);
    }

    @Test
    void testTransferWithOverdraft() {
        BankAccount targetAccount = new BankAccount("67890", "Jane Smith", 500.0, AccountType.CHECKING);
        assertTrue(account.transfer(targetAccount, 1200.0)); // Uses overdraft
        assertEquals(-203.0, account.getBalance(), 0.01); // -200 - 3 (transfer fee)
        assertEquals(1700.0, targetAccount.getBalance(), 0.01);
    }
}