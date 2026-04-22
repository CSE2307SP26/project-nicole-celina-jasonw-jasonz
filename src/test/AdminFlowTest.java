package test;

import main.BankAccount;
import main.MainMenu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminFlowTest {

    @Test
    void testAdminDeleteAccountValidRemoval() {
        MainMenu menu = new MainMenu("test_accounts.json");
        BankAccount acc1 = new BankAccount("alice", "1234");
        BankAccount acc2 = new BankAccount("bob", "5678");
        menu.getAccounts().clear();
        menu.getAccounts().add(acc1);
        menu.getAccounts().add(acc2);

        menu.runAdminDeleteAccount(acc1);
        assertFalse(menu.getAccounts().contains(acc1));
        assertTrue(menu.getAccounts().contains(acc2));
    }

    @Test
    void testAdminDeleteAccountNonexistentAccount() {
        MainMenu menu = new MainMenu("test_accounts.json");
        BankAccount acc1 = new BankAccount("alice", "1234");
        BankAccount acc2 = new BankAccount("bob", "5678");
        menu.getAccounts().clear();
        menu.getAccounts().add(acc1);

        menu.runAdminDeleteAccount(acc2);
        assertTrue(menu.getAccounts().contains(acc1));
        assertFalse(menu.getAccounts().contains(acc2));
    }

    @Test
    void testAdminDeleteAccountEmptyAccountList() {
        MainMenu menu = new MainMenu("test_accounts.json");
        menu.getAccounts().clear();

        BankAccount acc = new BankAccount("alice", "1234");
        menu.runAdminDeleteAccount(acc);
        assertFalse(menu.getAccounts().contains(acc));
    }

    @Test
    public void testAdminSetupGetsRecorded() {
        MainMenu menu = new MainMenu("test_admin.json");
        menu.recordAdminSetup("password", "blue", "cat");
        assertTrue(menu.checkAdminPassword("password"));
    }

    @Test
    public void testAdminPasswordCorrect() {
        MainMenu menu = new MainMenu("test_admin.json");
        menu.recordAdminSetup("password", "blue", "cat");
        boolean loginResult = menu.checkAdminPassword("password");
        assertTrue(loginResult);
    }

    @Test
    public void testAdminPasswordWrong() {
        MainMenu menu = new MainMenu("test_admin.json");
        menu.recordAdminSetup("password", "blue", "cat");
        boolean loginResult = menu.checkAdminPassword("wrongpassword");
        assertFalse(loginResult);
    }

    @Test
    public void testColorQuestionMatches() {
        MainMenu menu = new MainMenu("test_admin.json");
        menu.recordAdminSetup("password", "blue", "cat");
        boolean loginResult = menu.checkAdminAnswer("What is your favorite color?", "BLUE");
        assertTrue(loginResult);
    }

    @Test
    public void testAnimalQuestionMatches() {
        MainMenu menu = new MainMenu("test_admin.json");
        menu.recordAdminSetup("password", "blue", "cat");
        boolean loginResult = menu.checkAdminAnswer("What is your favorite animal?", "cAt");
        assertTrue(loginResult);
    }

    @Test
    public void testWrongAnswerLoginFails() {
        MainMenu menu = new MainMenu("test_admin.json");
        menu.recordAdminSetup("password", "blue", "cat");
        boolean loginResult = menu.checkAdminAnswer("What is your favorite color?", "red");
        assertFalse(loginResult);
    }

    @Test
    public void testAdminLoginSetupPersistsAcrossSessions() {
        MainMenu menu1 = new MainMenu("Test_admin.json");
        menu1.recordAdminSetup("password", "blue", "cat");
        MainMenu menu2 = new MainMenu("Test_admin.json");
        boolean passwordMatchesAcrossSession = menu2.checkAdminPassword("password");
        assertTrue(passwordMatchesAcrossSession);
    }

    @Test
    void testMaintenanceFeeValid() {
        MainMenu menu = new MainMenu("test_accounts.json");
        BankAccount account = new BankAccount("alice", "1234");
        account.deposit(100);
        menu.getAccounts().clear();
        menu.getAccounts().add(account);
        boolean result = account.setMaintenanceFee(5, 10, menu.getCurrentDay());
        assertTrue(result);
    }

    @Test
    void testMaintenanceFeeNotEligibleIfAccountHasHighBalance() {
        BankAccount account = new BankAccount("alice", "1234");
        account.deposit(600);
        boolean result = account.setMaintenanceFee(5, 10, 1);
        assertFalse(result);
    }

    @Test
    void testMaintenanceFeeTooLargeShowsInvalid() {
        BankAccount account = new BankAccount("alice", "1234");
        account.deposit(100);
        boolean result = account.setMaintenanceFee(20, 10, 1);
        assertFalse(result);
    }

    @Test
    void testMaintenanceFeeNotChargedBeforeStartDay() {
        MainMenu menu = new MainMenu("test_accounts.json");
        BankAccount account = new BankAccount("alice", "1234");
        account.deposit(100);
        menu.getAccounts().clear();
        menu.getAccounts().add(account);
        account.setMaintenanceFee(5, 5, menu.getCurrentDay());
        menu.advanceDaysAndProcess(4);
        assertEquals(100, account.getBalance(), 0.01);
    }

    @Test
    void testMaintenanceFeeChargedOnStartDay() {
        MainMenu menu = new MainMenu("test_accounts.json");
        BankAccount account = new BankAccount("alice", "1234");
        account.deposit(100);
        menu.getAccounts().clear();
        menu.getAccounts().add(account);
        account.setMaintenanceFee(5, 2, menu.getCurrentDay());
        menu.advanceDaysAndProcess(2);
        assertEquals(95, account.getBalance(), 0.01);
    }

    @Test
    void testMaintenanceFeeRepeatsEvery30Days() {
        MainMenu menu = new MainMenu("test_accounts.json");
        BankAccount account = new BankAccount("alice", "1234");
        account.deposit(100);
        menu.getAccounts().clear();
        menu.getAccounts().add(account);
        account.setMaintenanceFee(5, 1, menu.getCurrentDay());
        menu.advanceDaysAndProcess(1);
        menu.advanceDaysAndProcess(30);
        assertEquals(90, account.getBalance(), 0.01);
    }

    @Test
    void testMaintenanceFeeNotAppliedIfInsufficientBalance() {
        MainMenu menu = new MainMenu("test_accounts.json");
        BankAccount account = new BankAccount("alice", "1234");
        account.deposit(3);
        menu.getAccounts().clear();
        menu.getAccounts().add(account);
        account.setMaintenanceFee(5, 1, menu.getCurrentDay());
        menu.advanceDaysAndProcess(1);
        assertEquals(3, account.getBalance(), 0.01);
    }

    @Test
    void testMaintenanceFeePersistsAcrossSessions() {
        String file = "test_accounts.json";
        MainMenu menu1 = new MainMenu(file);
        BankAccount account = new BankAccount("alice", "1234");
        account.deposit(100);
        menu1.getAccounts().clear();
        menu1.recordNewAccount(account);
        boolean maintenanceFeeSaved = menu1.setAccountMaintenanceFee(account, 5, 1);
        assertTrue(maintenanceFeeSaved);
        MainMenu menu2 = new MainMenu(file);
        BankAccount loaded = menu2.getAccounts().get(0);
        assertTrue(loaded.hasMaintenanceFee());
        assertEquals(5, loaded.getMaintenanceFeeAmount(), 0.01);
        assertEquals(2, loaded.getMaintenanceFeeNextChargeDay());
    }
}

