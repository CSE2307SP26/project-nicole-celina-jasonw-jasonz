package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;


public class MainMenu {

    private static final int ACCOUNT_AUTH_LOGIN = 1;
    private static final int ACCOUNT_AUTH_SIGNUP = 2;
    private static final int ACCOUNT_AUTH_EXIT = 3;
    private static final int MAX_AUTH_SELECTION = 3;

    private static final int CUSTOMER_SELECT_ACCOUNT = 1;
    private static final int CUSTOMER_OPEN_ACCOUNT = 2;
    private static final int CUSTOMER_EXIT_TO_ROLE = 3;
    private static final int MAX_CUSTOMER_MAIN_SELECTION = 3;

    private static final int ACCT_DETAIL_DEPOSIT = 1;
    private static final int ACCT_DETAIL_WITHDRAW = 2;
    private static final int ACCT_DETAIL_CHECK_BALANCE = 3;
    private static final int ACCT_DETAIL_TRANSFER = 4;
    private static final int ACCT_DETAIL_TRANSACTION_HISTORY = 5;
    private static final int ACCT_DETAIL_CLOSE_ACCOUNT = 6;
    private static final int ACCT_DETAIL_BACK = 7;
    private static final int MAX_ACCOUNT_DETAIL_SELECTION = 7;

    private static final int ADMIN_CHOOSE_ACCOUNT = 1;
    private static final int ADMIN_REVIEW_PENDING_TRANSFERS = 2;
    private static final int ADMIN_REVIEW_ACCOUNT_LIST = 3;
    private static final int ADMIN_BACK_TO_LOGIN = 4;
    private static final int MAX_ADMIN_TOP_SELECTION = 4;

    /**
     * Transfers above this amount require administrator approval before funds
     * move.
     */
    public static final double LARGE_TRANSFER_THRESHOLD = 10000.0;

    private static final int ADMIN_ACT_COLLECT_FEE = 1;
    private static final int ADMIN_ACT_INTEREST = 2;
    private static final int ADMIN_ACT_FREEZE = 3;
    private static final int ADMIN_ACT_UNFREEZE = 4;
    private static final int ADMIN_ACT_BACK_TO_LIST = 5;
    private static final int MAX_ADMIN_ACTION_SELECTION = 5;

    private final List<BankAccount> accounts;
    private static final String ACCOUNTS_FILE = "accounts.json";
    private final List<PendingLargeTransfer> pendingLargeTransfers;
    private final Scanner keyboardInput;

    public static class PendingLargeTransfer {

        public final BankAccount from;
        public final BankAccount to;
        public final double amount;

        public PendingLargeTransfer(BankAccount from, BankAccount to, double amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }
    }

    public MainMenu() {
        this.accounts = new ArrayList<>();
        this.pendingLargeTransfers = new ArrayList<>();
        this.keyboardInput = new Scanner(System.in);
        initializeAccountsArrayList();
    }

    public int getPendingLargeTransferCount() {
        return pendingLargeTransfers.size();
    }

    BankAccount getDefaultAccount() {
        return accounts.get(0);
    }

    public void displayAuthModeSelection() {
        System.out.println();
        System.out.println("Welcome to the 237 Bank App!");
        System.out.println("Do you have an account with us?");
        System.out.println("1. Log in (customer / admin)");
        System.out.println("2. Sign up for a customer account");
        System.out.println("3. Exit the app");
    }

    public void displayCustomerMainMenu() {
        System.out.println();
        System.out.println("--- Customer ---");
        System.out.println("1. Select account");
        System.out.println("2. Create account");
        System.out.println("3. Return to role selection");
    }

    public void displayAdministratorTopMenu() {
        System.out.println();
        System.out.println("--- Administrator ---");
        System.out.println("1. Select account to manage");
        System.out.println("2. Review pending large transfers");
        System.out.println("3. View account login info");
        System.out.println("4. Log out");
    }

