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
                        accID INTEGER PRIMARY KEY AUTOINCREMENT,
                        accName TEXT NOT NULL,
                        accType TEXT NOT NULL,
                        currency TEXT NOT NULL
                    );
                """);

                //Transactions table, the single source of truth
                st.execute("""
                    CREATE TABLE IF NOT EXISTS transactions(
                        transID INTEGER PRIMARY KEY AUTOINCREMENT,
                        accID INTEGER NOT NULL,
                        amount_cents INTEGER NOT NULL,
                        description TEXT,
                        occurred_at TEXT NOT NULL,
                        FOREIGN KEY (accID) REFERENCES accounts(accID) ON DELETE CASCADE
                    );
                """);

                st.execute("CREATE INDEX IF NOT EXISTS idx_tx_account ON transactions(accID);");
            }
        } catch (Exception e) {
            throw new RuntimeException("DB init failed", e);
        }
    }
}
