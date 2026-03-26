package test;

import main.BankAccount;
import main.MainMenu;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class BankAccountTest {

    // customer test: deposit
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

    // customer test: withdrawal
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


    // customer test: close account

    @Test
    public void testValidAccountClosing() {
        MainMenu menu = new MainMenu();
        BankAccount testAccount = new BankAccount("Test");
        menu.getAccounts().add(testAccount);
        menu.performCloseAccount(testAccount, true);
        assertEquals(false, menu.getAccounts().contains(testAccount));
    }

    // customer test: transfer money
    // Fake subclass so tests can control user input
    static class TestMainMenu extends MainMenu {
        double testAmount;
        int testTargetIndex;

        @Override
        public double promptNonNegativeAmount(String message) {
            return testAmount;
        }

        @Override
        public int promptAccountIndex(String message) {
            return testTargetIndex;
        }
    }

    @Test
    public void testTransferAmountZeroMakesNoChange() {
        TestMainMenu menu = new TestMainMenu();

        BankAccount source = new BankAccount("Source");
        BankAccount target = new BankAccount("Target");

        source.deposit(100);
        target.deposit(50);

        menu.getAccounts().clear();
        menu.getAccounts().add(source);
        menu.getAccounts().add(target);

        menu.testAmount = 0;

        menu.performTransferWithdraw(source);

        assertEquals(100, source.getBalance(), 0.001);
        assertEquals(50, target.getBalance(), 0.001);
    }

    @Test
    public void testValidTransferMovesMoney() {
        TestMainMenu menu = new TestMainMenu();

        BankAccount source = new BankAccount("Source");
        BankAccount target = new BankAccount("Target");

        source.deposit(100);
        target.deposit(50);

        menu.getAccounts().clear();
        menu.getAccounts().add(source);
        menu.getAccounts().add(target);

        menu.testAmount = 30;
        menu.testTargetIndex = 2;

        menu.performTransferWithdraw(source);

        assertEquals(70, source.getBalance(), 0.001);
        assertEquals(80, target.getBalance(), 0.001);
    }

    @Test
    public void testTransferToSameAccountIsUndone() {
        TestMainMenu menu = new TestMainMenu();

        BankAccount source = new BankAccount("Source");
        BankAccount other = new BankAccount("Other");

        source.deposit(100);
        other.deposit(50);

        menu.getAccounts().clear();
        menu.getAccounts().add(source);
        menu.getAccounts().add(other);

        menu.testAmount = 25;
        menu.testTargetIndex = 1; // selects source itself

        menu.performTransferWithdraw(source);

        assertEquals(100, source.getBalance(), 0.001);
        assertEquals(50, other.getBalance(), 0.001);
    }


    // admin tests
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