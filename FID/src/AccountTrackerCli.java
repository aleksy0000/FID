import accTrack.Account;
import accTrack.AccountTracker;

import java.util.List;
import java.util.Scanner;

public class AccountTrackerCli {
    private final AccountTracker tracker = new AccountTracker();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        System.out.println("Account Tracker CLI");
        System.out.println("Type a menu number to continue.");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Select option: ");
            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    listAccounts();
                    break;
                case 3:
                    showAccount();
                    break;
                case 4:
                    deposit();
                    break;
                case 5:
                    withdraw();
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    System.out.println("Unknown option.");
                    break;
            }
        }
        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1) Create account");
        System.out.println("2) List accounts");
        System.out.println("3) Show account by ID");
        System.out.println("4) Deposit");
        System.out.println("5) Withdraw");
        System.out.println("6) Exit");
    }

    private void createAccount() {
        String name = readLine("Account name: ");
        int type = readInt("Account type (1=current, 2=savings): ");
        double balance = readDouble("Starting balance: ");
        int id = tracker.createNewAccount(name, type, balance);
        System.out.println("Created account ID " + id + ".");
    }

    private void listAccounts() {
        List<Account> accounts = tracker.getAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts.");
            return;
        }
        for (Account account : accounts) {
            System.out.println(
                "#" + account.getAccID()
                    + " " + account.getAccName()
                    + " | " + account.getAccType()
                    + " | balance " + formatMoney(account.getBalance())
            );
        }
    }

    private void showAccount() {
        int id = readInt("Account ID: ");
        Account account = tracker.getAccountById(id);
        if (account == null) {
            System.out.println("No account with ID " + id + ".");
            return;
        }
        System.out.println("Account: " + account.getAccName());
        System.out.println("Type: " + account.getAccType());
        System.out.println("Balance: " + formatMoney(account.getBalance()));
    }

    private void deposit() {
        int id = readInt("Account ID: ");
        Account account = tracker.getAccountById(id);
        if (account == null) {
            System.out.println("No account with ID " + id + ".");
            return;
        }
        double amount = readDouble("Deposit amount: ");
        account.deposit(amount);
        System.out.println("New balance: " + formatMoney(account.getBalance()));
    }

    private void withdraw() {
        int id = readInt("Account ID: ");
        Account account = tracker.getAccountById(id);
        if (account == null) {
            System.out.println("No account with ID " + id + ".");
            return;
        }
        double amount = readDouble("Withdraw amount: ");
        account.withdraw(amount);
        System.out.println("New balance: " + formatMoney(account.getBalance()));
    }

    private int readInt(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Enter a whole number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Enter a number.");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private String formatMoney(double value) {
        return String.format("%.2f", value);
    }
}
