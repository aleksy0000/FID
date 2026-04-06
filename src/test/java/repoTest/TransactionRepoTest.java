package repo;

import db.Db;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import transactions.Transaction;
import transactions.LedgerLine;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepoTest {
    // AI GENERATED CODE
    @TempDir
    Path tempDir;

    private Db db;
    private TransactionRepo transactionRepo;

    @BeforeEach
    void setUp() throws Exception {
        db = new Db(tempDir, "transaction-test.db");
        transactionRepo = new TransactionRepo(db);

        try (Connection c = db.connect(); Statement stmt = c.createStatement()) {
            // Drop tables if they exist to start clean
            stmt.execute("DROP TABLE IF EXISTS ledgerLines");
            stmt.execute("DROP TABLE IF EXISTS transactions");

            // Create transactions table
            stmt.execute("""
                    CREATE TABLE transactions(
                        transactionID VARCHAR(50) PRIMARY KEY,
                        transactionDate VARCHAR(50),
                        description VARCHAR(255)
                    )
                    """);

            // Create ledgerLines table
            stmt.execute("""
                    CREATE TABLE ledgerLines(
                        accID VARCHAR(50),
                        transactionID VARCHAR(50),
                        debit_amount_cents INT,
                        credit_amount_cents INT
                    )
                    """);
        }
    }

    @Test
    void testAddTransactionToDB() {
        // Create sample ledger lines
        LedgerLine line1 = new LedgerLine("001", 1000, 0);
        LedgerLine line2 = new LedgerLine("002", 0, 1000);

        // Compute totals required by constructor
        long totalDebit = line1.debit_amount_cents() + line2.debit_amount_cents();
        long totalCredit = line1.credit_amount_cents() + line2.credit_amount_cents();

        // Create a transaction object with the correct constructor
        Transaction tx = new Transaction(
                new Date(),                // transactionDate
                "TX001",                   // description
                List.of(line1, line2),     // ledger lines
                totalDebit,                // total debit
                totalCredit                // total credit
        );

        // Insert transaction into DB
        transactionRepo.addTransactionToDB(tx);

        // Verify transaction record exists
        try (Connection c = db.connect();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM transactions WHERE transactionID='" + tx.getTransactionID() + "'")) {

            assertTrue(rs.next(), "Transaction record should exist");
            assertEquals(tx.getTransactionID(), rs.getString("transactionID"));
            assertEquals("TX001", rs.getString("description"));
        } catch (Exception e) {
            fail("Exception verifying transactions table: " + e.getMessage());
        }

        // Verify ledger lines were inserted correctly
        try (Connection c = db.connect();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ledgerLines WHERE transactionID='" + tx.getTransactionID() + "'")) {

            int count = 0;
            while (rs.next()) {
                String accID = rs.getString("accID");
                int debit = rs.getInt("debit_amount_cents");
                int credit = rs.getInt("credit_amount_cents");

                if (accID.equals("001")) {
                    assertEquals(1000, debit);
                    assertEquals(0, credit);
                } else if (accID.equals("002")) {
                    assertEquals(0, debit);
                    assertEquals(1000, credit);
                } else {
                    fail("Unexpected account ID in ledger lines: " + accID);
                }
                count++;
            }
            assertEquals(2, count, "Should insert exactly 2 ledger lines");
        } catch (Exception e) {
            fail("Exception verifying ledgerLines table: " + e.getMessage());
        }
    }

    @Test
    void testAddTransactionToDBUnbalancedThrows() {
        // Ledger lines that are unbalanced
        LedgerLine line1 = new LedgerLine("001", 1000, 0);
        LedgerLine line2 = new LedgerLine("002", 0, 500);

        long totalDebit = line1.debit_amount_cents() + line2.debit_amount_cents();   // 1000
        long totalCredit = line1.credit_amount_cents() + line2.credit_amount_cents(); // 500

        Transaction tx = new Transaction(
                new Date(),
                "TX002",
                List.of(line1, line2),
                totalDebit,
                totalCredit
        );

        assertThrows(IllegalStateException.class, () -> transactionRepo.addTransactionToDB(tx));
    }

    @Test
    void testAddTransactionToDBDuplicateTransactionThrows() {
        LedgerLine line1 = new LedgerLine("001", 1000, 0);
        LedgerLine line2 = new LedgerLine("002", 0, 1000);

        Transaction tx = new Transaction(
                new Date(),
                "TX003",
                List.of(line1, line2),
                1000,
                1000
        );

        transactionRepo.addTransactionToDB(tx);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionRepo.addTransactionToDB(tx));
        assertEquals("Duplicate transaction detected", exception.getMessage());
    }
}
