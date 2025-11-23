import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanAccount {
    private String loanId;
    private String accountNumber;
    private double principal;
    private double interestRate;
    private int termMonths;
    private double monthlyPayment;
    private double remainingBalance;
    private LocalDate startDate;
    private List<LoanPayment> payments;
    private boolean isActive;

    public LoanAccount(String loanId, String accountNumber, double principal, double interestRate, int termMonths) {
        this.loanId = loanId;
        this.accountNumber = accountNumber;
        this.principal = principal;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.remainingBalance = principal;
        this.startDate = LocalDate.now();
        this.payments = new ArrayList<>();
        this.isActive = true;
        calculateMonthlyPayment();
    }

    private void calculateMonthlyPayment() {
        double monthlyRate = interestRate / 12;
        this.monthlyPayment = principal * (monthlyRate * Math.pow(1 + monthlyRate, termMonths)) / 
                             (Math.pow(1 + monthlyRate, termMonths) - 1);
    }

    public boolean makePayment(double amount) {
        if (!isActive || amount <= 0) return false;
        
        double interestPortion = remainingBalance * (interestRate / 12);
        double principalPortion = Math.min(amount - interestPortion, remainingBalance);
        
        if (principalPortion < 0) principalPortion = 0;
        
        remainingBalance -= principalPortion;
        payments.add(new LoanPayment(amount, principalPortion, interestPortion, remainingBalance));
        
        if (remainingBalance <= 0.01) {
            isActive = false;
            remainingBalance = 0;
        }
        
        return true;
    }

    public double getNextPaymentAmount() { return monthlyPayment; }
    public double getRemainingBalance() { return remainingBalance; }
    public boolean isActive() { return isActive; }
    public String getLoanId() { return loanId; }
    public List<LoanPayment> getPayments() { return new ArrayList<>(payments); }
    
    public static class LoanPayment {
        private final double totalAmount;
        private final double principalAmount;
        private final double interestAmount;
        private final double remainingBalance;
        private final LocalDate paymentDate;

        public LoanPayment(double totalAmount, double principalAmount, double interestAmount, double remainingBalance) {
            this.totalAmount = totalAmount;
            this.principalAmount = principalAmount;
            this.interestAmount = interestAmount;
            this.remainingBalance = remainingBalance;
            this.paymentDate = LocalDate.now();
        }

        public double getTotalAmount() { return totalAmount; }
        public double getPrincipalAmount() { return principalAmount; }
        public double getInterestAmount() { return interestAmount; }
        public double getRemainingBalance() { return remainingBalance; }
        public LocalDate getPaymentDate() { return paymentDate; }
    }
}