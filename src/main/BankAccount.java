package main;

public class BankAccount {

    private double balance;
    private String accountName;

    public BankAccount() {
        this.balance = 0;
        this.accountName = "";
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

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }
}
