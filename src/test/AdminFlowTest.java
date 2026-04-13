package test;

import main.BankAccount;
import main.MainMenu;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

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
}

