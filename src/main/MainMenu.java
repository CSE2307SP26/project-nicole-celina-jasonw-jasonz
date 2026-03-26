package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private static final int ROLE_CUSTOMER = 1;
    private static final int ROLE_ADMINISTRATOR = 2;
    private static final int ROLE_EXIT = 3;
    private static final int MAX_ROLE_SELECTION = 3;

    private static final int CUSTOMER_SELECT_ACCOUNT = 1;
    private static final int CUSTOMER_OPEN_ACCOUNT = 2;
    private static final int CUSTOMER_EXIT_TO_ROLE = 3;
    private static final int MAX_CUSTOMER_MAIN_SELECTION = 3;

    private static final int ACCT_DETAIL_DEPOSIT = 1;
    private static final int ACCT_DETAIL_WITHDRAW = 2;
    private static final int ACCT_DETAIL_TRANSFER = 3;
    private static final int ACCT_DETAIL_BACK = 4;
    private static final int MAX_ACCOUNT_DETAIL_SELECTION = 4;

    private static final int ADMIN_CHOOSE_ACCOUNT = 1;
    private static final int ADMIN_BACK_TO_ROLE = 2;
    private static final int MAX_ADMIN_TOP_SELECTION = 2;

    private static final int ADMIN_ACT_COLLECT_FEE = 1;
    private static final int ADMIN_ACT_INTEREST = 2;
    private static final int ADMIN_ACT_BACK_TO_LIST = 3;
    private static final int MAX_ADMIN_ACTION_SELECTION = 3;

    private final List<BankAccount> accounts;
    private final Scanner keyboardInput;

    public MainMenu() {
        this.accounts = new ArrayList<>();
        this.accounts.add(new BankAccount("Default Account"));
        this.keyboardInput = new Scanner(System.in);
    }

    public void displayRoleSelection() {
        System.out.println();
        System.out.println("Welcome to the 237 Bank App!");
        System.out.println("Select your role:");
        System.out.println("1. Customer");
        System.out.println("2. Administrator");
        System.out.println("3. Exit the app");
    }

    public void displayCustomerMainMenu() {
        System.out.println();
        System.out.println("--- Customer ---");
        System.out.println("1. Select account");
        System.out.println("2. Open account");
        System.out.println("3. Return to role selection");
    }

    public void displayAdministratorTopMenu() {
        System.out.println();
        System.out.println("--- Administrator ---");
        System.out.println("1. Select account to manage");
        System.out.println("2. Return to role selection");
    }

    public int getUserSelection(int max) {
        int selection = -1;
        while (selection < 1 || selection > max) {
            System.out.print("Please make a selection: ");
            selection = keyboardInput.nextInt();
        }
        return selection;
    }

    /**
     * Prints all accounts with 1-based indices (reusable for admin UI).
     */
    void printAccountListNumbered(List<BankAccount> list) {
        System.out.println("Accounts:");
        for (int i = 0; i < list.size(); i++) {
            BankAccount a = list.get(i);
            System.out.println("  " + (i + 1) + ". " + a.getName()
                    + " — balance: " + a.getBalance());
        }
    }

    /**
     * Prompts for a non-negative amount (0 allowed where caller treats it as invalid loop).
     */
    double promptNonNegativeAmount(String prompt) {
        double amount = -1;
        while (amount < 0) {
            System.out.print(prompt);
            amount = keyboardInput.nextDouble();
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

    void runOpenAccount() {
        keyboardInput.nextLine();
        System.out.print("Name for the new account: ");
        String name = keyboardInput.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Account name cannot be empty.");
            return;
        }
        accounts.add(new BankAccount(name));
        System.out.println("Account opened: " + name);
    }

    void displayAccountDetailMenu(BankAccount account) {
        System.out.println();
        System.out.println("--- Account detail: " + account.getName() + " ---");
        System.out.println("Balance: " + account.getBalance());
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Transfer money");
        System.out.println("4. Back to customer menu");
    }

    void runAccountDetailLoop(BankAccount account) {
        int action = -1;
        while (action != ACCT_DETAIL_BACK) {
            displayAccountDetailMenu(account);
            action = getUserSelection(MAX_ACCOUNT_DETAIL_SELECTION);
            switch (action) {
                case ACCT_DETAIL_DEPOSIT:
                    System.out.println("(Deposit — not implemented yet.)");
                    break;
                case ACCT_DETAIL_WITHDRAW:
                    System.out.println("(Withdraw — not implemented yet.)");
                    break;
                case ACCT_DETAIL_TRANSFER:
                    System.out.println("(Transfer money — not implemented yet.)");
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
        System.out.println("Managing: " + account.getName() + " | Balance: " + account.getBalance());
        System.out.println("1. Collect fee");
        System.out.println("2. Apply interest payment (rate % × principal × period)");
        System.out.println("3. Back to account list");
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
                default:
                    break;
            }
        }
    }

    public void runAdministratorFlow() {
        int top = -1;
        while (top != ADMIN_BACK_TO_ROLE) {
            displayAdministratorTopMenu();
            top = getUserSelection(MAX_ADMIN_TOP_SELECTION);
            if (top == ADMIN_CHOOSE_ACCOUNT) {
                runAdminAccountSelectionLoop();
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

    public void runCustomerFlow() {
        int selection = -1;
        while (selection != CUSTOMER_EXIT_TO_ROLE) {
            displayCustomerMainMenu();
            selection = getUserSelection(MAX_CUSTOMER_MAIN_SELECTION);
            switch (selection) {
                case CUSTOMER_SELECT_ACCOUNT:
                    runSelectAccountFlow();
                    break;
                case CUSTOMER_OPEN_ACCOUNT:
                    runOpenAccount();
                    break;
                default:
                    break;
            }
        }
    }

    public void run() {
        int role = -1;
        while (role != ROLE_EXIT) {
            displayRoleSelection();
            role = getUserSelection(MAX_ROLE_SELECTION);
            if (role == ROLE_CUSTOMER) {
                runCustomerFlow();
            } else if (role == ROLE_ADMINISTRATOR) {
                runAdministratorFlow();
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
