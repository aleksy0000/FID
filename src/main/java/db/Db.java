package db;

import java.nio.file.*;
import java.sql.*;

public class Db {
    private static final String DB_DIR = "data";
    private static final String DB_FILE = "app.db";
    private static final String URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;

    private Db(){}

    //connect to db
    public static Connection connect() throws SQLException {

        Connection c = DriverManager.getConnection(URL);
        try (Statement st = c.createStatement()){
            st.execute("PRAGMA foreign_keys = ON;");
            st.execute("PRAGMA journal_mode = wal;");
            st.execute("PRAGMA synchronous = NORMAL;");
        }
        return c;
    }

    //initialise schema
    public static void init(){
        try{
            Files.createDirectories(Paths.get(DB_DIR));
            try(Connection c = connect(); Statement st = c.createStatement()){

                //accounts table, we are not storing balance here, we derive balance from transactions
                st.execute("""
                    CREATE TABLE IF NOT EXISTS accounts (
                        accID TEXT PRIMARY KEY,
                        accName TEXT NOT NULL,
                        accType TEXT NOT NULL,
                        currency TEXT NOT NULL
                    );
                """);

                //Transactions table, the single source of truth, total debits must always equal total credits
                st.execute("""
                    CREATE TABLE IF NOT EXISTS transactions(
                        transactionID TEXT PRIMARY KEY NOT NULL,
                        transactionDate DATE NOT NULL,
                        description TEXT,
                    );
                """);

                st.execute("""
                    CREATE TABLE IF NOT EXISTS ledgerLines(
                        accID TEXT,
                        transactionID TEXT,
                        debit_amount_cents INT,
                        credit_amount_cents INT,
                        FOREIGN KEY (transactionID) REFERENCES transactions(transactionID) ON DELETE CASCADE,
                        FOREIGN KEY (accID) REFERENCES accounts(accID) ON DELETE CASCADE
                        
                        CHECK (debit_amount_cents >= 0),
                        CHECK (credit_amount_cents >= 0),
                        
                        CHECK (
                            (debit_amount_cents = 0 AND credit_amount_cents > 0)
                            OR
                            (debit_amount_cents > 0 AND credit_amount_cents = 0)
                        )
                    );
                """);

                st.execute("CREATE INDEX IF NOT EXISTS idx_entries_account ON entries(accID);");
                st.execute("CREATE INDEX IF NOT EXISTS idx_entries_trans ON entries(transID);");
            }
        } catch (Exception e) {
            throw new RuntimeException("DB init failed", e);
        }
    }
}
