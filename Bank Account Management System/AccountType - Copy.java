public enum AccountType {
    SAVINGS(0.025, 1000.0, 0.0),
    CHECKING(0.001, 500.0, 500.0),
    PREMIUM(0.035, 5000.0, 2000.0),
    BUSINESS(0.015, 10000.0, 5000.0);

    private final double interestRate;
    private final double minimumBalance;
    private final double overdraftLimit;

    AccountType(double interestRate, double minimumBalance, double overdraftLimit) {
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
        this.overdraftLimit = overdraftLimit;
    }

    public double getInterestRate() { return interestRate; }
    public double getMinimumBalance() { return minimumBalance; }
    public double getOverdraftLimit() { return overdraftLimit; }
}