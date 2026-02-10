package app;

import db.Db;
import io.CSVHandler;
import service.FinanceService;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public final class Main {
    public static void main(String[] args) {
        Db.init();
        FinanceService svc = new FinanceService();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("""
        \n=== Finance CLI ===
        1) Create account
        2) List accounts
        3) Add transaction
        4) List transactions for account
        5) Import transactions from CSV
        9) Reset database (drop and re-init)
        0) Exit
      """);
            System.out.print("> ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.print("Account name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Account Type (CURRENT, SAVINGS, LIABILITY): ");
                    String typeRaw = sc.nextLine().trim().toUpperCase();
                    System.out.print("Currency (e.g. EUR): ");
                    String curRaw = sc.nextLine().trim().toUpperCase();
                    try {
                        svc.createAccount(
                                name,
                                accounts.AccountType.valueOf(typeRaw),
                                accounts.Currency.valueOf(curRaw)
                        );
                        System.out.println("Created account.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid account type or currency.");
                    }
                }
                case "2" -> svc.accounts().list().forEach(System.out::println);
                case "3" -> {
                    System.out.print("Account id: ");
                    String accId = sc.nextLine().trim();
                    System.out.print("Counter account id: ");
                    String counterAccId = sc.nextLine().trim();
                    System.out.print("Amount (e.g. -12.50): ");
                    double amount = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Description: ");
                    String desc = sc.nextLine().trim();
                    long txId = svc.addTransaction(accId, counterAccId, amount, desc);
                    System.out.println("Created transaction id=" + txId);
                }
                case "4" -> {
                    System.out.print("Account id: ");
                    String accId = sc.nextLine().trim();
                    svc.transactions().listForAccount(accId).forEach(System.out::println);
                }
                case "5" -> {
                    System.out.print("Account id: ");
                    String accId = sc.nextLine().trim();
                    System.out.print("Income account id: ");
                    String incomeAccId = sc.nextLine().trim();
                    System.out.print("Expense account id: ");
                    String expenseAccId = sc.nextLine().trim();
                    System.out.print("CSV path (e.g. data/transactions_test.csv): ");
                    Path csvPath = Path.of(sc.nextLine().trim());
                    CSVHandler.readBankStatement(csvPath, accId, incomeAccId, expenseAccId);
                    System.out.println("CSV import finished.");
                }
                case "9" -> {
                    System.out.print("Type RESET to confirm: ");
                    String confirm = sc.nextLine().trim();
                    if (!"RESET".equals(confirm)) {
                        System.out.println("Reset cancelled.");
                        break;
                    }
                    try (Connection c = Db.connect(); Statement st = c.createStatement()) {
                        st.execute("DROP TABLE IF EXISTS transactions;");
                        st.execute("DROP TABLE IF EXISTS accounts;");
                    } catch (Exception e) {
                        throw new RuntimeException("DB reset failed", e);
                    }
                    Db.init();
                    System.out.println("Database reset completed.");
                }
                case "0" -> {
                    System.out.println("Bye.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
