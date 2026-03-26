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
    public void testNewAccountHasNoTransactionHistory() {
        BankAccount testAccount = new BankAccount();
        String transactionHistory = testAccount.getTransactionHistory();
        assertEquals("", transactionHistory);
    }

    @Test
    public void testDepositTransactionRecorded() {
        BankAccount testAccount = new BankAccount();
        testAccount.recordTransaction("Deposit", 50);
        String transactionHistory = testAccount.getTransactionHistory();
        assertEquals("Deposit: 50.0\n", transactionHistory);
    }

    @Test
    public void testWithdrawRecordsNegative() {
        BankAccount testAccount = new BankAccount();
        testAccount.recordTransaction("Withdraw", -30);
        String transactionHistory = testAccount.getTransactionHistory();
        assertEquals("Withdraw: -30.0\n", transactionHistory);
    }

    @Test
    public void testTransferOutRecordsNegative() {
        BankAccount testAccount = new BankAccount();
        testAccount.recordTransaction("Transfer Out", -20);
        String transactionHistory = testAccount.getTransactionHistory();
        assertEquals("Transfer Out: -20.0\n", transactionHistory);
    }

    @Test
    public void testMultipleTransactionsAreRecordedInOrder() {
        BankAccount testAccount = new BankAccount();
        testAccount.recordTransaction("Deposit", 50);
        testAccount.recordTransaction("Withdraw", -30);
        testAccount.recordTransaction("Transfer Out", -20);
        String transactionHistory = testAccount.getTransactionHistory();
        assertEquals("Deposit: 50.0\nWithdraw: -30.0\nTransfer Out: -20.0\n", transactionHistory);
    }
}
