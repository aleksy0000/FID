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
                    case "4" -> listTransactions();
                    case "0" -> {
                        running = false;
                        System.out.println("Exiting.");
                    }
                    default -> System.out.println("Unknown option. Choose 0, 1, 2, 3, or 4.");
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
        System.out.println("4) List transactions");
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

    private static void listTransactions() {
        String txSql = """
            SELECT transactionID, transactionDate, description
            FROM transactions
            ORDER BY transactionDate DESC, transactionID DESC
            """;
        String linesSql = """
            SELECT l.accID,
                   a.accName,
                   a.accType,
                   a.currency,
                   l.debit_amount_cents,
                   l.credit_amount_cents
            FROM ledgerLines l
            LEFT JOIN accounts a ON a.accID = l.accID
            WHERE l.transactionID = ?
            ORDER BY l.rowid
            """;

        try (Connection c = Db.connect();
             PreparedStatement txStmt = c.prepareStatement(txSql);
             ResultSet txRs = txStmt.executeQuery();
             PreparedStatement linesStmt = c.prepareStatement(linesSql)) {

            boolean any = false;
            while (txRs.next()) {
                any = true;
                String transactionId = txRs.getString("transactionID");
                String transactionDate = txRs.getString("transactionDate");
                String description = txRs.getString("description");
                long totalDebit = 0;
                long totalCredit = 0;

                System.out.println();
                System.out.println("Transaction: " + transactionId);
                System.out.println("Date: " + transactionDate);
                System.out.println("Description: " + (description == null || description.isBlank() ? "(none)" : description));
                System.out.println("Lines:");

                linesStmt.setString(1, transactionId);
                try (ResultSet linesRs = linesStmt.executeQuery()) {
                    while (linesRs.next()) {
                        int debit = linesRs.getInt("debit_amount_cents");
                        int credit = linesRs.getInt("credit_amount_cents");
                        totalDebit += debit;
                        totalCredit += credit;

                        String accountId = linesRs.getString("accID");
                        String accountName = linesRs.getString("accName");
                        String accountType = linesRs.getString("accType");
                        String currency = linesRs.getString("currency");

                        String resolvedName = (accountName == null || accountName.isBlank()) ? "(unknown account)" : accountName;
                        String resolvedType = (accountType == null || accountType.isBlank()) ? "-" : accountType;
                        String resolvedCurrency = (currency == null || currency.isBlank()) ? "-" : currency;

                        System.out.println(
                                " - " + resolvedName +
                                        " [id=" + accountId + ", type=" + resolvedType + ", currency=" + resolvedCurrency + "]" +
                                        " | debit=" + debit +
                                        " | credit=" + credit
                        );
                    }
                }
                System.out.println("Totals | debit=" + totalDebit + " | credit=" + totalCredit);
            }

            if (!any) {
                System.out.println("No transactions found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list transactions", e);
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
