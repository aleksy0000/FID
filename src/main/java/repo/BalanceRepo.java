/*
    BalanceRepo.java

    Purpose -> BalanceRepo.java handles all operations related to account balance.

    Functionality:
    - getAccountBalance(String accID) -> returns balance of account with the same accID that was passed to the method - used for quick lookup of balance
 */
package repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.Db;

public class BalanceRepo {
    private final Db db;

    public BalanceRepo(Db db) {
        this.db = db;
    }

    //return account balance from database
    public int getAccountBalance(String accID){
        String getAccountBalanceSql = """
            SELECT accID, sum(debit_amount_cents) - sum(credit_amount_cents) AS balance
            FROM ledgerLines
            WHERE accID = ?
            GROUP BY accID;
        """;

        try (Connection conn = db.connect();
             PreparedStatement stmt = conn.prepareStatement(getAccountBalanceSql)) {

            stmt.setString(1, accID); // bind parameter

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("balance");
                } else {
                    return 0; // account has no transactions yet
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 0; // or throw a runtime exception
        }
    }
}
