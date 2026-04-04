package repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static db.Db.connect;
import static java.sql.DriverManager.getConnection;

public class BalanceRepo {
    //return account balance from database
    public static int getAccountBalance(String accID){
        String getAccountBalanceSql = """
            SELECT accID, sum(debit_amount_cents) - sum(credit_amount_cents) AS balance
            FROM ledgerLines
            WHERE accID = ?
            GROUP BY accID;
        """;

        try (Connection conn = connect();
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
