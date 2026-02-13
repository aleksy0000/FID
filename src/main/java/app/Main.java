package app;

import accounts.Account;
import accounts.AccountType;
import accounts.Currency;
import db.Db;
import repo.TransactionRepo;
import service.FinanceService;
import transactions.LedgerLine;
import transactions.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public final class Main {

    private Main() {}

    public static void main(String[] args) {
        ensureSchema();

        FinanceService financeService = new FinanceService();
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createAccount(scanner, financeService);
                    case "2" -> listAccounts(financeService);
                    case "3" -> createAndInsertTransaction(scanner);
                    case "0" -> {
                        running = false;
                        System.out.println("Exiting.");
                    }
                    default -> System.out.println("Unknown option. Choose 0, 1, 2, or 3.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=== Finance DB Test CLI ===");
        System.out.println("1) Create account");
        System.out.println("2) List accounts");
        System.out.println("3) Create transaction (+2 ledger lines) and insert");
        System.out.println("0) Exit");
        System.out.print("Choose option: ");
    }

    private static void createAccount(Scanner scanner, FinanceService financeService) {
        System.out.print("Account name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Account type [CURRENT, SAVINGS, LIABILITY]: ");
        AccountType type = AccountType.valueOf(scanner.nextLine().trim().toUpperCase());

        System.out.print("Currency [EUR]: ");
        String currencyInput = scanner.nextLine().trim().toUpperCase();
        Currency currency = currencyInput.isEmpty() ? Currency.EUR : Currency.valueOf(currencyInput);

        Account account = new Account(name, type, currency);
        financeService.accounts().createTable(
                account.getAccID(),
                account.getAccName(),
                account.getAccTypeString(),
                account.getCurrencyString()
        );
        System.out.println("Account inserted with ID: " + account.getAccID());
    }

    private static void listAccounts(FinanceService financeService) {
        List<String> accounts = financeService.accounts().list();
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        System.out.println("Accounts:");
        for (String account : accounts) {
            System.out.println(" - " + account);
        }
    }

    private static void createAndInsertTransaction(Scanner scanner) {
        System.out.println("Tip: use option 2 first to copy account IDs.");
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Debit account ID: ");
        String debitAccountId = scanner.nextLine().trim();
        if (!accountExists(debitAccountId)) {
            throw new IllegalArgumentException("Debit account does not exist: " + debitAccountId);
        }

        System.out.print("Credit account ID: ");
        String creditAccountId = scanner.nextLine().trim();
        if (!accountExists(creditAccountId)) {
            throw new IllegalArgumentException("Credit account does not exist: " + creditAccountId);
        }

        System.out.print("Amount in cents (positive integer): ");
        int amount = Integer.parseInt(scanner.nextLine().trim());
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be > 0.");
        }

        List<LedgerLine> lines = new ArrayList<>();
        lines.add(new LedgerLine(debitAccountId, amount, 0));
        lines.add(new LedgerLine(creditAccountId, 0, amount));

        Transaction transaction = FinanceService.newTransaction(new Date(), description, lines);
        TransactionRepo.addTransactionToDB(transaction);

        System.out.println("Transaction inserted with ID: " + transaction.getTransactionID());
    }

    private static boolean accountExists(String accountId) {
        String sql = "SELECT 1 FROM accounts WHERE accID = ? LIMIT 1";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to validate account ID", e);
        }
    }

    private static void ensureSchema() {
        try (Connection c = Db.connect(); Statement st = c.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    accID TEXT PRIMARY KEY,
                    accName TEXT NOT NULL,
                    accType TEXT NOT NULL,
                    currency TEXT NOT NULL
                );
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    transactionID TEXT PRIMARY KEY NOT NULL,
                    transactionDate TEXT NOT NULL,
                    description TEXT
                );
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS ledgerLines (
                    accID TEXT NOT NULL,
                    transactionID TEXT NOT NULL,
                    debit_amount_cents INT NOT NULL,
                    credit_amount_cents INT NOT NULL,
                    FOREIGN KEY (transactionID) REFERENCES transactions(transactionID) ON DELETE CASCADE,
                    FOREIGN KEY (accID) REFERENCES accounts(accID) ON DELETE CASCADE,
                    CHECK (debit_amount_cents >= 0),
                    CHECK (credit_amount_cents >= 0),
                    CHECK (
                        (debit_amount_cents = 0 AND credit_amount_cents > 0)
                        OR
                        (debit_amount_cents > 0 AND credit_amount_cents = 0)
                    )
                );
            """);

            st.execute("CREATE INDEX IF NOT EXISTS idx_ledgerLines_account ON ledgerLines(accID);");
            st.execute("CREATE INDEX IF NOT EXISTS idx_ledgerLines_transaction ON ledgerLines(transactionID);");
        } catch (SQLException e) {
            throw new RuntimeException("Schema setup failed", e);
        }
    }
}
