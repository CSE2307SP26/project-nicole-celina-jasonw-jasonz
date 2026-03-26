package main;

public class BankAccount {

    private double balance;
    private String transactionHistory;

    public BankAccount() {
        this.balance = 0;
        this.transactionHistory = "";
    }

    public void deposit(double amount) {
        if(amount > 0) {
            this.balance += amount;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public void recordTransaction(String transactionType, double amount) {
        this.transactionHistory += transactionType + ": " + amount + "\n";
    }

    public String getTransactionHistory() {
        return this.transactionHistory;
    }
}