    public int getUserSelection(int max) {
        int selection = -1;
        while (selection < 1 || selection > max) {
            System.out.print("Please make a selection: ");
            while (!keyboardInput.hasNextInt()) { // handle non-integer input
                System.out.println("Invalid input. Please enter a number.");
                keyboardInput.next();
            }
            selection = keyboardInput.nextInt();
        }
        return selection;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    /**
     * Prints all accounts with 1-based indices (reusable for admin UI).
     */
    void printAccountListNumbered(List<BankAccount> list) {
        System.out.println("Accounts:");
        for (int i = 0; i < list.size(); i++) {
            BankAccount a = list.get(i);
            String frozenTag = a.isFrozen() ? " [FROZEN]" : "";
            System.out.println("  " + (i + 1) + ". " + a.getAccountName()
                    + " — balance: " + a.getBalance() + frozenTag);
        }
    }

    /**
     * Prompts for a non-negative amount (0 allowed where caller treats it as
     * invalid loop).
     */
    public double promptNonNegativeAmount(String prompt) {
        double amount = -1;
        while (amount < 0) {
            System.out.print(prompt);
            amount = keyboardInput.nextDouble();
            if (amount < 0) {
                System.out.println("Amount must be non-negative.");
            }
        }
        return amount;
    }

    /**
     * Prompts for a positive integer (e.g. months in an interest period).
     */
    int promptPositiveInt(String prompt) {
        int n = 0;
        while (n <= 0) {
            System.out.print(prompt);
            n = keyboardInput.nextInt();
        }
        return n;
    }

    // prompts for viable account index
    public int promptAccountIndex(String prompt) {
        int n = 0;
        while (n - 1 < 0 || n - 1 >= accounts.size()) {
            System.out.print(prompt);
            n = keyboardInput.nextInt();
            if (n - 1 < 0 || n - 1 >= accounts.size()) {
                System.out.println("Invalid account index. Please try again.");
            }
        }
        return n;
    }

    void displayAccountDetailMenu(BankAccount account) {
        System.out.println();
        System.out.println("--- Account detail: " + account.getAccountName() + " ---");
        if (account.isFrozen()) {
            System.out.println("(This account is frozen: deposits, withdrawals, and transfers are disabled.)");
        }
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Check balance");
        System.out.println("4. Transfer money");
        System.out.println("5. View transaction history");
        System.out.println("6. Close this account");
        System.out.println("7. Exit program");
    }

    public void performDeposit(BankAccount account) {
        double depositAmount = promptNonNegativeAmount("How much would you like to deposit: ");
        if (depositAmount == 0) {
            System.out.println("No deposit made.");
            return;
        }
        try {
            account.deposit(depositAmount);
            account.recordTransaction("Deposit", depositAmount);
            System.out.println("Deposit successful. ");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount.");
        }
    }

    public void performWithdrawal(BankAccount account) {
        double withdrawalAmount = promptNonNegativeAmount("How much would you like to withdraw: ");

        if (withdrawalAmount == 0) {
            System.out.println("No withdrawal made.");
            return;
        }
        try {
            account.withdraw(withdrawalAmount);
            account.recordTransaction("Withdraw", -withdrawalAmount);
            System.out.println("Withdrawal successful.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount.");
        }
    }

    public void performCheckBalance(BankAccount account) {
        System.out.println("Current balance: " + account.getBalance());
    }

    public void performCloseAccount(BankAccount account, boolean isTesting) {
        accounts.remove(account);
        System.out.println("Account [" + account.getAccountName() + "] is closed. Taking you back to the main menu.");
        if (!isTesting) {
            // runCustomerFlow();
        }
    }

    public void performTransferWithdraw(BankAccount account) {
        System.out.println("--- Transfer money between accounts ---");
        if (account.isFrozen()) {
            System.out.println("This account is frozen. Transfers are not allowed.");
            return;
        }
        printAccountListNumbered(accounts);
        double transferAmount = promptNonNegativeAmount("Amount to transfer from [" + account.getAccountName() + "]: ");
        if (transferAmount == 0) {
            System.out.println("No transfer made.");
            return;
        }
        int targetAccountIndex = promptAccountIndex("Select the account to transfer this amount into: ");
        BankAccount targetAccount = accounts.get(targetAccountIndex - 1);
        if (targetAccount == account) {
            System.out.println("You cannot transfer money to the same account.");
            return;
        }
        if (targetAccount.isFrozen()) {
            System.out.println("The destination account is frozen. Transfers are not allowed.");
            return;
        }
        if (transferAmount > LARGE_TRANSFER_THRESHOLD) {
            if (account.getBalance() < transferAmount) {
                System.out.println("Insufficient balance.");
                return;
            }
            pendingLargeTransfers.add(new PendingLargeTransfer(account, targetAccount, transferAmount));
            System.out.println("This transfer exceeds $" + LARGE_TRANSFER_THRESHOLD
                    + " and requires administrator approval. Your request has been submitted.");
            return;
        }
        try {
            completeImmediateTransfer(account, targetAccount, transferAmount);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    void completeImmediateTransfer(BankAccount from, BankAccount to, double transferAmount) {
        from.withdraw(transferAmount);
        from.recordTransaction("Transfer Out", -transferAmount);
        to.deposit(transferAmount);
        to.recordTransaction("Transfer In", transferAmount);
        System.out.println("--- Here's your updated account balance: ---");
        System.out.println(from.getAccountName() + ": " + from.getBalance());
        System.out.println(to.getAccountName() + ": " + to.getBalance());
    }

    void runPendingLargeTransfersReview() {
        while (true) {
            if (pendingLargeTransfers.isEmpty()) {
                System.out.println("No pending large transfer requests.");
                return;
            }
            System.out.println();
            System.out.println("--- Pending large transfers (over $" + LARGE_TRANSFER_THRESHOLD + ") ---");
            for (int i = 0; i < pendingLargeTransfers.size(); i++) {
                PendingLargeTransfer p = pendingLargeTransfers.get(i);
                System.out.println((i + 1) + ". " + p.from.getAccountName() + " -> " + p.to.getAccountName()
                        + " | $" + p.amount);
            }
            int backIndex = pendingLargeTransfers.size() + 1;
            System.out.println("  " + backIndex + ". Back");
            int choice = getUserSelection(backIndex);
            if (choice == backIndex) {
                return;
            }
            PendingLargeTransfer selected = pendingLargeTransfers.get(choice - 1);
            System.out.println("1. Approve");
            System.out.println("2. Deny");
            System.out.println("3. Cancel");
            int action = getUserSelection(3);
            if (action == 1) {
                approvePendingLargeTransfer(selected);
            } else if (action == 2) {
                denyPendingLargeTransfer(selected);
            }
        }
    }

    void approvePendingLargeTransfer(PendingLargeTransfer p) {
        int idx = pendingLargeTransfers.indexOf(p);
        if (idx < 0) {
            System.out.println("That request is no longer pending.");
            return;
        }
        if (p.from.isFrozen() || p.to.isFrozen()) {
            System.out.println("Cannot approve: one or both accounts are frozen.");
            return;
        }
        if (p.from.getBalance() < p.amount) {
            System.out.println("Cannot approve: insufficient balance. Request removed.");
            pendingLargeTransfers.remove(idx);
            return;
        }
        try {
            completeImmediateTransfer(p.from, p.to, p.amount);
            pendingLargeTransfers.remove(p);
            System.out.println("Large transfer approved and completed.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    void denyPendingLargeTransfer(PendingLargeTransfer p) {
        if (pendingLargeTransfers.remove(p)) {
            System.out.println("Request denied and removed.");
        }
    }

    public void performViewTransactionHistory(BankAccount account) {
        if (account.getTransactionHistory().isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        } else {
            System.out.println("Transaction History for: " + account.getAccountName());
            for (String transaction : account.getTransactionHistory()) {
                System.out.println(transaction);
            }
        }
    }

    void runAccountDetailLoop(BankAccount account) {
        int action = -1;
        while (action != ACCT_DETAIL_BACK) {
            displayAccountDetailMenu(account);
            action = getUserSelection(MAX_ACCOUNT_DETAIL_SELECTION);
            switch (action) {
                case ACCT_DETAIL_DEPOSIT:
                    performDeposit(account);
                    break;
                case ACCT_DETAIL_WITHDRAW:
                    performWithdrawal(account);
                    break;
                case ACCT_DETAIL_CHECK_BALANCE:
                    performCheckBalance(account);
                    break;
                case ACCT_DETAIL_TRANSFER:
                    performTransferWithdraw(account);
                    break;
                case ACCT_DETAIL_TRANSACTION_HISTORY:
                    performViewTransactionHistory(account);
                    break;
                case ACCT_DETAIL_CLOSE_ACCOUNT:
                    performCloseAccount(account, false);
                    break;
                default:
                    break;
            }
        }
    }

    void runSelectAccountFlow() {
        if (accounts.isEmpty()) {
            System.out.println("You have no accounts yet. Open an account first.");
            return;
        }
        BankAccount selected = promptSelectAccountOrBack();
        if (selected != null) {
            runAccountDetailLoop(selected);
        }
    }

    /**
     * Returns chosen account, or null if user chose back.
     */
    BankAccount promptSelectAccountOrBack() {
        printAccountListNumbered(accounts);
        int backIndex = accounts.size() + 1;
        System.out.println("  " + backIndex + ". Back");
        int choice = getUserSelection(backIndex);
        if (choice == backIndex) {
            return null;
        }
        return accounts.get(choice - 1);
    }

    void displayAdminActionsForAccount(BankAccount account) {
        System.out.println();
        System.out.println("Managing: " + account.getAccountName() + " | Balance: " + account.getBalance()
                + (account.isFrozen() ? " | FROZEN" : ""));
        System.out.println("1. Collect fee");
        System.out.println("2. Apply interest payment (rate % × principal × period)");
        System.out.println("3. Freeze account");
        System.out.println("4. Unfreeze account");
        System.out.println("5. Back to account list");
    }

    void performCollectFee(BankAccount account) {
        double fee = promptNonNegativeAmount("Fee amount to collect: ");
        if (fee == 0) {
            System.out.println("No fee collected.");
            return;
        }
        try {
            account.collectFee(fee);
            System.out.println("Fee collected. New balance: " + account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    void performInterestPayment(BankAccount account) {
        double principal = account.getBalance();
        if (principal <= 0) {
            System.out.println("No principal balance — no interest can accrue.");
            return;
        }
        System.out.println("Principal (current balance): " + principal);
        System.out.println("Interest = principal × (annual rate % / 100) × (months / 12).");
        double annualRate = promptNonNegativeAmount("Annual interest rate (% per year), e.g. 3 for 3%: ");
        int months = promptPositiveInt("Number of months in this accrual period: ");
        if (annualRate == 0) {
            System.out.println("Rate is 0% — no interest credited.");
            return;
        }
        try {
            double credited = account.applyInterestPayment(annualRate, months);
            System.out.println("Interest credited: " + credited);
            System.out.println("New balance: " + account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid rate or period.");
        }
    }

    void performFreezeAccount(BankAccount account) {
        if (account.isFrozen()) {
            System.out.println("This account is already frozen.");
            return;
        }
        account.setFrozen(true);
        System.out.println("Account \"" + account.getAccountName() + "\" is now frozen.");
    }

    void performUnfreezeAccount(BankAccount account) {
        if (!account.isFrozen()) {
            System.out.println("This account is not frozen.");
            return;
        }
        account.setFrozen(false);
        System.out.println("Account \"" + account.getAccountName() + "\" is now unfrozen.");
    }

    void runAdminAccountActions(BankAccount account) {
        int action = -1;
        while (action != ADMIN_ACT_BACK_TO_LIST) {
            displayAdminActionsForAccount(account);
            action = getUserSelection(MAX_ADMIN_ACTION_SELECTION);
            switch (action) {
                case ADMIN_ACT_COLLECT_FEE:
                    performCollectFee(account);
                    break;
                case ADMIN_ACT_INTEREST:
                    performInterestPayment(account);
                    break;
                case ADMIN_ACT_FREEZE:
                    performFreezeAccount(account);
                    break;
                case ADMIN_ACT_UNFREEZE:
                    performUnfreezeAccount(account);
                    break;
                default:
                    break;
            }
        }
    }

    public void runAdministratorFlow() {
        int top = -1;
        while (top != ADMIN_BACK_TO_LOGIN) {
            displayAdministratorTopMenu();
            top = getUserSelection(MAX_ADMIN_TOP_SELECTION);
            if (top == ADMIN_CHOOSE_ACCOUNT) {
                runAdminAccountSelectionLoop();
            } else if (top == ADMIN_REVIEW_PENDING_TRANSFERS) {
                runPendingLargeTransfersReview();
            } else if (top == ADMIN_REVIEW_ACCOUNT_LIST) {
                // runPrintAccountsFromFile();
            }
        }
    }

    void runAdminAccountSelectionLoop() {
        boolean backToAdminTop = false;
        while (!backToAdminTop) {
            BankAccount selected = promptSelectAccountOrBack();
            if (selected == null) {
                backToAdminTop = true;
            } else {
                runAdminAccountActions(selected);
            }
        }
    }



    // feature: customer sign up

    private List<BankAccount> readAccountsFromFile() {
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        Type listType = new TypeToken<List<BankAccount>>(){}.getType();
        try (FileReader reader = new FileReader(file)) {
            List<BankAccount> result = new Gson().fromJson(reader, listType);
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error reading accounts file.");
            return new ArrayList<>();
        }
    }

    private void writeAccountsToFile(List<BankAccount> list) {
        try (FileWriter writer = new FileWriter(ACCOUNTS_FILE)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(list, writer);
        } catch (IOException e) {
            System.out.println("Error writing accounts file.");
        }
    }

    private boolean isUsernameTaken(String username, List<BankAccount> stored) {
        for (BankAccount acc : stored) {
            if (username.equals(acc.getAccountName())) return true;
        }
        return false;
    }

    public boolean recordNewAccount(BankAccount newAccount) {
        List<BankAccount> stored = readAccountsFromFile();
        if (isUsernameTaken(newAccount.getAccountName(), stored)) {
            System.out.println("This username already exists. Try again with a different username.");
            return false;
        }
        stored.add(newAccount);
        writeAccountsToFile(stored);
        accounts.add(newAccount);
        return true;
    }

    public void runCustomerSignUpFlow() {
        System.out.println();
        System.out.println("Create a username: ");
        String newAccountUsername = keyboardInput.next();
        System.out.println("Create a password: ");
        String newAccountPassword = keyboardInput.next();
        BankAccount newAccount = new BankAccount(newAccountUsername, newAccountPassword);
        newAccount.setLoggedIn(true);
        boolean created = recordNewAccount(newAccount);
        if (created) {
            System.out.println("Your new account (" + newAccountUsername + ") has been created. Automatically logging you in.");
            runAccountDetailLoop(newAccount);
        } else {
            run();
        }
    }


    public void initializeAccountsArrayList() {
        accounts.clear();
        for (BankAccount acc : readAccountsFromFile()) {
            if (!acc.getAccountName().equals("defaultaccount")) {
                accounts.add(acc);
            }
        }
        writeAccountsToFile(accounts);
    }



    // TODO: adapt from previous runCustomerFlow()
    public void runLogInFlow() {
        int selection = -1;
        while (selection != CUSTOMER_EXIT_TO_ROLE) {
            displayCustomerMainMenu();
            selection = getUserSelection(MAX_CUSTOMER_MAIN_SELECTION);
            switch (selection) {
                case CUSTOMER_SELECT_ACCOUNT:
                    runSelectAccountFlow();
                    break;
                case CUSTOMER_OPEN_ACCOUNT:
                    // performCreateAccount();
                    break;
                default:
                    break;
            }
        }
    }

    public void run() {
        initializeAccountsArrayList();
        int accountAccessMethod = -1;
        while (accountAccessMethod != ACCOUNT_AUTH_EXIT) {
            displayAuthModeSelection();
            accountAccessMethod = getUserSelection(MAX_AUTH_SELECTION);
            if (accountAccessMethod == ACCOUNT_AUTH_LOGIN) {
                // runCustomerFlow(); 
                runAdministratorFlow();
                // runLogInFlow(); //TODO
            } else if (accountAccessMethod == ACCOUNT_AUTH_SIGNUP) {
                // runAdministratorFlow();
                runCustomerSignUpFlow();
                return;
            }
        }
        System.out.println();
        System.out.println("Thank you for using the 237 Bank App. Goodbye!");
    }

    public static void main(String[] args) {
        MainMenu bankApp = new MainMenu();
        bankApp.run();
    }

}
