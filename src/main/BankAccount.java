package main;
//
public class BankAccount {

    private double balance;

    public BankAccount() {
        this.balance = 0;
    }

    public void deposit(double amount) {
        if(amount > 0) {
            this.balance += amount;
        } else {
            throw new IllegalArgumentException();
        }
    }
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException();
        }
        if (amount > this.balance){
            throw new IllegalArgumentException();
        }
        this.balance -= amount;
    }

    public void balanceCheck() {
        double balance = this.getBalance();
        System.out.println("Your current balance is: " + balance);
    }

    public double getBalance() {
        return this.balance;
    }
}