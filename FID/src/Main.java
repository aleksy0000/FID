import db.Db;
import service.FinanceService;

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
        0) Exit
      """);
            System.out.print("> ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.print("Account name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Account Type(CURRENT OR SAVINGS:");
                    String type = sc.nextLine().trim().toUpperCase();
                    System.out.print("Currency (e.g. EUR): ");
                    String cur = sc.nextLine().trim().toUpperCase();
                    long id = svc.createAccount(name, type ,cur);
                    System.out.println("Created account id=" + id);
                }
                case "2" -> svc.accounts().list().forEach(System.out::println);
                case "3" -> {
                    System.out.print("Account id: ");
                    long accId = Long.parseLong(sc.nextLine().trim());
                    System.out.print("Amount (e.g. -12.50): ");
                    double amount = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Description: ");
                    String desc = sc.nextLine().trim();
                    long txId = svc.addTransaction(accId, amount, desc);
                    System.out.println("Created transaction id=" + txId);
                }
                case "4" -> {
                    System.out.print("Account id: ");
                    long accId = Long.parseLong(sc.nextLine().trim());
                    svc.transactions().listForAccount(accId).forEach(System.out::println);
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
