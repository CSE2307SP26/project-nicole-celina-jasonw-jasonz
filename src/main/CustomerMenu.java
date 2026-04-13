package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class CustomerMenu {
    private final List<BankAccount> accounts;
    private final List<PendingLargeTransfer> pendingLargeTransfers;
    private final AccountStorage accountStorage;
    private final Scanner keyboardInput;
    private final ConsolePrompts prompts;
    private final SystemTime systemTime;
    private final TimeStorage timeStorage;

    CustomerMenu(
            List<BankAccount> accounts,
            List<PendingLargeTransfer> pendingLargeTransfers,
            AccountStorage accountStorage,
            Scanner keyboardInput,
            SystemTime systemTime,
            TimeStorage timeStorage
    ) {
        this.accounts = accounts;
        this.pendingLargeTransfers = pendingLargeTransfers;
        this.accountStorage = accountStorage;
        this.keyboardInput = keyboardInput;
        this.prompts = new ConsolePrompts(keyboardInput);
        this.systemTime = systemTime;
        this.timeStorage = timeStorage;
    }

    void displayAccountDetailMenu(BankAccount account) {
        System.out.println();
        System.out.println("Day " + systemTime.getCurrentDay());
        System.out.println("--- Account detail: " + account.getAccountName() + " ---");
        if (account.isFrozen()) {
            System.out.println("(This account is frozen: deposits, withdrawals, and transfers are disabled.)");
        }
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Check balance");
        System.out.println("4. Transfer money");
        System.out.println("5. View transaction history");
        System.out.println("6. View debit card");
        System.out.println("7. Close this account");
        System.out.println("8. Fast-forward time (days)");
        System.out.println("9. Exit program");
    }

    BankAccount authenticateCustomerLogin(String username, String password) {
        for (BankAccount acc : accounts) {
            if (acc.getAccountName().equals(username) && acc.getAccountPassword().equals(password)) {
                return acc;
            }
        }
        return null;
    }

    void runCustomerLogInFlow() {
        System.out.println();
        System.out.println("Enter your username: ");
        String username = keyboardInput.next();
        System.out.println("Enter your password: ");
        String password = keyboardInput.next();
        BankAccount account = authenticateCustomerLogin(username, password);
        if (account == null) {
            System.out.println("Invalid username or password. Returning to main menu.");
            return;
        }
        account.setLoggedIn(true);
        System.out.println("Login successful. Welcome back, " + account.getAccountName() + "!");
        System.out.println("Current day: Day " + systemTime.getCurrentDay());
        runAccountDetailLoop(account);
    }

    void runCustomerSignUpFlow() {
        System.out.println();
        System.out.println("Create a username: ");
        String newAccountUsername = keyboardInput.next();
        System.out.println("Create a password: ");
        String newAccountPassword = keyboardInput.next();
        BankAccount newAccount = new BankAccount(newAccountUsername, newAccountPassword);
        newAccount.setLoggedIn(true);
        boolean created = recordNewAccount(newAccount);
        if (!created) {
            return;
        }
        System.out.println("Your new account (" + newAccountUsername + ") has been created. Automatically logging you in.");
        System.out.println("Current day: Day " + systemTime.getCurrentDay());
        runAccountDetailLoop(newAccount);
    }

    boolean recordNewAccount(BankAccount newAccount) {
        List<BankAccount> stored = accountStorage.readAccounts();
        if (isUsernameTaken(newAccount.getAccountName(), stored)) {
            System.out.println("This username already exists. Try again with a different username.");
            return false;
        }
        stored.add(newAccount);
        accountStorage.writeAccounts(stored);
        accounts.add(newAccount);
        return true;
    }

    void initializeAccountsArrayList() {
        List<BankAccount> persisted = new ArrayList<>(accountStorage.readAccounts());
        persisted.removeIf(acc -> acc.getAccountName().equals("defaultaccount"));
        accounts.clear();
        accounts.addAll(persisted);
        accountStorage.writeAccounts(accounts);
    }

    void performCloseAccount(BankAccount account) {
        accounts.remove(account);
        accountStorage.writeAccounts(accounts);
        System.out.println("Account [" + account.getAccountName() + "] is closed. Taking you back to the main menu.");
    }

    private boolean isUsernameTaken(String username, List<BankAccount> stored) {
        for (BankAccount acc : stored) {
            if (username.equals(acc.getAccountName())) {
                return true;
            }
        }
        return false;
    }

    private void runAccountDetailLoop(BankAccount account) {
        int action = -1;
        while (action != 9) {
            displayAccountDetailMenu(account);
            action = prompts.getUserSelection(9);
            if (!handleAccountDetailAction(action, account)) {
                return;
            }
        }
    }

    private boolean handleAccountDetailAction(int action, BankAccount account) {
        switch (action) {
            case 1:
                performDeposit(account);
                return true;
            case 2:
                performWithdrawal(account);
                return true;
            case 3:
                System.out.println("Current balance: " + account.getBalance());
                return true;
            case 4:
                performTransferWithdraw(account);
                return true;
            case 5:
                performViewTransactionHistory(account);
                return true;
            case 6:
                performViewDebitCard(account);
                return true;
            case 7:
                performCloseAccount(account);
                return false;
            case 8:
                fastForwardTimeFlow();
                return true;
            default:
                return true;
        }
    }

    private void fastForwardTimeFlow() {
        int days = prompts.promptPositiveInt("Enter number of days to fast-forward: ");
        try {
            systemTime.advanceDays(days);
            timeStorage.write(systemTime);
            System.out.println("Time advanced. Current day is now Day " + systemTime.getCurrentDay());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void performDeposit(BankAccount account) {
        if (account.isFrozen()) {
            System.out.println("This account is frozen. Deposits are not allowed.");
            return;
        }
        double amount = prompts.promptNonNegativeAmount("How much would you like to deposit: ");
        if (amount == 0) {
            System.out.println("No deposit made.");
            return;
        }
        try {
            account.deposit(amount);
            account.recordTransaction("Deposit", amount);
            System.out.println("Deposit successful. ");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount.");
        }
    }

    private void performWithdrawal(BankAccount account) {
        if (account.isFrozen()) {
            System.out.println("This account is frozen. Withdrawals are not allowed.");
            return;
        }
        double amount = prompts.promptNonNegativeAmount("How much would you like to withdraw: ");
        if (amount == 0) {
            System.out.println("No withdrawal made.");
            return;
        }
        try {
            account.withdraw(amount);
            account.recordTransaction("Withdraw", -amount);
            System.out.println("Withdrawal successful.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount.");
        }
    }

    private void performViewTransactionHistory(BankAccount account) {
        if (account.getTransactionHistory().isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        System.out.println("Transaction History for: " + account.getAccountName());
        for (String transaction : account.getTransactionHistory()) {
            System.out.println(transaction);
        }
    }

    private void performViewDebitCard(BankAccount account) {
        if (account.hasDebitCard()) {
            printDebitCardInfo(account);
            return;
        }
        runDebitCardSetupFlow(account);
    }

    private void printDebitCardInfo(BankAccount account) {
        System.out.println("---Here's your debit card information---");
        System.out.println("Cardholder name: " + account.getDebitCardFirstName() + " " + account.getDebitCardLastName());
        System.out.println("Card number: " + account.getDebitCardNumber());
        System.out.println("Linked bank account: " + account.getAccountName());
    }

    private void runDebitCardSetupFlow(BankAccount account) {
        System.out.println("This account currently does not have a debit card. You can choose to set one up.");
        System.out.println("1. Set up debit card");
        System.out.println("2. Back to account detail");
        int choice = prompts.getUserSelection(2);
        if (choice != 1) {
            return;
        }
        System.out.print("Enter first name for the debit card: ");
        String firstName = keyboardInput.nextLine().trim();
        System.out.print("Enter last name for the debit card: ");
        String lastName = keyboardInput.nextLine().trim();
        if (firstName.isEmpty() || lastName.isEmpty()) {
            System.out.println("First and last name cannot be empty.");
            return;
        }
        account.createDebitCard(firstName, lastName);
        System.out.println("---Your debit card has been set up---");
        printDebitCardInfo(account);
    }

    private void performTransferWithdraw(BankAccount account) {
        System.out.println("--- Transfer money between accounts ---");
        if (account.isFrozen()) {
            System.out.println("This account is frozen. Transfers are not allowed.");
            return;
        }
        printAccountListNumbered(accounts);
        double amount = prompts.promptNonNegativeAmount("Amount to transfer from [" + account.getAccountName() + "]: ");
        if (amount == 0) {
            System.out.println("No transfer made.");
            return;
        }
        int idx = promptAccountIndex("Select the account to transfer this amount into: ");
        BankAccount target = accounts.get(idx - 1);
        if (target == account) {
            System.out.println("You cannot transfer money to the same account.");
            return;
        }
        if (target.isFrozen()) {
            System.out.println("The destination account is frozen. Transfers are not allowed.");
            return;
        }
        if (account.getBalance() < amount) {
            System.out.println("Insufficient balance.");
            return;
        }
        if (amount > MainMenu.LARGE_TRANSFER_THRESHOLD) {
            pendingLargeTransfers.add(new PendingLargeTransfer(account, target, amount));
            System.out.println("This transfer exceeds $" + MainMenu.LARGE_TRANSFER_THRESHOLD
                    + " and requires administrator approval. Your request has been submitted.");
            return;
        }
        try {
            account.withdraw(amount);
            account.recordTransaction("Transfer Out", -amount);
            target.deposit(amount);
            target.recordTransaction("Transfer In", amount);
            System.out.println("--- Here's your updated account balance: ---");
            System.out.println(account.getAccountName() + ": " + account.getBalance());
            System.out.println(target.getAccountName() + ": " + target.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printAccountListNumbered(List<BankAccount> list) {
        System.out.println("Accounts:");
        for (int i = 0; i < list.size(); i++) {
            BankAccount a = list.get(i);
            String frozenTag = a.isFrozen() ? " [FROZEN]" : "";
            System.out.println("  " + (i + 1) + ". " + a.getAccountName()
                    + " — balance: " + a.getBalance() + frozenTag);
        }
    }

    private int promptAccountIndex(String prompt) {
        int n = 0;
        while (n - 1 < 0 || n - 1 >= accounts.size()) {
            System.out.print(prompt);
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
}

