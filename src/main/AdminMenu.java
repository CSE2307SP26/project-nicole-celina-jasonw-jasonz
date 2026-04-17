package main;

import java.util.List;
import java.util.Scanner;

class AdminMenu {
    private static final String ADMIN_QUESTION_COLOR = "What is your favorite color?";
    private static final String ADMIN_QUESTION_ANIMAL = "What is your favorite animal?";

    private final List<BankAccount> accounts;
    private final List<PendingLargeTransfer> pendingLargeTransfers;
    private final AccountStorage accountStorage;
    private final AdminStorage adminStorage;
    private final Scanner keyboardInput;
    private final ConsolePrompts prompts;
    private final SystemTime systemTime;
    private final TimeStorage timeStorage;
    private final ScheduledTransferService scheduledTransferService;

    AdminMenu(
            List<BankAccount> accounts,
            List<PendingLargeTransfer> pendingLargeTransfers,
            AccountStorage accountStorage,
            AdminStorage adminStorage,
            Scanner keyboardInput,
            SystemTime systemTime,
            TimeStorage timeStorage,
            ScheduledTransferService scheduledTransferService
    ) {
        this.accounts = accounts;
        this.pendingLargeTransfers = pendingLargeTransfers;
        this.accountStorage = accountStorage;
        this.adminStorage = adminStorage;
        this.keyboardInput = keyboardInput;
        this.prompts = new ConsolePrompts(keyboardInput);
        this.systemTime = systemTime;
        this.timeStorage = timeStorage;
        this.scheduledTransferService = scheduledTransferService;
    }

    void runAdminLoginFlow() {
        if (!adminSetupExists()) {
            runAdminSetupFlow();
            return;
        }
        if (!verifyAdminPassword()) {
            return;
        }
        if (!verifySecurityQuestion()) {
            return;
        }
        System.out.println("Administrator login successful.");
        System.out.println("Current day: Day " + systemTime.getCurrentDay());
        runAdministratorFlow();
    }

    void runPrintAccountsFromFile() {
        List<BankAccount> stored = accountStorage.readAccounts();
        if (stored.isEmpty()) {
            System.out.println("No registered accounts found.");
            return;
        }
        System.out.println();
        System.out.println("Here's a list of registered customer accounts: ");
        System.out.println("------------------------");
        for (BankAccount acc : stored) {
            System.out.println("Username: " + acc.getAccountName());
            System.out.println("Password: " + acc.getAccountPassword());
            System.out.println("------------------------");
        }
    }

    void runAdminDeleteAccount(BankAccount account) {
        if (hasUnresolvedTransfer(account)) {
            System.out.println("Cannot delete this account: large transfer request unresolved.");
            return;
        }
        accounts.remove(account);
        persistAccounts();
        System.out.println("Account [" + account.getAccountName() + "] is deleted.");
    }

    void recordAdminSetup(String adminPassword, String favoriteColorAnswer, String favoriteAnimalAnswer) {
        adminStorage.writeAdminLoginInfo(new AdminLoginInfo(adminPassword, favoriteColorAnswer, favoriteAnimalAnswer));
    }

    boolean checkAdminPassword(String adminPassword) {
        AdminLoginInfo adminInfo = adminStorage.readAdminLoginInfo();
        return adminInfo != null && adminInfo.adminPassword.equals(adminPassword);
    }

    boolean checkAdminAnswer(String question, String answer) {
        AdminLoginInfo adminInfo = adminStorage.readAdminLoginInfo();
        if (adminInfo == null) {
            return false;
        }
        if (question.equals(ADMIN_QUESTION_COLOR)) {
            return adminInfo.favoriteColorAnswer.equalsIgnoreCase(answer);
        }
        if (question.equals(ADMIN_QUESTION_ANIMAL)) {
            return adminInfo.favoriteAnimalAnswer.equalsIgnoreCase(answer);
        }
        return false;
    }

    private void runAdminSetupFlow() {
        System.out.println();
        System.out.println("Detected first time login. Create your administrator password: ");
        String adminPassword = keyboardInput.next();
        System.out.println("You will answer two security questions. One of them will be asked when you login.");
        System.out.println(ADMIN_QUESTION_COLOR);
        String favoriteColorAnswer = keyboardInput.next();
        System.out.println(ADMIN_QUESTION_ANIMAL);
        String favoriteAnimalAnswer = keyboardInput.next();
        if (adminPassword.isEmpty() || favoriteColorAnswer.isEmpty() || favoriteAnimalAnswer.isEmpty()) {
            System.out.println("Password and answer cannot be empty.");
            return;
        }
        recordAdminSetup(adminPassword, favoriteColorAnswer, favoriteAnimalAnswer);
        System.out.println("Administrator login setup is completed.");
    }

    private boolean verifyAdminPassword() {
        System.out.println();
        System.out.println("Enter administrator password: ");
        String adminPassword = keyboardInput.next();
        if (!checkAdminPassword(adminPassword)) {
            System.out.println("Incorrect password. Returning to menu.");
            return false;
        }
        return true;
    }

