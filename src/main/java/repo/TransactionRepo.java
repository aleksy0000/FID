package repo;

import db.Db;

import java.sql.*;
import java.util.*;

public final class TransactionRepo {

    public long add(long accountId, int amountCents, String description, String occurredAtIso) {
        String sql = """
      INSERT INTO transactions(account_id, amount_cents, description, occurred_at)
      VALUES (?, ?, ?, ?)
    """;
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, accountId);
            ps.setInt(2, amountCents);
            ps.setString(3, description);
            ps.setString(4, occurredAtIso);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                throw new SQLException("No generated key");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> listForAccount(long accountId) {
        String sql = """
      SELECT id, amount_cents, description, occurred_at
      FROM transactions
      WHERE account_id = ?
      ORDER BY occurred_at DESC, id DESC
    """;
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(
                            "tx#" + rs.getLong("id")
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
