package main;

import java.util.List;
import java.util.ArrayList;

public class BankAccount {

    private final String accountName;
    private String accountPassword;
    private boolean isLoggedIn;
    private double balance;
    private boolean frozen;
    private List<String> transactionHistory;
    private boolean hasDebitCard;
    private String debitCardFirstName;
    private String debitCardLastName;
    private String debitCardNumber;

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
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountPassword() {
        return accountPassword;
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

//debit card PR record

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