    private boolean verifySecurityQuestion() {
        String question = getRandomAdminQuestion();
        System.out.println(question);
        String answer = keyboardInput.next();
        if (!checkAdminAnswer(question, answer)) {
            System.out.println("Incorrect answer to security question. Returning to menu.");
            return false;
        }
        return true;
    }

    private void runAdministratorFlow() {
        int top = -1;
        while (top != 6) {
            displayAdministratorTopMenu();
            top = prompts.getUserSelection(6);
            handleAdminTopChoice(top);
        }
    }

    private void displayAdministratorTopMenu() {
        System.out.println();
        System.out.println("Day " + systemTime.getCurrentDay());
        System.out.println("--- Administrator ---");
        System.out.println("1. Select account to manage");
        System.out.println("2. Review pending large transfers");
        System.out.println("3. View account login info");
        System.out.println("4. Delete an account");
        System.out.println("5. Fast-forward time (days)");
        System.out.println("6. Log out");
    }

    private void handleAdminTopChoice(int top) {
        if (top == 1) {
            runAdminAccountSelectionLoop();
        } else if (top == 2) {
            runPendingLargeTransfersReview();
        } else if (top == 3) {
            runPrintAccountsFromFile();
        } else if (top == 4) {
            runAdminDeleteAccountFlow();
        } else if (top == 5) {
            fastForwardTimeFlow();
        }
    }

    private void fastForwardTimeFlow() {
        int days = prompts.promptPositiveInt("Enter number of days to fast-forward: ");
        try {
            systemTime.advanceDays(days);
            processDueLoansForAllAccounts();
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
            persistAccounts();
            System.out.println("Loan repayment processing completed for due accounts.");
        }
    }

    private void runAdminAccountSelectionLoop() {
        while (true) {
            BankAccount selected = promptSelectAccountOrBack();
            if (selected == null) {
                return;
            }
            runAdminAccountActions(selected);
        }
    }

    private BankAccount promptSelectAccountOrBack() {
        printAccountListNumbered(accounts);
        int backIndex = accounts.size() + 1;
        System.out.println("  " + backIndex + ". Back");
        int choice = prompts.getUserSelection(backIndex);
        if (choice == backIndex) {
            return null;
        }
        return accounts.get(choice - 1);
    }

    private void runAdminAccountActions(BankAccount account) {
        int action = -1;
        while (action != 5) {
            displayAdminActionsForAccount(account);
            action = prompts.getUserSelection(5);
            handleAdminAccountAction(action, account);
        }
    }

    private void displayAdminActionsForAccount(BankAccount account) {
        System.out.println();
        System.out.println("Managing: " + account.getAccountName() + " | Balance: " + account.getBalance()
                + (account.isFrozen() ? " | FROZEN" : ""));
        System.out.println("1. Collect fee");
        System.out.println("2. Apply interest payment (rate % × principal × period)");
        System.out.println("3. Freeze account");
        System.out.println("4. Unfreeze account");
        System.out.println("5. Back to account list");
    }

    private void handleAdminAccountAction(int action, BankAccount account) {
        if (action == 1) {
            performCollectFee(account);
        } else if (action == 2) {
            performInterestPayment(account);
        } else if (action == 3) {
            performFreezeAccount(account);
        } else if (action == 4) {
            performUnfreezeAccount(account);
        }
    }

