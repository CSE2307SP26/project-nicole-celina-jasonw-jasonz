package test;

import main.BankAccount;
import main.MainMenu;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CustomerFlowTest {

    @Test
    public void testValidAccountClosing() {
        MainMenu menu = new MainMenu();
        BankAccount testAccount = new BankAccount("Test", "testpassword");
        menu.getAccounts().add(testAccount);
        menu.performCloseAccount(testAccount, true);
        assertEquals(false, menu.getAccounts().contains(testAccount));
    }

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

        BankAccount source = new BankAccount("Source", "testpassword");
        BankAccount target = new BankAccount("Target", "testpassword");

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

        BankAccount source = new BankAccount("Source", "testpassword");
        BankAccount target = new BankAccount("Target", "testpassword");

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
    public void testLargeTransferIsQueuedNotExecuted() {
        TestMainMenu menu = new TestMainMenu();

        BankAccount source = new BankAccount("Source", "testpassword");
        BankAccount target = new BankAccount("Target", "testpassword");

        source.deposit(MainMenu.LARGE_TRANSFER_THRESHOLD + 1);
        target.deposit(50);

        menu.getAccounts().clear();
        menu.getAccounts().add(source);
        menu.getAccounts().add(target);

        menu.testAmount = MainMenu.LARGE_TRANSFER_THRESHOLD + 1;
        menu.testTargetIndex = 2;

        menu.performTransferWithdraw(source);

        assertEquals(1, menu.getPendingLargeTransferCount());
        assertEquals(MainMenu.LARGE_TRANSFER_THRESHOLD + 1, source.getBalance(), 0.001);
        assertEquals(50, target.getBalance(), 0.001);
    }

    @Test
    public void testTransferToSameAccountIsUndone() {
        TestMainMenu menu = new TestMainMenu();

        BankAccount source = new BankAccount("Source", "testpassword");
        BankAccount other = new BankAccount("Other", "testpassword");

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

    // features: customer sign up & print accounts
    @Nested
    class SignUpTests {
        @TempDir
        Path tempDir;

        private MainMenu makeMenu() {
            return new MainMenu(tempDir.resolve("accounts.json").toString());
        }

        @Test
        void testRecordNewAccountReturnsTrue() {
            MainMenu menu = makeMenu();
            assertTrue(menu.recordNewAccount(new BankAccount("alice", "pass")));
        }

        @Test
        void testRecordNewAccountAddsToAccountsList() {
            MainMenu menu = makeMenu();
            BankAccount newAccount = new BankAccount("alice", "pass");
            menu.recordNewAccount(newAccount);
            assertTrue(menu.getAccounts().contains(newAccount));
        }

        @Test
        void testRecordNewAccountDuplicateUsernameReturnsFalse() {
            MainMenu menu = makeMenu();
            menu.recordNewAccount(new BankAccount("alice", "pass1"));
            assertFalse(menu.recordNewAccount(new BankAccount("alice", "pass2")));
        }

        @Test
        void testRecordNewAccountDuplicateNotAddedToList() {
            MainMenu menu = makeMenu();
            menu.recordNewAccount(new BankAccount("alice", "pass1"));
            int sizeBefore = menu.getAccounts().size();
            menu.recordNewAccount(new BankAccount("alice", "pass2"));
            assertEquals(sizeBefore, menu.getAccounts().size());
        }

        @Test
        void testRecordNewAccountPersistedAcrossSessions() {
            String file = tempDir.resolve("accounts.json").toString();
            MainMenu menu1 = new MainMenu(file);
            menu1.recordNewAccount(new BankAccount("alice", "pass"));
            MainMenu menu2 = new MainMenu(file);
            assertTrue(menu2.getAccounts().stream()
                    .anyMatch(a -> a.getAccountName().equals("alice")));
        }

        @Test
        void testInitializeAccountsLoadsAllPersistedAccounts() {
            String file = tempDir.resolve("accounts.json").toString();
            MainMenu menu1 = new MainMenu(file);
            menu1.recordNewAccount(new BankAccount("bob", "pass"));
            menu1.recordNewAccount(new BankAccount("carol", "pass"));
            MainMenu menu2 = new MainMenu(file);
            assertEquals(2, menu2.getAccounts().size());
        }

        @Test
        void testInitializeSkipsDefaultAccount() {
            String file = tempDir.resolve("accounts.json").toString();
            MainMenu menu1 = new MainMenu(file);
            menu1.recordNewAccount(new BankAccount("defaultaccount", "pass"));
            MainMenu menu2 = new MainMenu(file);
            assertFalse(menu2.getAccounts().stream()
                    .anyMatch(a -> a.getAccountName().equals("defaultaccount")));
        }

        @Test
        void testDuplicateDetectedFromFileNotJustMemory() {
            String file = tempDir.resolve("accounts.json").toString();
            MainMenu menu1 = new MainMenu(file);
            menu1.recordNewAccount(new BankAccount("alice", "pass"));
            MainMenu menu2 = new MainMenu(file);
            assertFalse(menu2.recordNewAccount(new BankAccount("alice", "differentpass")));
        }

        private String captureOutput(Runnable action) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            PrintStream original = System.out;
            System.setOut(new PrintStream(buf));
            try { action.run(); } finally { System.setOut(original); }
            return buf.toString();
        }

        @Test
        void testPrintAccountsShowsUsernameAndPassword() {
            MainMenu menu = makeMenu();
            menu.recordNewAccount(new BankAccount("alice", "secret"));
            String output = captureOutput(menu::runPrintAccountsFromFile);
            assertTrue(output.contains("alice"));
            assertTrue(output.contains("secret"));
        }

        @Test
        void testPrintAccountsShowsAllAccounts() {
            MainMenu menu = makeMenu();
            menu.recordNewAccount(new BankAccount("alice", "pass1"));
            menu.recordNewAccount(new BankAccount("bob", "pass2"));
            String output = captureOutput(menu::runPrintAccountsFromFile);
            assertTrue(output.contains("alice"));
            assertTrue(output.contains("bob"));
        }

        @Test
        void testPrintAccountsEmptyFileShowsNoAccountsMessage() {
            MainMenu menu = makeMenu();
            String output = captureOutput(menu::runPrintAccountsFromFile);
            assertTrue(output.contains("No registered accounts found."));
        }
    }

    // runCustomerLogInFlow tests (pure auth method)
    @Test
    void testAuthenticateCustomerSuccess() {
        MainMenu menu = new MainMenu("test_accounts.json");
        BankAccount acc = new BankAccount("alice", "1234");
        menu.getAccounts().clear();
        menu.getAccounts().add(acc);

        BankAccount result = menu.authenticateCustomerLogin("alice", "1234");
        assertEquals(acc, result);
    }

    @Test
    void testAuthenticateCustomerWrongPassword() {
        MainMenu menu = new MainMenu("test_accounts.json");
        BankAccount acc = new BankAccount("alice", "1234");
        menu.getAccounts().clear();
        menu.getAccounts().add(acc);

        BankAccount result = menu.authenticateCustomerLogin("alice", "123");
        assertEquals(null, result);
    }

    @Test
    void testAuthenticateCustomerWrongUsername() {
        MainMenu menu = new MainMenu("test_accounts.json");
        BankAccount acc = new BankAccount("alice", "1234");
        menu.getAccounts().clear();
        menu.getAccounts().add(acc);

        BankAccount result = menu.authenticateCustomerLogin("bob", "1234");
        assertEquals(null, result);
    }

    @Test
    void testAuthenticateCustomerNoAccounts() {
        MainMenu menu = new MainMenu("test_accounts.json");
        menu.getAccounts().clear();

        BankAccount result = menu.authenticateCustomerLogin("alice", "1234");
        assertEquals(null, result);
    }

    @Test
    void testAuthenticateCustomerMultipleAccounts() {
        MainMenu menu = new MainMenu("test_accounts.json");
        BankAccount acc1 = new BankAccount("alice", "1234");
        BankAccount acc2 = new BankAccount("bob", "5678");
        menu.getAccounts().clear();
        menu.getAccounts().add(acc1);
        menu.getAccounts().add(acc2);
        BankAccount result1 = menu.authenticateCustomerLogin("alice", "1234");
        assertEquals(acc1, result1);
        BankAccount result2 = menu.authenticateCustomerLogin("bob", "5678");
        assertEquals(acc2, result2);
    }
}

