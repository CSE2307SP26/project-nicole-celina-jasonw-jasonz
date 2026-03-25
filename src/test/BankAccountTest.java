package test;

import main.BankAccount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

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
    public void testCreateAccountStoresInputName() {
        BankAccount testAccount = new BankAccount();
        testAccount.setAccountName("Savings");
        assertNotNull(testAccount);
        assertEquals("Savings", testAccount.getAccountName());
    }

    @Test
    public void testCreateAccountHasZeroBalance() {
        BankAccount testAccount = new BankAccount();
        assertEquals(0, testAccount.getBalance(), 0.005);
    }
}
