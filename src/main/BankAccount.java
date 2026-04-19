package main;


import java.util.List;
import java.util.ArrayList;

public class BankAccount {
    public static final double LOAN_FIXED_INTEREST_RATE = 0.05;

    private String accountName;
    private String accountPassword;
    private boolean isLoggedIn;
    private double balance;
    private boolean frozen;
    private List<String> transactionHistory;
    private boolean hasDebitCard;
    private String debitCardFirstName;
    private String debitCardLastName;
    private String debitCardNumber;
    private boolean hasActiveLoan;
    private double activeLoanPrincipal;
    private double activeLoanRepaymentAmount;
    private int activeLoanDueDay;
    private static final double LOAN_LATE_PENALTY_RATE = 0.10;
    private boolean loanLatePenaltyApplied;

    public BankAccount() {
        this("defaultaccount", "defaultpassword");
    }

    public BankAccount(String accountName, String accountPassword) {
        this.accountName = accountName;
        this.accountPassword = accountPassword;
        this.isLoggedIn = false;
        this.balance = 0;
        this.frozen = false;
        this.transactionHistory = new ArrayList<>();
        this.hasDebitCard = false;
        this.debitCardFirstName = "";
        this.debitCardLastName = "";
        this.debitCardNumber = "";
        this.hasActiveLoan = false;
        this.activeLoanPrincipal = 0;
        this.activeLoanRepaymentAmount = 0;
        this.activeLoanDueDay = 0;
        this.loanLatePenaltyApplied = false;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public void deposit(double amount) {
        if (this.frozen) {
            throw new IllegalStateException("Account is frozen");
        }
        if (amount > 0) {
            this.balance += amount;
        } else {
            throw new IllegalArgumentException();
        }
    }
    public void withdraw(double amount) {
        if (this.frozen) {
            throw new IllegalStateException("Account is frozen");
        }
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

    public void recordTransaction(String transactionType, double amount) {
        transactionHistory.add(transactionType + ": " + amount);
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    public boolean hasDebitCard() {
        return hasDebitCard;
    }

    public String getDebitCardFirstName() {
        return debitCardFirstName;
    }

    public String getDebitCardLastName() {
        return debitCardLastName;
    }

    public String getDebitCardNumber() {
        return debitCardNumber;
    }

    public String generateDebitCardNumber() {
        String cardNumber = "";
        int firstDigit = (int)(Math.random() * 9) + 1;
        cardNumber += firstDigit;
        for(int i = 0; i < 15; i++) {
            int cardDigit = (int)(Math.random() * 10);
            cardNumber += cardDigit;
        }
        return cardNumber;
    }

    public void createDebitCard(String firstName, String lastName) {
        if (hasDebitCard) {
            return;
        }
        this.debitCardFirstName = firstName;
        this.debitCardLastName = lastName;
        this.debitCardNumber = generateDebitCardNumber();
        this.hasDebitCard = true;
    }

    public boolean hasActiveLoan() {
        return hasActiveLoan;
    }

    public double getActiveLoanRepaymentAmount() {
        return activeLoanRepaymentAmount;
    }

    public boolean isLoanLatePenaltyApplied() {
        return loanLatePenaltyApplied;
    }

    public boolean hasOverdueBalance() {
        return hasActiveLoan && loanLatePenaltyApplied;
    }

    public int getActiveLoanDueDay() {
        return activeLoanDueDay;
    }

    public void applyLoan(double amount, int repaymentDays, int currentDay) {
        if (amount <= 0 || repaymentDays <= 0) {
            throw new IllegalArgumentException("Loan amount and repayment days must be positive.");
        }
        if (hasActiveLoan) {
            throw new IllegalStateException("You already have an active loan.");
        }
        if (isFrozen()) {
            throw new IllegalStateException("Account is frozen");
        }
        this.activeLoanPrincipal = amount;
        this.activeLoanRepaymentAmount = amount * (1.0 + LOAN_FIXED_INTEREST_RATE);
        this.activeLoanDueDay = currentDay + repaymentDays;
        this.hasActiveLoan = true;
        this.loanLatePenaltyApplied = false;
        this.balance += amount;
        recordTransaction("Loan Disbursed", amount);
    }

    public void makeLoanRepayment(double amount, int currentDay) {
        if (!hasActiveLoan) {
            throw new IllegalStateException("You do not have an active loan.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Repayment amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        double payment = Math.min(amount, activeLoanRepaymentAmount);

        balance -= payment;
        activeLoanRepaymentAmount -= payment;
        recordTransaction("Loan Repayment", -payment);

        if (activeLoanRepaymentAmount <= 0.000001) {
            clearActiveLoan();
            recordTransaction("Loan Fully Repaid", 0);
        }
    }

    public boolean processLoanIfDue(int currentDay) {
        if (!hasActiveLoan || currentDay < activeLoanDueDay) {
            return false;
        }
        if (activeLoanRepaymentAmount <= 0.000001) {
            clearActiveLoan();
            recordTransaction("Loan Fully Repaid", 0);
            return true;
        }
        if (balance >= activeLoanRepaymentAmount) {
            balance -= activeLoanRepaymentAmount;
            recordTransaction("Loan Repaid", -activeLoanRepaymentAmount);
            clearActiveLoan();
        } else {
            applyLatePenaltyIfNeeded();
            setFrozen(true);
            recordTransaction("Loan Default - Account Frozen", 0);
        }
        return true;
    }

    private void applyLatePenaltyIfNeeded() {
        if (!loanLatePenaltyApplied) {
            double penalty = activeLoanRepaymentAmount * LOAN_LATE_PENALTY_RATE;
            activeLoanRepaymentAmount += penalty;
            loanLatePenaltyApplied = true;
            recordTransaction("Loan Late Penalty", -penalty);
        }
    }

    private void clearActiveLoan() {
        this.hasActiveLoan = false;
        this.activeLoanPrincipal = 0;
        this.activeLoanRepaymentAmount = 0;
        this.activeLoanDueDay = 0;
        this.loanLatePenaltyApplied = false;
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
        validateInterestPaymentInputs(annualRatePercent, monthsInPeriod);
        return applyInterestIfPositivePrincipal(annualRatePercent, monthsInPeriod);
    }

    private void validateInterestPaymentInputs(double annualRatePercent, int monthsInPeriod) {
        if (annualRatePercent < 0) {
            throw new IllegalArgumentException();
        }
        if (monthsInPeriod <= 0) {
            throw new IllegalArgumentException();
        }
    }

    private double applyInterestIfPositivePrincipal(double annualRatePercent, int monthsInPeriod) {
        if (this.balance <= 0) {
            return 0;
        }
        double principal = this.balance;
        double interest = principal * (annualRatePercent / 100.0) * (monthsInPeriod / 12.0);
        this.balance += interest;
        return interest;
    }
}