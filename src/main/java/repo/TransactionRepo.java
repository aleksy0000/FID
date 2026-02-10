package repo;

import db.Db;
import java.sql.*;
import java.util.*;

public final class TransactionRepo {

    public static long add(String accountId, String counterAccountId, int amountCents, String description, String occurredAtIso) {
        String txSql = """
      INSERT INTO transactions(occurred_at, description)
      VALUES (?, ?)
    """;
        String entrySql = """
      INSERT INTO entries(transID, accID, amount_cents)
      VALUES (?, ?, ?)
    """;
        try (Connection c = Db.connect()) {
            c.setAutoCommit(false);
            try (PreparedStatement txPs = c.prepareStatement(txSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement entryPs = c.prepareStatement(entrySql)) {
                txPs.setString(1, occurredAtIso);
                txPs.setString(2, description);
                txPs.executeUpdate();
                long transId;
                try (ResultSet rs = txPs.getGeneratedKeys()) {
                    if (rs.next()) {
                        transId = rs.getLong(1);
                    } else {
                        throw new SQLException("No generated key");
                    }
                }

                entryPs.setLong(1, transId);
                entryPs.setString(2, accountId);
                entryPs.setInt(3, amountCents);
                entryPs.executeUpdate();

                entryPs.setLong(1, transId);
                entryPs.setString(2, counterAccountId);
                entryPs.setInt(3, -amountCents);
                entryPs.executeUpdate();

                c.commit();
                return transId;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> listForAccount(String accountId) {
        String sql = """
      SELECT t.transID, e.amount_cents, t.description, t.occurred_at
      FROM entries e
      JOIN transactions t ON t.transID = e.transID
      WHERE e.accID = ?
      ORDER BY t.occurred_at DESC, t.transID DESC, e.entryID DESC
    """;
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(
                            "tx#" + rs.getLong("transID")
                                    + " amount=" + rs.getInt("amount_cents")
                                    + " desc=" + rs.getString("description")
                                    + " at=" + rs.getString("occurred_at")
                    );
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
