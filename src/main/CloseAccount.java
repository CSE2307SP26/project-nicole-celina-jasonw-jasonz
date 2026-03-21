// A bank customer should be able to close an existing account.(Nicole)
// shortcut for print line: sout
import java.util.ArrayList;
import java.util.Scanner;



public class CloseAccount {

    public static ArrayList<Account> initializeAccountListRandomly(ArrayList<Account> accountList){
        int numOfAccount = (int)(Math.random() * 5 + 1);
        for(int i = 0; i < numOfAccount; i++){
            Account testAccount = new Account("test account " + i);
            accountList.add(testAccount);
        }
        System.out.println("Welcome to our bank. As a consumer, you may close an existing account.");
        printAccountList(accountList);
        return accountList;
    }

    public static int getNumberOfAccounts(ArrayList<Account> accountList){
        return accountList.size();
    }

    public static void printAccountList(ArrayList<Account> accountList){
        System.out.println("Here's the current list of existing accounts:");
        for (int i = 0; i < accountList.size(); i++){
            System.out.println("[" + i + "] " + accountList.get(i).getName());
        }
    }

    public static void promptForAccountIndex(ArrayList<Account> accountList, boolean continuePrompting){
        System.out.println("Enter the index of the account you want to close (0-" + (getNumberOfAccounts(accountList) - 1) + "): ");
        Scanner scanner = new Scanner(System.in);
        String accountIndexInput = scanner.nextLine();
        
        convertIndexInputToInteger(accountIndexInput, accountList, continuePrompting);
        
    }

    public static void replyForSuccessfulClosure(int accountIndex, ArrayList<Account> accountList){
        accountList.remove(accountIndex);
        System.out.println("Success! This account is closed.");
        printAccountList(accountList);
    }

    public static void replyForOutOfBoundIndex(String accountIndexInput, ArrayList<Account> accountList, boolean continuePrompting){
        System.out.println("This index (" + accountIndexInput + ") is out of range. Please try again with a valid integer index (0-" + (getNumberOfAccounts(accountList) - 1) + ").");
        printAccountList(accountList);
        if(continuePrompting){
            promptForAccountIndex(accountList, true);
        }
    }

    public static void replyForNonIntegerInput(String accountIndexInput, ArrayList<Account> accountList, boolean continuePrompting){
        System.out.println("Invalid input: Please try again with a valid integer index.");
        printAccountList(accountList);
        if(continuePrompting){
        promptForAccountIndex(accountList, true);
        }
    }

    public static void convertIndexInputToInteger(String accountIndexInput, ArrayList<Account> accountList, boolean continuePrompting){
        try {
            int accountIndex = Integer.parseInt(accountIndexInput);
            if(accountIndex >= 0 && accountIndex < accountList.size()){
                replyForSuccessfulClosure(accountIndex, accountList);
            } else {
                replyForOutOfBoundIndex(accountIndexInput, accountList, continuePrompting);
            }
        } catch (NumberFormatException e) {
            replyForNonIntegerInput(accountIndexInput, accountList, continuePrompting);
        }
    }

    public static void main(String[] args) {
        ArrayList<Account> accountList = new ArrayList<>();
        initializeAccountListRandomly(accountList);
        promptForAccountIndex(accountList, true);
    }
}