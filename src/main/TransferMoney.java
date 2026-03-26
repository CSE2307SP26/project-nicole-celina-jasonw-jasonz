//7. A bank customer should be able to transfer money from one account to another. 
// shortcut for print line: sout

import java.util.ArrayList;
import java.util.Scanner;

public class TransferMoney {
    private static final Scanner scanner = new Scanner(System.in);

    public static ArrayList<Account> initializeAccountListRandomly(ArrayList<Account> accountList) {
        int numOfAccount = (int) (Math.random() * 5 + 1);

        for (int i = 0; i < numOfAccount; i++) {
            Account testAccount = new Account("test account " + i);

            // Optional: give each account some starting money
            testAccount.deposit((i + 1) * 100);

            accountList.add(testAccount);
        }

        System.out.println("Welcome to our bank. As a consumer, you may transfer money between existing accounts.");
        printAccountList(accountList);
        return accountList;
    }

    public static void printAccountList(ArrayList<Account> accountList) {
        System.out.println("Here's the current list of existing accounts:");
        for (int i = 0; i < accountList.size(); i++) {
            Account account = accountList.get(i);
            System.out.println("[" + i + "] " + account.getName() + " | Balance: $" + account.getBalance());
        }
    }

    public static int getNumberOfAccounts(ArrayList<Account> accountList) {
        return accountList.size();
    }

    public static int promptForAccountIndex(ArrayList<Account> accountList, String promptMessage) {
        while (true) {
            System.out.println(promptMessage);
            String accountIndexInput = scanner.nextLine();
            int result = convertIndexInputToInteger(accountIndexInput, accountList);

            if (result != -1) {
                return result;
            }
        }
    }

    public static int convertIndexInputToInteger(String accountIndexInput, ArrayList<Account> accountList) {
        try {
            int accountIndex = Integer.parseInt(accountIndexInput);

            if (accountIndex >= 0 && accountIndex < accountList.size()) {
                return accountIndex;
            } else {
                System.out.println("This index (" + accountIndexInput + ") is out of range. Please try again with a valid integer index (0-"
                        + (getNumberOfAccounts(accountList) - 1) + ").");
                printAccountList(accountList);
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: Please try again with a valid integer index.");
            printAccountList(accountList);
            return -1;
        }
    }

    public static double promptForTransferAmount(ArrayList<Account> accountList, int sourceAccountIndex) {
        while (true) {
            System.out.println("How much money do you want to transfer? (Enter a positive number): ");
            String transferAmountInput = scanner.nextLine();
            double result = convertAmountInputToDouble(transferAmountInput, sourceAccountIndex, accountList);

            if (result != -1) {
                return result;
            }
        }
    }

    public static double convertAmountInputToDouble(
            String transferAmountInput,
            int sourceAccountIndex,
            ArrayList<Account> accountList) {

        double sourceAccountBalance = accountList.get(sourceAccountIndex).getBalance();

        try {
            double transferAmount = Double.parseDouble(transferAmountInput);

            if (transferAmount > 0 && transferAmount <= sourceAccountBalance) {
                return transferAmount;
            } else {
                System.out.println("The transfer amount must be greater than 0 and no more than the source account balance.");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: Please enter a valid number.");
            return -1;
        }
    }

    public static void performTransfer(
            ArrayList<Account> accountList,
            int sourceAccountIndex,
            int targetAccountIndex,
            double transferAmount) {

        Account sourceAccount = accountList.get(sourceAccountIndex);
        Account targetAccount = accountList.get(targetAccountIndex);

        if (sourceAccountIndex == targetAccountIndex) {
            System.out.println("You cannot transfer money to the same account.");
            return;
        }

        sourceAccount.withdraw(transferAmount);
        targetAccount.deposit(transferAmount);

        System.out.println("Transfer successful.");
        System.out.println(sourceAccount.getName() + " new balance: $" + sourceAccount.getBalance());
        System.out.println(targetAccount.getName() + " new balance: $" + targetAccount.getBalance());
    }

    public static void main(String[] args) {
        ArrayList<Account> accountList = new ArrayList<>();
        initializeAccountListRandomly(accountList);

        int sourceAccountIndex = promptForAccountIndex(
                accountList,
                "Which account do you want to transfer from? (0-" + (getNumberOfAccounts(accountList) - 1) + "): ");

        double transferAmount = promptForTransferAmount(accountList, sourceAccountIndex);

        int targetAccountIndex = promptForAccountIndex(
                accountList,
                "Which account do you want to transfer into? (0-" + (getNumberOfAccounts(accountList) - 1) + "): ");

        performTransfer(accountList, sourceAccountIndex, targetAccountIndex, transferAmount);

        System.out.println();
        System.out.println("Updated account list:");
        printAccountList(accountList);
    }
}