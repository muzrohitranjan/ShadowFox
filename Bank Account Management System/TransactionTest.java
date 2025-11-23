import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class TransactionTest {
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transaction("DEPOSIT", 500.0, 1500.0, "Cash deposit");
    }

    // Transaction Creation Tests (5 test cases)
    @Test
    void testTransactionCreation() {
        assertNotNull(transaction);
        assertEquals("DEPOSIT", transaction.getType());
        assertEquals(500.0, transaction.getAmount(), 0.01);
        assertEquals(1500.0, transaction.getBalanceAfter(), 0.01);
        assertEquals("Cash deposit", transaction.getDescription());
    }

    @Test
    void testTransactionTimestamp() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        Transaction newTransaction = new Transaction("WITHDRAWAL", 100.0, 900.0, "ATM withdrawal");
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        
        assertTrue(newTransaction.getTimestamp().isAfter(before));
        assertTrue(newTransaction.getTimestamp().isBefore(after));
    }

    @Test
    void testTransactionWithZeroAmount() {
        Transaction zeroTransaction = new Transaction("INTEREST", 0.0, 1000.0, "No interest");
        assertEquals(0.0, zeroTransaction.getAmount(), 0.01);
    }

    @Test
    void testTransactionWithNegativeAmount() {
        Transaction negativeTransaction = new Transaction("WITHDRAWAL", -200.0, 800.0, "Reversal");
        assertEquals(-200.0, negativeTransaction.getAmount(), 0.01);
    }

    @Test
    void testTransactionWithLargeAmount() {
        Transaction largeTransaction = new Transaction("DEPOSIT", 1000000.0, 1001000.0, "Large deposit");
        assertEquals(1000000.0, largeTransaction.getAmount(), 0.01);
    }

    // Transaction Type Tests (5 test cases)
    @Test
    void testDepositTransaction() {
        Transaction deposit = new Transaction("DEPOSIT", 300.0, 1300.0, "Branch deposit");
        assertEquals("DEPOSIT", deposit.getType());
    }

    @Test
    void testWithdrawalTransaction() {
        Transaction withdrawal = new Transaction("WITHDRAWAL", 200.0, 800.0, "ATM withdrawal");
        assertEquals("WITHDRAWAL", withdrawal.getType());
    }

    @Test
    void testTransferTransaction() {
        Transaction transfer = new Transaction("TRANSFER_OUT", 150.0, 850.0, "Transfer to account 12345");
        assertEquals("TRANSFER_OUT", transfer.getType());
    }

    @Test
    void testInterestTransaction() {
        Transaction interest = new Transaction("INTEREST", 16.67, 1016.67, "Monthly interest");
        assertEquals("INTEREST", interest.getType());
    }

    @Test
    void testInitialTransaction() {
        Transaction initial = new Transaction("INITIAL", 1000.0, 1000.0, "Account opened");
        assertEquals("INITIAL", initial.getType());
    }

    // Transaction String Representation Tests (5 test cases)
    @Test
    void testToStringFormat() {
        String transactionString = transaction.toString();
        assertTrue(transactionString.contains("DEPOSIT"));
        assertTrue(transactionString.contains("500.00"));
        assertTrue(transactionString.contains("1500.00"));
        assertTrue(transactionString.contains("Cash deposit"));
    }

    @Test
    void testToStringWithDifferentType() {
        Transaction withdrawal = new Transaction("WITHDRAWAL", 100.0, 900.0, "ATM withdrawal");
        String withdrawalString = withdrawal.toString();
        assertTrue(withdrawalString.contains("WITHDRAWAL"));
        assertTrue(withdrawalString.contains("100.00"));
    }

    @Test
    void testToStringWithZeroAmount() {
        Transaction zeroTransaction = new Transaction("INTEREST", 0.0, 1000.0, "No interest earned");
        String zeroString = zeroTransaction.toString();
        assertTrue(zeroString.contains("0.00"));
    }

    @Test
    void testToStringWithNegativeBalance() {
        Transaction overdraft = new Transaction("WITHDRAWAL", 1200.0, -200.0, "Overdraft withdrawal");
        String overdraftString = overdraft.toString();
        assertTrue(overdraftString.contains("-200.00"));
    }

    @Test
    void testToStringContainsTimestamp() {
        String transactionString = transaction.toString();
        // Should contain year, month, day pattern
        assertTrue(transactionString.matches(".*\\d{4}-\\d{2}-\\d{2}.*"));
    }

    // Transaction Immutability Tests (5 test cases)
    @Test
    void testTransactionTypeImmutable() {
        String originalType = transaction.getType();
        assertEquals("DEPOSIT", originalType);
        // Type should remain unchanged (no setter available)
        assertEquals("DEPOSIT", transaction.getType());
    }

    @Test
    void testTransactionAmountImmutable() {
        double originalAmount = transaction.getAmount();
        assertEquals(500.0, originalAmount, 0.01);
        // Amount should remain unchanged (no setter available)
        assertEquals(500.0, transaction.getAmount(), 0.01);
    }

    @Test
    void testTransactionBalanceImmutable() {
        double originalBalance = transaction.getBalanceAfter();
        assertEquals(1500.0, originalBalance, 0.01);
        // Balance should remain unchanged (no setter available)
        assertEquals(1500.0, transaction.getBalanceAfter(), 0.01);
    }

    @Test
    void testTransactionDescriptionImmutable() {
        String originalDescription = transaction.getDescription();
        assertEquals("Cash deposit", originalDescription);
        // Description should remain unchanged (no setter available)
        assertEquals("Cash deposit", transaction.getDescription());
    }

    @Test
    void testTransactionTimestampImmutable() {
        LocalDateTime originalTimestamp = transaction.getTimestamp();
        assertNotNull(originalTimestamp);
        // Timestamp should remain unchanged (no setter available)
        assertEquals(originalTimestamp, transaction.getTimestamp());
    }
}