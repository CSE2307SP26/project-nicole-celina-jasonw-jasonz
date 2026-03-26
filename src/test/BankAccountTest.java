package test;

import main.BankAccount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

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
    public void testCollectFee() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(100);
        testAccount.collectFee(25);
        assertEquals(75, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testInvalidCollectFee() {
        BankAccount testAccount = new BankAccount();
        try {
            testAccount.collectFee(-25);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }

    @Test
    public void testCollectFeeInsufficientBalance() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(50);
        try {
            testAccount.collectFee(75);
            fail();
        } catch (IllegalStateException e) {
            assertEquals(50, testAccount.getBalance(), 0.01);
        }
    }

    @Test
    public void testApplyInterestPaymentOneMonth() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(1000);
        double credited = testAccount.applyInterestPayment(12.0, 1);
        assertEquals(10.0, credited, 0.01);
        assertEquals(1010.0, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testApplyInterestPaymentFullYear() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(100);
        double credited = testAccount.applyInterestPayment(6.0, 12);
        assertEquals(6.0, credited, 0.01);
        assertEquals(106.0, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testApplyInterestPaymentZeroPrincipal() {
        BankAccount testAccount = new BankAccount();
        double credited = testAccount.applyInterestPayment(5.0, 12);
        assertEquals(0, credited, 0.01);
        assertEquals(0, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testInvalidInterestRate() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(100);
        try {
            testAccount.applyInterestPayment(-1.0, 12);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(100, testAccount.getBalance(), 0.01);
        }
    }

    @Test
    public void testInvalidInterestPeriod() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(100);
        try {
            testAccount.applyInterestPayment(5.0, 0);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(100, testAccount.getBalance(), 0.01);
        }
    }
}
