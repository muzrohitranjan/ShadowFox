import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoanAccountTest {
    private LoanAccount loan;

    @BeforeEach
    void setUp() {
        loan = new LoanAccount("L5000", "1000", 10000.0, 0.05, 12);
    }

    // Loan Creation Tests (5 test cases)
    @Test
    void testLoanCreation() {
        assertNotNull(loan);
        assertEquals("L5000", loan.getLoanId());
        assertEquals(10000.0, loan.getRemainingBalance(), 0.01);
        assertTrue(loan.isActive());
    }

    @Test
    void testMonthlyPaymentCalculation() {
        double expectedPayment = 856.07; // Calculated for 10k at 5% for 12 months
        assertEquals(expectedPayment, loan.getNextPaymentAmount(), 1.0);
    }

    @Test
    void testLoanWithZeroPrincipal() {
        LoanAccount zeroLoan = new LoanAccount("L5001", "1001", 0.0, 0.05, 12);
        assertEquals(0.0, zeroLoan.getRemainingBalance(), 0.01);
        assertEquals(0.0, zeroLoan.getNextPaymentAmount(), 0.01);
    }

    @Test
    void testLoanWithHighInterestRate() {
        LoanAccount highRateLoan = new LoanAccount("L5002", "1002", 5000.0, 0.15, 24);
        assertTrue(highRateLoan.getNextPaymentAmount() > 200.0);
    }

    @Test
    void testLoanWithLongTerm() {
        LoanAccount longTermLoan = new LoanAccount("L5003", "1003", 20000.0, 0.04, 60);
        assertTrue(longTermLoan.getNextPaymentAmount() < 400.0);
    }

    // Payment Tests (5 test cases)
    @Test
    void testValidPayment() {
        double paymentAmount = loan.getNextPaymentAmount();
        assertTrue(loan.makePayment(paymentAmount));
        assertTrue(loan.getRemainingBalance() < 10000.0);
        assertEquals(1, loan.getPayments().size());
    }

    @Test
    void testZeroPayment() {
        assertFalse(loan.makePayment(0.0));
        assertEquals(10000.0, loan.getRemainingBalance(), 0.01);
    }

    @Test
    void testNegativePayment() {
        assertFalse(loan.makePayment(-100.0));
        assertEquals(10000.0, loan.getRemainingBalance(), 0.01);
    }

    @Test
    void testOverpayment() {
        assertTrue(loan.makePayment(15000.0)); // Pay more than balance
        assertEquals(0.0, loan.getRemainingBalance(), 0.01);
        assertFalse(loan.isActive());
    }

    @Test
    void testPartialPayment() {
        assertTrue(loan.makePayment(100.0)); // Less than monthly payment
        assertTrue(loan.getRemainingBalance() > 9900.0); // Most goes to interest
    }

    // Loan Status Tests (5 test cases)
    @Test
    void testActiveLoanStatus() {
        assertTrue(loan.isActive());
        loan.makePayment(loan.getNextPaymentAmount());
        assertTrue(loan.isActive()); // Still active after one payment
    }

    @Test
    void testLoanPayoffStatus() {
        loan.makePayment(15000.0); // Pay off completely
        assertFalse(loan.isActive());
        assertEquals(0.0, loan.getRemainingBalance(), 0.01);
    }

    @Test
    void testPaymentOnInactiveLoan() {
        loan.makePayment(15000.0); // Pay off loan
        assertFalse(loan.makePayment(100.0)); // Should fail
    }

    @Test
    void testMultiplePayments() {
        for (int i = 0; i < 5; i++) {
            loan.makePayment(loan.getNextPaymentAmount());
        }
        assertEquals(5, loan.getPayments().size());
        assertTrue(loan.getRemainingBalance() < 10000.0);
    }

    @Test
    void testLoanPaymentHistory() {
        loan.makePayment(1000.0);
        var payments = loan.getPayments();
        assertEquals(1, payments.size());
        
        var payment = payments.get(0);
        assertEquals(1000.0, payment.getTotalAmount(), 0.01);
        assertTrue(payment.getInterestAmount() > 0);
        assertTrue(payment.getPrincipalAmount() > 0);
    }

    // Payment Calculation Tests (5 test cases)
    @Test
    void testInterestCalculation() {
        loan.makePayment(1000.0);
        var payment = loan.getPayments().get(0);
        
        double expectedInterest = 10000.0 * (0.05 / 12); // Monthly interest
        assertEquals(expectedInterest, payment.getInterestAmount(), 1.0);
    }

    @Test
    void testPrincipalCalculation() {
        loan.makePayment(1000.0);
        var payment = loan.getPayments().get(0);
        
        double monthlyInterest = 10000.0 * (0.05 / 12);
        double expectedPrincipal = 1000.0 - monthlyInterest;
        assertEquals(expectedPrincipal, payment.getPrincipalAmount(), 1.0);
    }

    @Test
    void testRemainingBalanceAfterPayment() {
        double initialBalance = loan.getRemainingBalance();
        loan.makePayment(1000.0);
        var payment = loan.getPayments().get(0);
        
        double expectedRemaining = initialBalance - payment.getPrincipalAmount();
        assertEquals(expectedRemaining, payment.getRemainingBalance(), 0.01);
    }

    @Test
    void testPaymentDateRecording() {
        loan.makePayment(1000.0);
        var payment = loan.getPayments().get(0);
        assertNotNull(payment.getPaymentDate());
    }

    @Test
    void testPaymentHistoryImmutability() {
        loan.makePayment(1000.0);
        var payments = loan.getPayments();
        int originalSize = payments.size();
        payments.clear(); // Should not affect original
        assertEquals(originalSize, loan.getPayments().size());
    }
}