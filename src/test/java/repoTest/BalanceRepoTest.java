package repoTest;

import db.Db;
import org.junit.jupiter.api.*;
import repo.BalanceRepo;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class BalanceRepoTest {

    @BeforeEach
    void setUp() throws Exception {
        // Create a fresh ledgerLines table for testing
        try (Connection c = Db.connect(); Statement stmt = c.createStatement()) {
            // Drop the table if it exists to start clean
            stmt.execute("DROP TABLE IF EXISTS ledgerLines");

            // Create ledgerLines table
            stmt.execute("""
                    CREATE TABLE ledgerLines(
                        accID VARCHAR(50),
                        debit_amount_cents INT,
                        credit_amount_cents INT
                    )
                    """);

            // Insert sample data
            stmt.execute("INSERT INTO ledgerLines(accID, debit_amount_cents, credit_amount_cents) VALUES ('001', 1000, 200)");
            stmt.execute("INSERT INTO ledgerLines(accID, debit_amount_cents, credit_amount_cents) VALUES ('001', 500, 100)");
            stmt.execute("INSERT INTO ledgerLines(accID, debit_amount_cents, credit_amount_cents) VALUES ('002', 2000, 1500)");
        }
    }

    @Test
    void testGetAccountBalanceExistingAccount() {
        // Account 001: balance = (1000 + 500) - (200 + 100) = 1200
        int balance = BalanceRepo.getAccountBalance("001");
        assertEquals(1200, balance);

        // Account 002: balance = 2000 - 1500 = 500
        int balance2 = BalanceRepo.getAccountBalance("002");
        assertEquals(500, balance2);
    }

    @Test
    void testGetAccountBalanceNoTransactions() {
        // Account 003 does not exist, should return 0
        int balance = BalanceRepo.getAccountBalance("003");
        assertEquals(0, balance);
    }

    @Test
    void testGetAccountBalanceAllCredits() throws Exception {
        // Insert a new account with only credits
        try (Connection c = Db.connect(); Statement stmt = c.createStatement()) {
            stmt.execute("INSERT INTO ledgerLines(accID, debit_amount_cents, credit_amount_cents) VALUES ('004', 0, 500)");
        }
        int balance = BalanceRepo.getAccountBalance("004");
        // Balance should be negative if credits > debits: 0 - 500 = -500
        assertEquals(-500, balance);
    }

    @Test
    void testGetAccountBalanceAllDebits() throws Exception {
        // Insert a new account with only debits
        try (Connection c = Db.connect(); Statement stmt = c.createStatement()) {
            stmt.execute("INSERT INTO ledgerLines(accID, debit_amount_cents, credit_amount_cents) VALUES ('005', 800, 0)");
        }
        int balance = BalanceRepo.getAccountBalance("005");
        assertEquals(800, balance);
    }
}