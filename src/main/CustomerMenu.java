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
    private final ScheduledTransferService scheduledTransferService;

    CustomerMenu(
            List<BankAccount> accounts,
            List<PendingLargeTransfer> pendingLargeTransfers,
            AccountStorage accountStorage,
            Scanner keyboardInput,
            SystemTime systemTime,
            TimeStorage timeStorage,
            ScheduledTransferService scheduledTransferService
    ) {
        this.accounts = accounts;
        this.pendingLargeTransfers = pendingLargeTransfers;
        this.accountStorage = accountStorage;
        this.keyboardInput = keyboardInput;
        this.prompts = new ConsolePrompts(keyboardInput);
        this.systemTime = systemTime;
        this.timeStorage = timeStorage;
        this.scheduledTransferService = scheduledTransferService;
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
        System.out.println("7. Update account credentials");
        System.out.println("8. Close this account");
        System.out.println("9. Apply for a loan");
        System.out.println("10. Repay due loan");
        System.out.println("11. Fast-forward time (days)");
        System.out.println("12. Exit program");
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
        System.out.println("Note: Accounts need to have a minimum balance of $500 to avoid possible maintenance fees, which would be at most $10 per 30 days if applied.");
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
        while (action != 11) {
            displayAccountDetailMenu(account);
            action = prompts.getUserSelection(11);
            if (!handleAccountDetailAction(action, account)) {
                return;
            }
        }
    }

    private boolean handleAccountDetailAction(int action, BankAccount account) {
        switch (action) {
            case 1: return runDepositAction(account);
            case 2: return runWithdrawalAction(account);
            case 3: return runCheckBalanceAction(account);
            case 4: return runTransferAction(account);
            case 5: return runTransactionHistoryAction(account);
            case 6: return runDebitCardAction(account);
            case 7: return runUpdateCredentialsAction(account);
            case 8: return runCloseAccountAction(account);
            case 9: return runLoanAction(account);
            case 10: return runRepayLoanAction(account);
            case 11: return runFastForwardAction();
            default: return true;
        }
    }

    private boolean runDepositAction(BankAccount account) {
        performDeposit(account);
        return true;
    }

    private boolean runWithdrawalAction(BankAccount account) {
        performWithdrawal(account);
        return true;
    }

    private boolean runCheckBalanceAction(BankAccount account) {
        System.out.println("Current balance: " + account.getBalance());
        return true;
    }

    private boolean runTransferAction(BankAccount account) {
        performTransferWithdraw(account);
        return true;
    }

    private boolean runTransactionHistoryAction(BankAccount account) {
        performViewTransactionHistory(account);
        return true;
    }

    private boolean runDebitCardAction(BankAccount account) {
        performViewDebitCard(account);
        return true;
    }

    private boolean runUpdateCredentialsAction(BankAccount account) {
        runUpdateCredentialsFlow(account);
        return true;
    }

    private boolean runCloseAccountAction(BankAccount account) {
        performCloseAccount(account);
        return false;
    }

    private boolean runLoanAction(BankAccount account) {
        applyLoanFlow(account);
        return true;
    }
    private boolean runRepayLoanAction(BankAccount account) {
        performLoanRepayment(account);
        return true;
    }

    private boolean runFastForwardAction() {
        fastForwardTimeFlow();
        return true;
    }

    private void applyLoanFlow(BankAccount account) {
        double amount = prompts.promptNonNegativeAmount("Enter loan amount: ");
        if (amount == 0) {
            System.out.println("No loan created.");
            return;
        }
        int repaymentDays = prompts.promptPositiveInt("Enter days until repayment is due: ");
        try {
            account.applyLoan(amount, repaymentDays, systemTime.getCurrentDay());
            accountStorage.writeAccounts(accounts);
            System.out.println("Loan approved at fixed interest rate: " + (BankAccount.LOAN_FIXED_INTEREST_RATE * 100) + "%");
            System.out.println("Repayment due on Day " + account.getActiveLoanDueDay()
                    + " for amount: " + account.getActiveLoanRepaymentAmount());
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void performLoanRepayment(BankAccount account) {
        if (!account.hasActiveLoan()){
            System.out.println("You do not have an active loan to repay.");
            return;
        }
        System.out.println("Active loan repayment amount: " + account.getActiveLoanRepaymentAmount());
        System.out.println("Repayment due on Day " + account.getActiveLoanDueDay());
        double amount = prompts.promptNonNegativeAmount("Enter repayment amount: ");
        if (amount == 0) {
            System.out.println("No repayment made.");
            return;
        }
        try {
            account.makeLoanRepayment(amount, systemTime.getCurrentDay());
            accountStorage.writeAccounts(accounts);
            if (account.hasActiveLoan()){
                System.out.println("Repayment successful. Remaining loan repayment amount: " + account.getActiveLoanRepaymentAmount());
            } else {
                System.out.println("Loan fully paid.");
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void runUpdateCredentialsFlow(BankAccount account) {
        System.out.println();
        System.out.println("--- Update account credentials ---");
        System.out.println("1. Update username");
        System.out.println("2. Update password");
        System.out.println("3. Update both");
        System.out.println("4. Cancel");
        int choice = prompts.getUserSelection(4);
        switch (choice) {
            case 1: updateUsernameFlow(account); break;
            case 2: updatePasswordFlow(account); break;
            case 3: updateBothCredentialsFlow(account); break;
            default: System.out.println("No changes made.");
        }
    }

    private void updateBothCredentialsFlow(BankAccount account) {
        if (updateUsernameFlow(account)) {
            updatePasswordFlow(account);
        }
    }

    private boolean updateUsernameFlow(BankAccount account) {
        System.out.println("Enter new username: ");
        String newUsername = keyboardInput.next();
        return performUpdateUsername(account, newUsername);
    }

    private boolean updatePasswordFlow(BankAccount account) {
        System.out.println("Enter new password: ");
        String newPassword = keyboardInput.next();
        System.out.println("Confirm new password: ");
        String confirmPassword = keyboardInput.next();
        return performUpdatePassword(account, newPassword, confirmPassword);
    }

    public boolean performUpdateUsername(BankAccount account, String newUsername) {
        if (newUsername.equals(account.getAccountName())) {
            System.out.println("New username matches the current username. No change made.");
            return false;
        }
        if (isUsernameTakenByOther(newUsername, account)) {
            System.out.println("This username is already taken. No change made.");
            return false;
        }
        account.setAccountName(newUsername);
        accountStorage.writeAccounts(accounts);
        System.out.println("Username updated to: " + newUsername);
        return true;
    }

    public boolean performUpdatePassword(BankAccount account, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match. No change made.");
            return false;
        }
        account.setAccountPassword(newPassword);
        accountStorage.writeAccounts(accounts);
        System.out.println("Password updated successfully.");
        return true;
    }

    private boolean isUsernameTakenByOther(String username, BankAccount current) {
        for (BankAccount acc : accounts) {
            if (acc != current && username.equals(acc.getAccountName())) {
                return true;
            }
        }
        return false;
    }

    private void fastForwardTimeFlow() {
        int days = prompts.promptPositiveInt("Enter number of days to fast-forward: ");
        try {
            systemTime.advanceDays(days);
            processDueLoansForAllAccounts();
            processMaintenanceFeesForAllAccounts();
            scheduledTransferService.processDue(systemTime.getCurrentDay(), accounts, accountStorage);
            timeStorage.write(systemTime);
            System.out.println("Time advanced. Current day is now Day " + systemTime.getCurrentDay());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void processDueLoansForAllAccounts() {
        boolean anyProcessed = false;
        for (BankAccount acc : accounts) {
            if (acc.processLoanIfDue(systemTime.getCurrentDay())) {
                anyProcessed = true;
            }
        }
        if (anyProcessed) {
            accountStorage.writeAccounts(accounts);
            System.out.println("Loan repayment processing completed for due accounts.");
        }
    }

    private void processMaintenanceFeesForAllAccounts() {
        for (BankAccount account : accounts) {
            account.processMaintenanceFee(systemTime.getCurrentDay());
        }
    }

    private void performDeposit(BankAccount account) {
        if (isFrozenBlockingDeposit(account)) {
            return;
        }
        double amount = prompts.promptNonNegativeAmount("How much would you like to deposit: ");
        if (amount == 0) {
            System.out.println("No deposit made.");
            return;
        }
        tryExecuteDeposit(account, amount);
    }

    private boolean isFrozenBlockingDeposit(BankAccount account) {
        if (account.isFrozen()) {
            System.out.println("This account is frozen. Deposits are not allowed.");
            return true;
        }
        return false;
    }

    private void tryExecuteDeposit(BankAccount account, double amount) {
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
        if (isFrozenBlockingWithdrawal(account)) {
            return;
        }
        double amount = prompts.promptNonNegativeAmount("How much would you like to withdraw: ");
        if (amount == 0) {
            System.out.println("No withdrawal made.");
            return;
        }
        tryExecuteWithdrawal(account, amount);
    }

    private boolean isFrozenBlockingWithdrawal(BankAccount account) {
        if (account.isFrozen()) {
            System.out.println("This account is frozen. Withdrawals are not allowed.");
            return true;
        }
        return false;
    }

    private void tryExecuteWithdrawal(BankAccount account, double amount) {
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
        printDebitCardSetupMenu();
        int choice = prompts.getUserSelection(2);
        if (choice != 1) {
            return;
        }
        if (!readDebitCardNamesAndCreate(account)) {
            return;
        }
        System.out.println("---Your debit card has been set up---");
        printDebitCardInfo(account);
    }

    private void printDebitCardSetupMenu() {
        System.out.println("This account currently does not have a debit card. You can choose to set one up.");
        System.out.println("1. Set up debit card");
        System.out.println("2. Back to account detail");
    }

    private boolean readDebitCardNamesAndCreate(BankAccount account) {
        System.out.print("Enter first name for the debit card: ");
        String firstName = keyboardInput.nextLine().trim();
        System.out.print("Enter last name for the debit card: ");
        String lastName = keyboardInput.nextLine().trim();
        if (firstName.isEmpty() || lastName.isEmpty()) {
            System.out.println("First and last name cannot be empty.");
            return false;
        }
        account.createDebitCard(firstName, lastName);
        return true;
    }

    private boolean isFrozenTransferSource(BankAccount account) {
        if (account.isFrozen()) {
            System.out.println("This account is frozen. Transfers are not allowed.");
            return true;
        }
        return false;
    }

    private void performTransferWithdraw(BankAccount account) {
        System.out.println("--- Transfer money between accounts ---");
        if (isFrozenTransferSource(account)) {
            return;
        }
        int timingChoice = promptTransferTiming();
        if (timingChoice == 1) {
            runImmediateTransferFlow(account);
        } else {
            runScheduledTransferFlow(account);
        }
    }

    private int promptTransferTiming() {
        System.out.println("When would you like to complete this transfer?");
        System.out.println("1. Complete transfer now");
        System.out.println("2. Schedule transfer for a later day");
        return prompts.getUserSelection(2);
    }

    private void runImmediateTransferFlow(BankAccount account) {
        printAccountListNumbered(accounts);
        Double amount = promptTransferAmountOrNull(account);
        if (amount == null) {
            return;
        }
        BankAccount target = promptValidTransferTargetOrNull(account);
        if (target == null) {
            return;
        }
        finishTransferAfterAmountAndTarget(account, target, amount);
    }

    private void runScheduledTransferFlow(BankAccount account) {
        printAccountListNumbered(accounts);
        Double amount = promptTransferAmountOrNull(account);
        if (amount == null) {
            return;
        }
        BankAccount target = promptValidTransferTargetOrNull(account);
        if (target == null) {
            return;
        }
        int days = prompts.promptPositiveInt("Schedule transfer how many days from today: ");
        queueScheduledTransfer(account, target, amount, days);
    }

    public boolean queueScheduledTransfer(BankAccount from, BankAccount to, double amount, int days) {
        int scheduledDay = systemTime.getCurrentDay() + days;
        scheduledTransferService.schedule(new ScheduledTransfer(
                from.getAccountName(), to.getAccountName(), amount, scheduledDay));
        System.out.println("Transfer of " + amount + " to " + to.getAccountName()
                + " scheduled for Day " + scheduledDay
                + ". Funds will be deducted from your account on that day (must be sufficient at that time).");
        return true;
    }

    private void finishTransferAfterAmountAndTarget(BankAccount account, BankAccount target, double amount) {
        if (!hasSufficientBalanceForTransfer(account, amount)) {
            return;
        }
        if (tryQueueLargeTransfer(account, target, amount)) {
            return;
        }
        completeImmediateTransfer(account, target, amount);
    }

    private Double promptTransferAmountOrNull(BankAccount account) {
        double amount = prompts.promptNonNegativeAmount(
                "Amount to transfer from [" + account.getAccountName() + "]: ");
        if (amount == 0) {
            System.out.println("No transfer made.");
            return null;
        }
        return amount;
    }

    private BankAccount promptValidTransferTargetOrNull(BankAccount account) {
        int idx = promptAccountIndex("Select the account to transfer this amount into: ");
        BankAccount target = accounts.get(idx - 1);
        if (target == account) {
            System.out.println("You cannot transfer money to the same account.");
            return null;
        }
        if (target.isFrozen()) {
            System.out.println("The destination account is frozen. Transfers are not allowed.");
            return null;
        }
        return target;
    }

    private boolean hasSufficientBalanceForTransfer(BankAccount account, double amount) {
        if (account.getBalance() < amount) {
            System.out.println("Insufficient balance.");
            return false;
        }
        return true;
    }

    private boolean tryQueueLargeTransfer(BankAccount account, BankAccount target, double amount) {
        if (amount <= MainMenu.LARGE_TRANSFER_THRESHOLD) {
            return false;
        }
        pendingLargeTransfers.add(new PendingLargeTransfer(account, target, amount));
        System.out.println("This transfer exceeds $" + MainMenu.LARGE_TRANSFER_THRESHOLD
                + " and requires administrator approval. Your request has been submitted.");
        return true;
    }

    private void completeImmediateTransfer(BankAccount account, BankAccount target, double amount) {
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

