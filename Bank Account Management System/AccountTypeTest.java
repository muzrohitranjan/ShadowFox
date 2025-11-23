import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTypeTest {

    @Test
    void testSavingsAccountProperties() {
        assertEquals(0.025, AccountType.SAVINGS.getInterestRate(), 0.001);
        assertEquals(1000.0, AccountType.SAVINGS.getMinimumBalance(), 0.01);
        assertEquals(0.0, AccountType.SAVINGS.getOverdraftLimit(), 0.01);
    }

    @Test
    void testCheckingAccountProperties() {
        assertEquals(0.001, AccountType.CHECKING.getInterestRate(), 0.001);
        assertEquals(500.0, AccountType.CHECKING.getMinimumBalance(), 0.01);
        assertEquals(500.0, AccountType.CHECKING.getOverdraftLimit(), 0.01);
    }

    @Test
    void testPremiumAccountProperties() {
        assertEquals(0.035, AccountType.PREMIUM.getInterestRate(), 0.001);
        assertEquals(5000.0, AccountType.PREMIUM.getMinimumBalance(), 0.01);
        assertEquals(2000.0, AccountType.PREMIUM.getOverdraftLimit(), 0.01);
    }

    @Test
    void testBusinessAccountProperties() {
        assertEquals(0.015, AccountType.BUSINESS.getInterestRate(), 0.001);
        assertEquals(10000.0, AccountType.BUSINESS.getMinimumBalance(), 0.01);
        assertEquals(5000.0, AccountType.BUSINESS.getOverdraftLimit(), 0.01);
    }

    @Test
    void testAccountTypeComparison() {
        assertTrue(AccountType.PREMIUM.getInterestRate() > AccountType.CHECKING.getInterestRate());
        assertTrue(AccountType.BUSINESS.getOverdraftLimit() > AccountType.SAVINGS.getOverdraftLimit());
    }
}