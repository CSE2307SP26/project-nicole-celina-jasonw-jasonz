package test;

import main.BankAccount;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class BankAccountCoreTest {

    @Test
    public void testDeposit() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(50);
        assertEquals(50, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testDepositOnFrozenAccountFails() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(50);
        testAccount.setFrozen(true);
        try {
            testAccount.deposit(10);
            fail();
        } catch (IllegalStateException e) {
            assertEquals(50, testAccount.getBalance(), 0.01);
        }
    }

    @Test
    public void testWithdrawOnFrozenAccountFails() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(50);
        testAccount.setFrozen(true);
        try {
            testAccount.withdraw(10);
            fail();
        } catch (IllegalStateException e) {
            assertEquals(50, testAccount.getBalance(), 0.01);
        }
    }

    @Test
    public void testInvalidDeposit() {
        BankAccount testAccount = new BankAccount();
        try {
            testAccount.deposit(-50);
            fail();
        } catch (IllegalArgumentException e) {
            // test passes
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
            // test passes
        }
    }

    @Test
    public void testEmptyWithdrawal() {
        BankAccount testAccount = new BankAccount();
        try {
            testAccount.withdraw(20);
            fail();
        } catch (IllegalArgumentException e) {
            // test passes
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
            // test passes
        }
    }

    @Test
    public void testCreateAccountStoresInputName() {
        BankAccount testAccount = new BankAccount("Savings", "testpassword");
        assertEquals("Savings", testAccount.getAccountName());
    }

    @Test
    public void testCreateAccountHasZeroBalance() {
        BankAccount testAccount = new BankAccount("Savings", "testpassword");
        assertEquals(0, testAccount.getBalance(), 0.005);
    }

    @Test
    public void testNewAccountHasNoTransactionHistory() {
        BankAccount testAccount = new BankAccount();
        List<String> transactionHistory = testAccount.getTransactionHistory();
        assertEquals(0, transactionHistory.size());
    }

    @Test
    public void testDepositTransactionRecorded() {
        BankAccount testAccount = new BankAccount();
        testAccount.recordTransaction("Deposit", 50);
        List<String> transactionHistory = testAccount.getTransactionHistory();
        assertEquals(1, transactionHistory.size());
        assertEquals("Deposit: 50.0", transactionHistory.get(0));
    }

    @Test
    public void testWithdrawRecordsNegative() {
        BankAccount testAccount = new BankAccount();
        testAccount.recordTransaction("Withdraw", -30);
        List<String> transactionHistory = testAccount.getTransactionHistory();
        assertEquals(1, transactionHistory.size());
        assertEquals("Withdraw: -30.0", transactionHistory.get(0));
    }

    @Test
    public void testTransferOutRecordsNegative() {
        BankAccount testAccount = new BankAccount();
        testAccount.recordTransaction("Transfer Out", -20);
        List<String> transactionHistory = testAccount.getTransactionHistory();
        assertEquals(1, transactionHistory.size());
        assertEquals("Transfer Out: -20.0", transactionHistory.get(0));
    }

    @Test
    public void testTransferInRecordsPositive() {
        BankAccount testAccount = new BankAccount();
        testAccount.recordTransaction("Transfer In", 20);
        List<String> transactionHistory = testAccount.getTransactionHistory();
        assertEquals(1, transactionHistory.size());
        assertEquals("Transfer In: 20.0", transactionHistory.get(0));
    }

    @Test
    public void testMultipleTransactionsAreRecordedInOrder() {
        BankAccount testAccount = new BankAccount();
        testAccount.recordTransaction("Deposit", 50);
        testAccount.recordTransaction("Withdraw", -30);
        testAccount.recordTransaction("Transfer Out", -20);
        testAccount.recordTransaction("Transfer In", 20);
        List<String> transactionHistory = testAccount.getTransactionHistory();
        assertEquals(4, transactionHistory.size());
        assertEquals("Deposit: 50.0", transactionHistory.get(0));
        assertEquals("Withdraw: -30.0", transactionHistory.get(1));
        assertEquals("Transfer Out: -20.0", transactionHistory.get(2));
        assertEquals("Transfer In: 20.0", transactionHistory.get(3));
    }

    @Test
    public void testAccountStartsWithNoDebitCard() {
        BankAccount testAccount = new BankAccount();
        assertFalse(testAccount.hasDebitCard());
    }

    @Test
    public void testCreateDebitCardStoresFirstName() {
        BankAccount testAccount = new BankAccount();
        testAccount.createDebitCard("John", "Smith");
        assertEquals("John", testAccount.getDebitCardFirstName());
    }

    @Test
    public void testCreateDebitCardStoresLastName() {
        BankAccount testAccount = new BankAccount();
        testAccount.createDebitCard("John", "Smith");
        assertEquals("Smith", testAccount.getDebitCardLastName());
    }

    @Test
    public void testCreateDebitCardMarksAccountHasCard() {
        BankAccount testAccount = new BankAccount();
        testAccount.createDebitCard("John", "Smith");
        assertTrue(testAccount.hasDebitCard());
    }

    @Test
    public void testCreateDebitCardGeneratesCardNumber() {
        BankAccount testAccount = new BankAccount();
        testAccount.createDebitCard("John", "Smith");
        String cardNumber = testAccount.getDebitCardNumber();
        assertNotNull(cardNumber);
        assertFalse(cardNumber.isEmpty());
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
            // test passes
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

    @Test
    public void testApplyLoanCreditsBalanceAndSetsDueInfo() {
        BankAccount testAccount = new BankAccount();
        testAccount.applyLoan(200.0, 7, 1);
        assertTrue(testAccount.hasActiveLoan());
        assertEquals(200.0, testAccount.getBalance(), 0.01);
        assertEquals(8, testAccount.getActiveLoanDueDay());
        assertEquals(210.0, testAccount.getActiveLoanRepaymentAmount(), 0.01);
    }

    @Test
    public void testProcessLoanIfDueDeductsRepaymentWhenSufficientFunds() {
        BankAccount testAccount = new BankAccount();
        testAccount.applyLoan(100.0, 3, 1); // Due day = 4, repayment = 105
        testAccount.deposit(10.0); // Total balance now = 110
        boolean processed = testAccount.processLoanIfDue(4);
        assertTrue(processed);
        assertFalse(testAccount.hasActiveLoan());
        assertFalse(testAccount.isFrozen());
        assertEquals(5.0, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testProcessLoanIfDueFreezesWhenInsufficientFunds() {
        BankAccount testAccount = new BankAccount();
        testAccount.applyLoan(100.0, 2, 1); // Due day = 3, repayment = 105
        boolean processed = testAccount.processLoanIfDue(3);

        assertTrue(processed);
        assertTrue(testAccount.hasActiveLoan());
        assertTrue(testAccount.isFrozen());
        assertEquals(100.0, testAccount.getBalance(), 0.01);
        assertEquals(115.5, testAccount.getActiveLoanRepaymentAmount(), 0.01);
        assertTrue(testAccount.isLoanLatePenaltyApplied());
    }

    @Test
    public void testProcessLoanIfDueBeforeDueDateNoChange() {
        BankAccount testAccount = new BankAccount();
        testAccount.applyLoan(100.0, 5, 1); // Due day = 6
        boolean processed = testAccount.processLoanIfDue(5);
        assertFalse(processed);
        assertTrue(testAccount.hasActiveLoan());
        assertEquals(100.0, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testCannotApplySecondLoanWhenActiveLoanExists() {
        BankAccount testAccount = new BankAccount();
        testAccount.applyLoan(50.0, 2, 1);
        try {
            testAccount.applyLoan(20.0, 2, 1);
            fail();
        } catch (IllegalStateException e) {
            assertEquals(50.0, testAccount.getBalance(), 0.01);
            assertTrue(testAccount.hasActiveLoan());
        }
    }

    @Test 
    public void testLoanRepaymentBasics() {
        BankAccount testAccount = new BankAccount();
        try {
            testAccount.makeLoanRepayment(10, 0);
            fail();
        } catch (IllegalStateException e) {
            assertEquals(0, testAccount.getBalance(), 0.01);
            assertFalse(testAccount.hasActiveLoan());
        }
        testAccount.applyLoan(50, 2, 1);
        assertEquals(50, testAccount.getBalance(), 0.01);
        assertEquals(52.5, testAccount.getActiveLoanRepaymentAmount(), 0.01);
        testAccount.deposit(5);
        assertEquals(55, testAccount.getBalance(), 0.01);
        try {
            testAccount.makeLoanRepayment(20, 1);
            assertEquals(32.5, testAccount.getActiveLoanRepaymentAmount(), 0.01);
            assertEquals(35, testAccount.getBalance(), 0.01);
            assertTrue(testAccount.hasActiveLoan());
        } catch (Exception e) {
            fail();
        }
        try {
            testAccount.makeLoanRepayment(32.5, 1);
            assertEquals(0, testAccount.getActiveLoanRepaymentAmount(), 0.01);
            assertFalse(testAccount.hasActiveLoan());
            assertEquals(2.5, testAccount.getBalance(), 0.01);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testLoanRepaymentEdgeCases() {
        BankAccount testAccount = new BankAccount();
        testAccount.applyLoan(50, 2, 1);

        assertEquals(52.5, testAccount.getActiveLoanRepaymentAmount(), 0.01);
        assertEquals(50, testAccount.getBalance(), 0.01);
        assertTrue(testAccount.hasActiveLoan());

        testAccount.deposit(100);
        assertEquals(150, testAccount.getBalance(), 0.01);

        try {
            testAccount.makeLoanRepayment(55, 1);
            assertEquals(97.5, testAccount.getBalance(), 0.01);
            assertEquals(0, testAccount.getActiveLoanRepaymentAmount(), 0.01);
            assertFalse(testAccount.hasActiveLoan());
        } catch (Exception e) {
            fail();
        }

        testAccount = new BankAccount();
        testAccount.applyLoan(50, 2, 1);
        testAccount.deposit(100);

        try {
            testAccount.makeLoanRepayment(160, 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(150, testAccount.getBalance(), 0.01);
            assertEquals(52.5, testAccount.getActiveLoanRepaymentAmount(), 0.01);
            assertTrue(testAccount.hasActiveLoan());
        }

        testAccount = new BankAccount();
        testAccount.applyLoan(50, 2, 1);
        testAccount.deposit(100);

        try {
            testAccount.makeLoanRepayment(52.5, 4);
            assertEquals(97.5, testAccount.getBalance(), 0.01);
            assertEquals(0, testAccount.getActiveLoanRepaymentAmount(), 0.01);
            assertFalse(testAccount.hasActiveLoan());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testPartialLoanRepaymentThenDefaultOnDueDate() {
        BankAccount testAccount = new BankAccount();

        testAccount.applyLoan(50, 2, 1);

        assertEquals(50, testAccount.getBalance(), 0.01);
        assertEquals(52.5, testAccount.getActiveLoanRepaymentAmount(), 0.01);
        assertEquals(3, testAccount.getActiveLoanDueDay());
        assertTrue(testAccount.hasActiveLoan());
        assertFalse(testAccount.isFrozen());

        testAccount.makeLoanRepayment(20, 2);

        assertEquals(30, testAccount.getBalance(), 0.01);
        assertEquals(32.5, testAccount.getActiveLoanRepaymentAmount(), 0.01);
        assertTrue(testAccount.hasActiveLoan());
        assertFalse(testAccount.isFrozen());

        boolean processed = testAccount.processLoanIfDue(3);

        assertTrue(processed);
        assertTrue(testAccount.isFrozen());
        assertTrue(testAccount.hasActiveLoan());
        assertEquals(30, testAccount.getBalance(), 0.01);
        assertEquals(35.75, testAccount.getActiveLoanRepaymentAmount(), 0.01);
        assertTrue(testAccount.isLoanLatePenaltyApplied());
    }

    @Test
    public void testLoanLatePenaltyAppliedOnlyOnce() {
        BankAccount testAccount = new BankAccount();
        testAccount.applyLoan(100.0, 2, 1); // repayment = 105, due day = 3

        boolean processedFirst = testAccount.processLoanIfDue(3);
        assertTrue(processedFirst);
        assertTrue(testAccount.hasActiveLoan());
        assertTrue(testAccount.isFrozen());
        assertEquals(115.5, testAccount.getActiveLoanRepaymentAmount(), 0.01);
        assertTrue(testAccount.isLoanLatePenaltyApplied());

        boolean processedSecond = testAccount.processLoanIfDue(4);
        assertTrue(processedSecond);
        assertEquals(115.5, testAccount.getActiveLoanRepaymentAmount(), 0.01);
        assertTrue(testAccount.isLoanLatePenaltyApplied());
    }

    @Test
    public void testOverdueRepaymentAllowedWhenFrozen() {
        BankAccount testAccount = new BankAccount();
        testAccount.applyLoan(50.0, 2, 1); // repayment = 52.5, due day = 3

        boolean processed = testAccount.processLoanIfDue(3);
        assertTrue(processed);
        assertTrue(testAccount.isFrozen());
        assertTrue(testAccount.hasActiveLoan());
        assertEquals(57.75, testAccount.getActiveLoanRepaymentAmount(), 0.01);

        // Balance is still 50 after default, so partial overdue repayment is possible
        try {
            testAccount.makeLoanRepayment(20.0, 4);
            assertEquals(30.0, testAccount.getBalance(), 0.01);
            assertEquals(37.75, testAccount.getActiveLoanRepaymentAmount(), 0.01);
            assertTrue(testAccount.hasActiveLoan());
            assertTrue(testAccount.isFrozen());
        } catch (Exception e) {
            fail();
        }
    }
}

