package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import main.BankAccount;

public class BankAccountTest {

    @Test
    public void testDeposit() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(50);
        assertEquals(50, testAccount.getBalance(), 0.01);
    }
    @Test
    public void testInvalidDeposit() {
        BankAccount testAccount = new BankAccount();
        try {
            testAccount.deposit(-50);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }
    @Test
    public void testWithdrawal() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(50);
        testAccount.withdraw(10);
        assertEquals(40, testAccount.getBalance(), 0.01);
    }
    @Test
    public void testInvalidWithdrawal() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(50);
        try {
            testAccount.withdraw(-20);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }
    @Test
    public void testEmptyWithdrawal() {
        BankAccount testAccount = new BankAccount();
        try {
            testAccount.withdraw(20);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }
    @Test
    public void testWithdrawalTooMuch() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(50);
        try {
            testAccount.withdraw(60);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }

}
