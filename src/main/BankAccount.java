package main;

public class BankAccount {

    private final String name;
    private double balance;

    public BankAccount() {
        this("Default Checking");
    }

    public BankAccount(String name) {
        this.name = name;
        this.balance = 0;
    }

    public String getName() {
        return name;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        } else {
            throw new IllegalArgumentException();
        }
    }
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException();
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance -= amount;
    }

    

    public double getBalance() {
        return this.balance;
    }

    public void collectFee(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException();
        }
        if (this.balance < amount) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.balance -= amount;
    }

    /**
     * Credits simple interest on the current principal (balance) for a period.
     * Interest = principal × (annualRatePercent / 100) × (monthsInPeriod / 12).
     *
     * @param annualRatePercent nominal annual rate, e.g. 3.0 for 3% per year
     * @param monthsInPeriod    length of the accrual period in months
     * @return interest amount credited (0 if principal is not positive)
     */
    public double applyInterestPayment(double annualRatePercent, int monthsInPeriod) {
        if (annualRatePercent < 0) {
            throw new IllegalArgumentException();
        }
        if (monthsInPeriod <= 0) {
            throw new IllegalArgumentException();
        }
        if (this.balance <= 0) {
            return 0;
        }
        double principal = this.balance;
        double interest = principal * (annualRatePercent / 100.0) * (monthsInPeriod / 12.0);
        this.balance += interest;
        return interest;
    }
}