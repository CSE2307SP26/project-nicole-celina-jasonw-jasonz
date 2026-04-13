package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private static final int ACCOUNT_CUSTOMER_LOGIN = 1;
    private static final int ACCOUNT_CUSTOMER_SIGNUP = 2;
    private static final int ACCOUNT_ADMIN_LOGIN = 3;
    private static final int ACCOUNT_AUTH_EXIT = 4;
    private static final int MAX_AUTH_SELECTION = 4;

    /**
     * Transfers above this amount require administrator approval before funds
     * move.
     */
    public static final double LARGE_TRANSFER_THRESHOLD = 10000.0;

    private final List<BankAccount> accounts;
    private final String accountsFile;
    private final Scanner keyboardInput;
    private final List<PendingLargeTransfer> pendingLargeTransfers;
    private final AccountStorage accountStorage;
    private final AdminStorage adminStorage;
    private final TimeStorage timeStorage;
    private final SystemTime systemTime;
    private final CustomerMenu customerMenu;
    private final AdminMenu adminMenu;
    private final ConsolePrompts prompts;

    public MainMenu() {
        this(StoragePaths.DEFAULT_ACCOUNTS_FILE);
    }

    public MainMenu(String accountsFile) {
        this.accountsFile = StoragePaths.normalizeDataFilePath(accountsFile);
        this.accounts = new ArrayList<>();
        this.pendingLargeTransfers = new ArrayList<>();
        this.keyboardInput = new Scanner(System.in);
        this.prompts = new ConsolePrompts(keyboardInput);
        if (StoragePaths.DEFAULT_ACCOUNTS_FILE.equals(this.accountsFile)) {
            StoragePaths.migrateLegacyStorageIfNeeded();
        }
        this.accountStorage = new AccountStorage(this.accountsFile);
        this.adminStorage = new AdminStorage(StoragePaths.buildAdminFilePath(this.accountsFile));
        this.timeStorage = new TimeStorage(StoragePaths.buildTimeFilePath(this.accountsFile));
        this.systemTime = timeStorage.readOrDefault();
        // Requirement: each app restart resets time to Day 1.
        this.systemTime.resetToDay1();
        this.timeStorage.write(systemTime);
        this.customerMenu = new CustomerMenu(accounts, pendingLargeTransfers, accountStorage, keyboardInput, systemTime, timeStorage);
        this.adminMenu = new AdminMenu(accounts, pendingLargeTransfers, accountStorage, adminStorage, keyboardInput, systemTime, timeStorage);
        customerMenu.initializeAccountsArrayList();
    }

    public int getPendingLargeTransferCount() {
        return pendingLargeTransfers.size();
    }

    public void displayAuthModeSelection() {
        System.out.println();
        System.out.println("Welcome to the 237 Bank App!");
        System.out.println("Current day: Day " + systemTime.getCurrentDay());
        System.out.println("Do you have an account with us?");
        System.out.println("1. Log in as customer");
        System.out.println("2. Sign up for a customer account");
        System.out.println("3. Log in as administrator");
        System.out.println("4. Exit the app");
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    // Kept for unit tests (overridable in test subclasses).
    public double promptNonNegativeAmount(String message) {
        return prompts.promptNonNegativeAmount(message);
    }

    // Kept for unit tests (overridable in test subclasses).
    public int promptAccountIndex(String message) {
        int n = 0;
        while (n - 1 < 0 || n - 1 >= accounts.size()) {
            System.out.print(message);
            while (!keyboardInput.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                keyboardInput.next();
            }
            n = keyboardInput.nextInt();
            keyboardInput.nextLine();
            if (n - 1 < 0 || n - 1 >= accounts.size()) {
                System.out.println("Invalid account index. Please try again.");
            }
        }
        return n;
    }

    // Kept for unit tests; production flow uses CustomerMenu.
    public void performTransferWithdraw(BankAccount account) {
        try {
            TransferFlow.performTransferWithdraw(account, accounts, pendingLargeTransfers, new TransferPrompts() {
                @Override
                public double promptNonNegativeAmount(String message) {
                    return MainMenu.this.promptNonNegativeAmount(message);
                }

                @Override
                public int promptAccountIndex(String message) {
                    return MainMenu.this.promptAccountIndex(message);
                }
            });
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    // Kept for unit tests (signature + behavior).
    public void performCloseAccount(BankAccount account, boolean isTesting) {
        customerMenu.performCloseAccount(account);
    }

    public void initializeAccountsArrayList() {
        customerMenu.initializeAccountsArrayList();
    }

    public void runPrintAccountsFromFile() {
        adminMenu.runPrintAccountsFromFile();
    }

    public BankAccount authenticateCustomerLogin(String username, String password) {
        return customerMenu.authenticateCustomerLogin(username, password);
    }

    public void runAdminDeleteAccount(BankAccount account) {
        adminMenu.runAdminDeleteAccount(account);
    }

    public void recordAdminSetup(String adminPassword, String favoriteColorAnswer, String favoriteAnimalAnswer) {
        adminMenu.recordAdminSetup(adminPassword, favoriteColorAnswer, favoriteAnimalAnswer);
    }

    public boolean checkAdminPassword(String adminPassword) {
        return adminMenu.checkAdminPassword(adminPassword);
    }

    public boolean checkAdminAnswer(String question, String answer) {
        return adminMenu.checkAdminAnswer(question, answer);
    }

    public boolean recordNewAccount(BankAccount newAccount) {
        return customerMenu.recordNewAccount(newAccount);
    }

    public void run() {
        initializeAccountsArrayList();
        int accountAccessMethod = -1;
        while (accountAccessMethod != ACCOUNT_AUTH_EXIT) {
            displayAuthModeSelection();
            accountAccessMethod = prompts.getUserSelection(MAX_AUTH_SELECTION);
            if (accountAccessMethod == ACCOUNT_CUSTOMER_LOGIN) {
                customerMenu.runCustomerLogInFlow();
            } else if (accountAccessMethod == ACCOUNT_CUSTOMER_SIGNUP) {
                customerMenu.runCustomerSignUpFlow();
            } else if (accountAccessMethod == ACCOUNT_ADMIN_LOGIN) {
                adminMenu.runAdminLoginFlow();
            } else if (accountAccessMethod == ACCOUNT_AUTH_EXIT) {
                // exit loop and end program
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

class PendingLargeTransfer {
    final BankAccount from;
    final BankAccount to;
    final double amount;

    PendingLargeTransfer(BankAccount from, BankAccount to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}

class ConsolePrompts {
    private final Scanner keyboardInput;

    ConsolePrompts(Scanner keyboardInput) {
        this.keyboardInput = keyboardInput;
    }

    int getUserSelection(int max) {
        int selection = -1;
        while (selection < 1 || selection > max) {
            System.out.print("Please make a selection: ");
            while (!keyboardInput.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                keyboardInput.next();
            }
            selection = keyboardInput.nextInt();
            keyboardInput.nextLine();
        }
        return selection;
    }

    double promptNonNegativeAmount(String prompt) {
        double amount = -1;
        while (amount < 0) {
            System.out.print(prompt);
            amount = keyboardInput.nextDouble();
            if (amount < 0) {
                System.out.println("Amount must be non-negative.");
            }
        }
        keyboardInput.nextLine();
        return amount;
    }

    int promptPositiveInt(String prompt) {
        int n = 0;
        while (n <= 0) {
            System.out.print(prompt);
            n = keyboardInput.nextInt();
            keyboardInput.nextLine();
        }
        return n;
    }
}

interface TransferPrompts {
    double promptNonNegativeAmount(String message);

    int promptAccountIndex(String message);
}

final class TransferFlow {
    private TransferFlow() {}

    static void performTransferWithdraw(
            BankAccount from,
            List<BankAccount> accounts,
            List<PendingLargeTransfer> pendingLargeTransfers,
            TransferPrompts prompts
    ) {
        System.out.println("--- Transfer money between accounts ---");
        if (from.isFrozen()) {
            System.out.println("This account is frozen. Transfers are not allowed.");
            return;
        }
        printAccountListNumbered(accounts);
        double amount = prompts.promptNonNegativeAmount("Amount to transfer from [" + from.getAccountName() + "]: ");
        if (amount == 0) {
            System.out.println("No transfer made.");
            return;
        }
        int targetIdx = prompts.promptAccountIndex("Select the account to transfer this amount into: ");
        BankAccount to = accounts.get(targetIdx - 1);
        if (to == from) {
            System.out.println("You cannot transfer money to the same account.");
            return;
        }
        if (to.isFrozen()) {
            System.out.println("The destination account is frozen. Transfers are not allowed.");
            return;
        }
        if (from.getBalance() < amount) {
            System.out.println("Insufficient balance.");
            return;
        }
        if (amount > MainMenu.LARGE_TRANSFER_THRESHOLD) {
            pendingLargeTransfers.add(new PendingLargeTransfer(from, to, amount));
            System.out.println("This transfer exceeds $" + MainMenu.LARGE_TRANSFER_THRESHOLD
                    + " and requires administrator approval. Your request has been submitted.");
            return;
        }
        from.withdraw(amount);
        from.recordTransaction("Transfer Out", -amount);
        to.deposit(amount);
        to.recordTransaction("Transfer In", amount);
    }

    private static void printAccountListNumbered(List<BankAccount> list) {
        System.out.println("Accounts:");
        for (int i = 0; i < list.size(); i++) {
            BankAccount a = list.get(i);
            String frozenTag = a.isFrozen() ? " [FROZEN]" : "";
            System.out.println("  " + (i + 1) + ". " + a.getAccountName()
                    + " — balance: " + a.getBalance() + frozenTag);
        }
    }
}