    private void performCollectFee(BankAccount account) {
        double fee = prompts.promptNonNegativeAmount("Fee amount to collect: ");
        if (fee == 0) {
            System.out.println("No fee collected.");
            return;
        }
        try {
            account.collectFee(fee);
            persistAccounts();
            System.out.println("Fee collected. New balance: " + account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void performInterestPayment(BankAccount account) {
        if (!hasPositivePrincipalForInterest(account)) {
            return;
        }
        printInterestCalculationGuidance(account.getBalance());
        double annualRate = prompts.promptNonNegativeAmount(
                "Annual interest rate (% per year), e.g. 3 for 3%: ");
        int months = prompts.promptPositiveInt("Number of months in this accrual period: ");
        if (annualRate == 0) {
            System.out.println("Rate is 0% — no interest credited.");
            return;
        }
        applyInterestAndPersist(account, annualRate, months);
    }

    private boolean hasPositivePrincipalForInterest(BankAccount account) {
        if (account.getBalance() <= 0) {
            System.out.println("No principal balance — no interest can accrue.");
            return false;
        }
        return true;
    }

    private void printInterestCalculationGuidance(double principal) {
        System.out.println("Principal (current balance): " + principal);
        System.out.println("Interest = principal × (annual rate % / 100) × (months / 12).");
    }

    private void applyInterestAndPersist(BankAccount account, double annualRate, int months) {
        try {
            double credited = account.applyInterestPayment(annualRate, months);
            persistAccounts();
            System.out.println("Interest credited: " + credited);
            System.out.println("New balance: " + account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid rate or period.");
        }
    }

    private void performFreezeAccount(BankAccount account) {
        if (account.isFrozen()) {
            System.out.println("This account is already frozen.");
            return;
        }
        account.setFrozen(true);
        persistAccounts();
        System.out.println("Account \"" + account.getAccountName() + "\" is now frozen.");
    }

    private void performUnfreezeAccount(BankAccount account) {
        if (!account.isFrozen()) {
            System.out.println("This account is not frozen.");
            return;
        }
        account.setFrozen(false);
        persistAccounts();
        System.out.println("Account \"" + account.getAccountName() + "\" is now unfrozen.");
    }

    private void runPendingLargeTransfersReview() {
        while (true) {
            if (exitReviewIfPendingQueueEmpty()) {
                return;
            }
            int idx = promptPendingTransferIndex();
            if (idx < 0) {
                return;
            }
            PendingLargeTransfer selected = pendingLargeTransfers.get(idx);
            dispatchPendingTransferAdminAction(selected);
        }
    }

    private boolean exitReviewIfPendingQueueEmpty() {
        if (pendingLargeTransfers.isEmpty()) {
            System.out.println("No pending large transfer requests.");
            return true;
        }
        return false;
    }

    private void printApproveDenyCancelMenu() {
        System.out.println("1. Approve");
        System.out.println("2. Deny");
        System.out.println("3. Cancel");
    }

    private void dispatchPendingTransferAdminAction(PendingLargeTransfer selected) {
        printApproveDenyCancelMenu();
        int action = prompts.getUserSelection(3);
        if (action == 1) {
            approvePendingLargeTransfer(selected);
        } else if (action == 2) {
            denyPendingLargeTransfer(selected);
        }
    }

    private int promptPendingTransferIndex() {
        System.out.println();
        System.out.println("--- Pending large transfers (over $" + MainMenu.LARGE_TRANSFER_THRESHOLD + ") ---");
        for (int i = 0; i < pendingLargeTransfers.size(); i++) {
            PendingLargeTransfer p = pendingLargeTransfers.get(i);
            System.out.println((i + 1) + ". " + p.from.getAccountName() + " -> " + p.to.getAccountName()
                    + " | $" + p.amount);
        }
        int backIndex = pendingLargeTransfers.size() + 1;
        System.out.println("  " + backIndex + ". Back");
        int choice = prompts.getUserSelection(backIndex);
        if (choice == backIndex) {
            return -1;
        }
        return choice - 1;
    }

    private void approvePendingLargeTransfer(PendingLargeTransfer p) {
        int idx = pendingLargeTransfers.indexOf(p);
        if (!isStillPendingTransferIndex(idx)) {
            return;
        }
        if (hasFrozenAccountInPendingTransfer(p)) {
            return;
        }
        if (removePendingTransferIfInsufficientBalance(p, idx)) {
            return;
        }
        completeApprovedLargeTransferSafely(p);
    }

    private boolean isStillPendingTransferIndex(int idx) {
        if (idx < 0) {
            System.out.println("That request is no longer pending.");
            return false;
        }
        return true;
    }

    private boolean hasFrozenAccountInPendingTransfer(PendingLargeTransfer p) {
        if (p.from.isFrozen() || p.to.isFrozen()) {
            System.out.println("Cannot approve: one or both accounts are frozen.");
            return true;
        }
        return false;
    }

    private boolean removePendingTransferIfInsufficientBalance(PendingLargeTransfer p, int idx) {
        if (p.from.getBalance() < p.amount) {
            System.out.println("Cannot approve: insufficient balance. Request removed.");
            pendingLargeTransfers.remove(idx);
            return true;
        }
        return false;
    }

    private void completeApprovedLargeTransferSafely(PendingLargeTransfer p) {
        try {
            p.from.withdraw(p.amount);
            p.from.recordTransaction("Transfer Out", -p.amount);
            p.to.deposit(p.amount);
            p.to.recordTransaction("Transfer In", p.amount);
            pendingLargeTransfers.remove(p);
            persistAccounts();
            System.out.println("Large transfer approved and completed.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    private void denyPendingLargeTransfer(PendingLargeTransfer p) {
        if (pendingLargeTransfers.remove(p)) {
            System.out.println("Request denied and removed.");
        }
    }

    private void runAdminDeleteAccountFlow() {
        runPrintAccountsFromFile();
        System.out.println("Select the account to delete by username: ");
        String username = keyboardInput.next();
        BankAccount toDelete = findAccountByUsername(username);
        if (toDelete == null) {
            System.out.println("Account not found.");
            return;
        }
        runAdminDeleteAccount(toDelete);
    }

    private BankAccount findAccountByUsername(String username) {
        for (BankAccount acc : accounts) {
            if (acc.getAccountName().equals(username)) {
                return acc;
            }
        }
        return null;
    }

    private boolean hasUnresolvedTransfer(BankAccount account) {
        for (PendingLargeTransfer p : pendingLargeTransfers) {
            if (p.from == account || p.to == account) {
                return true;
            }
        }
        return false;
    }

    private boolean adminSetupExists() {
        return adminStorage.readAdminLoginInfo() != null;
    }

    private String getRandomAdminQuestion() {
        int questionNumber = (int) (Math.random() * 2);
        if (questionNumber == 0) {
            return ADMIN_QUESTION_COLOR;
        }
        return ADMIN_QUESTION_ANIMAL;
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

    private void persistAccounts() {
        accountStorage.writeAccounts(accounts);
    }
}

