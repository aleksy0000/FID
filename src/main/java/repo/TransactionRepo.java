package repo;

import db.Db;
import transactions.Transaction;

import java.sql.*;
import java.util.*;
import java.util.Date;

public final class TransactionRepo {

    public static void addTransactionToDB(Transaction transaction) {

        transaction.assertBalance();

        String transactionSql = """
      INSERT INTO transactions(transactionID, transactionDate, description)
      VALUES (?, ?, ?)
    """;
        String ledgerLineSql = """
      INSERT INTO ledgerLines(accID, transactionID, debit_amount_cents, credit_amount_cents)
      VALUES (?, ?, ?, ?)
    """;
        try (Connection c = Db.connect()) {
            c.setAutoCommit(false);

            try(
            PreparedStatement txStmt = c.prepareStatement(transactionSql);
            PreparedStatement lineStmt = c.prepareStatement(ledgerLineSql);)
            {

                //insert transaction header
                txStmt.setString(1, transaction.getTransactionID());
                txStmt.setString(2, transaction.getTransactionDate().toString());
                txStmt.setString(3, transaction.getDescription());

                txStmt.executeUpdate();

                //insert ledger lines (batch)
                for (int i = 0; i < transaction.getLedgerLines().size(); i++) {
                    lineStmt.setString(1, transaction.getLedgerLines().get(i).accID());
                    lineStmt.setString(2, transaction.getTransactionID());
                    lineStmt.setLong(3, transaction.getLedgerLines().get(i).debit_amount_cents());
                    lineStmt.setLong(4, transaction.getLedgerLines().get(i).credit_amount_cents());

                    lineStmt.addBatch();
                }

                lineStmt.executeBatch();

                c.commit();
            }catch (SQLException e){
                c.rollback();
                throw e;
            }finally{
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
