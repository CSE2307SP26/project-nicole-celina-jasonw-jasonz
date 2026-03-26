//7. A bank customer should be able to transfer money from one account to another. 
// shortcut for print line: sout

import java.util.ArrayList;
import java.util.Scanner;

public class TransferMoney {
    public static ArrayList<Account> initializeAccountListRandomly(ArrayList<Account> accountList) {
        int numOfAccount = (int) (Math.random() * 5 + 1);
        for (int i = 0; i < numOfAccount; i++) {
            Account testAccount = new Account("test account " + i);
            accountList.add(testAccount);
        }
        System.out.println("Welcome to our bank. As a consumer, you may transfer money between existing accounts.");
        printAccountList(accountList);
        return accountList;
    }

    public static void printAccountList(ArrayList<Account> accountList) {
        System.out.println("Here's the current list of existing accounts:");
        for (int i = 0; i < accountList.size(); i++) {
            System.out.println("[" + i + "] " + accountList.get(i).getName());
        }
    }

    public static int getNumberOfAccounts(ArrayList<Account> accountList) {
        return accountList.size();
    }

    public static int promptForAccountIndex(ArrayList<Account> accountList, boolean continuePrompting) {
        Scanner scanner = new Scanner(System.in);
        String accountIndexInput = scanner.nextLine();
        return convertIndexInputToInteger(accountIndexInput, accountList, continuePrompting);
    }

    public static int convertIndexInputToInteger(String accountIndexInput, ArrayList<Account> accountList,
            boolean continuePrompting) {
        try {
            int accountIndex = Integer.parseInt(accountIndexInput);
            if (accountIndex >= 0 && accountIndex < accountList.size()) {
                return accountIndex;
            } else {
                replyForOutOfBoundIndex(accountIndexInput, accountList, continuePrompting);
            }
        } catch (NumberFormatException e) {
            replyForNonIntegerInput(accountIndexInput, accountList, continuePrompting);
        }
    }


    public static void replyForOutOfBoundIndex(String accountIndexInput, ArrayList<Account> accountList,
            boolean continuePrompting) {
        System.out.println("This index (" + accountIndexInput
                + ") is out of range. Please try again with a valid integer index (0-"
                + (getNumberOfAccounts(accountList) - 1) + ").");
        printAccountList(accountList);
        if (continuePrompting) {
            promptForAccountIndex(accountList, true);
        }
    }

    public static void replyForNonIntegerInput(String accountIndexInput, ArrayList<Account> accountList,
            boolean continuePrompting) {
        System.out.println("Invalid input: Please try again with a valid integer index.");
        printAccountList(accountList);
        if (continuePrompting) {
            promptForAccountIndex(accountList, true);
        }
    }

    public static double promptForTransferAmount(ArrayList<Account> accountList, int sourceAccountIndex, boolean continuePrompting) {
        System.out.println("How much money do you want to transfer? (Enter a positive number): ");
        Scanner scanner = new Scanner(System.in);
        String transferAmountInput = scanner.nextLine();
        double transferAmount = convertAmountInputToDouble(transferAmountInput);
        double sourceAccountBalance = accountList[sourceAccountIndex];
        if (transferAmount < 0 && continuePrompting){
            System.out.println("The transfer amount should be positive.");
            promptForTransferAmount(accountList, sourceAccountIndex, continuePrompting);
        } else if (transferAmount >= sourceAccountBalance && continuePrompting) {

        } else {

        }
    } 

    public static void main(String[] args) {
        ArrayList<Account> accountList = new ArrayList<>();
        initializeAccountListRandomly(accountList);
        System.out.println("Which account do you want to transfer from? (0-"
                + (getNumberOfAccounts(accountList) - 1) + "): ");
        int sourceAccountIndex = promptForAccountIndex(accountList, true);
        double transferAmount = promptForTransferAmount(accountList, sourceAccountIndex, true);
        System.out.println("Which account do you want to transfer to? (0-"
                + (getNumberOfAccounts(accountList) - 1) + "): ");
        int targetAccountIndex = promptForAccountIndex(accountList, true);
        
    }
}
