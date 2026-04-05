package db;

import java.nio.file.*;
import java.sql.*;

/**
 * Handles database connection and schema initialisation.
 *
 * Usage:
 * Db db = new Db("db_folder","db_file");
 * db.init();
 * Connection conn = db.connect();
 */
public class Db {
    private String DB_DIR;
    private String URL;

    public Db(String DB_DIR, String DB_FILE){
        DB_DIR = DB_DIR;
        URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;
    }

    /**
     * <p> This method connects us to the database</p>
     *
     * @return SQLlite connection
     * @throws SQLException
     */
    public Connection connect() throws SQLException {

        Connection c = DriverManager.getConnection(URL);
        try (Statement st = c.createStatement()){
            st.execute("PRAGMA foreign_keys = ON;");
            st.execute("PRAGMA journal_mode = wal;");
            st.execute("PRAGMA synchronous = NORMAL;");
        }
        return c;
    }

    /**
     * <p> Initialises Schema </p>
     */
    public void init(){
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
                        description TEXT
                    );
                """);

                st.execute("""
                    CREATE TABLE IF NOT EXISTS ledgerLines(
                        accID TEXT,
                        transactionID TEXT,
                        debit_amount_cents INT,
                        credit_amount_cents INT,
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
                st.execute("CREATE INDEX IF NOT EXISTS idx_ledgerLines_trans ON ledgerLines(transactionID);");
            }
        } catch (Exception e) {
            throw new RuntimeException("DB init failed", e);
        }
    }
}
