package main;

import java.util.Scanner;

public class MainMenu {

    private static final int ROLE_CUSTOMER = 1;
    private static final int ROLE_ADMINISTRATOR = 2;
    private static final int ROLE_EXIT = 3;
    private static final int MAX_ROLE_SELECTION = 3;

    private static final int CUSTOMER_DEPOSIT = 1;
    private static final int CUSTOMER_EXIT_TO_ROLE = 2;
    private static final int MAX_CUSTOMER_SELECTION = 2;

    private static final int ADMIN_SEARCH = 1;
    private static final int ADMIN_BACK_TO_ROLE = 2;
    private static final int MAX_ADMIN_SELECTION = 2;

    private BankAccount userAccount;
    private Scanner keyboardInput;

    public MainMenu() {
        this.userAccount = new BankAccount();
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

    public void displayCustomerOptions() {
        System.out.println();
        System.out.println("--- Customer ---");
        System.out.println("1. Make a deposit");
        System.out.println("2. Return to role selection");
    }

    public void displayAdministratorOptions() {
        System.out.println();
        System.out.println("--- Administrator ---");
        System.out.println("1. Search Customer / Account");
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

    public void processCustomerInput(int selection) {
        switch (selection) {
            case CUSTOMER_DEPOSIT:
                performDeposit();
                break;
            default:
                break;
        }
    }

    public void processAdministratorInput(int selection) {
        switch (selection) {
            case ADMIN_SEARCH:
                System.out.println("Search Customer / Account is not yet implemented.");
                break;
            default:
                break;
        }
    }

    public void performDeposit() {
        double depositAmount = -1;
        while (depositAmount < 0) {
            System.out.print("How much would you like to deposit: ");
            depositAmount = keyboardInput.nextInt();
        }
        userAccount.deposit(depositAmount);
    }

    public void runCustomerFlow() {
        int selection = -1;
        while (selection != CUSTOMER_EXIT_TO_ROLE) {
            displayCustomerOptions();
            selection = getUserSelection(MAX_CUSTOMER_SELECTION);
            processCustomerInput(selection);
        }
    }

    public void runAdministratorFlow() {
        int selection = -1;
        while (selection != ADMIN_BACK_TO_ROLE) {
            displayAdministratorOptions();
            selection = getUserSelection(MAX_ADMIN_SELECTION);
            processAdministratorInput(selection);
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
