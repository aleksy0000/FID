/*
    TransactionRepo.java

    Purpose -> TransactionRepo.java handles all operations modifying transaction and ledgerLines tables and records in the database.

    Functionality:
    - addTransactionToDB(Transaction transaction) -> Inserts transaction objects data and its nested ledgerLine objects data into transaction and ledgerLines respectively.
 */
package repo;

import db.Db;
import transactions.Transaction;

import java.sql.*;
import java.util.*;
import java.util.Date;

public final class TransactionRepo {
    // AI GENERATED CODE
    private final Db db;

    public TransactionRepo(Db db) {
        this.db = db;
    }

    /*
        Inserts:
        1. transaction record into transaction table using passed transaction object.
        2. ledgerLines database record into ledgerLines table using ledgerLines java record, through ledgerLines list inside of passed transactions object.
     */
    public void addTransactionToDB(Transaction transaction) {

        duplicateCheck(transaction);
        transaction.assertBalance();

        String transactionSql = """
      INSERT INTO transactions(transactionID, transactionDate, description)
      VALUES (?, ?, ?)
    """;
        String ledgerLineSql = """
      INSERT INTO ledgerLines(accID, transactionID, debit_amount_cents, credit_amount_cents)
      VALUES (?, ?, ?, ?)
    """;
        try (Connection c = db.connect()) {
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

    public void duplicateCheck(Transaction transaction) {
        String sql = """
        SELECT 1 FROM transactions WHERE transactionID = ?
    """;

        try (Connection c = db.connect();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setString(1, transaction.getTransactionID());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                throw new RuntimeException("Duplicate transaction detected");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //returns specific transaction and it's ledgerLines by transactionID
   public Transaction getByID(String transactionID) throws SQLException {
        String getTransactionsSql =
                """
                    SELECT transactionID, transactionDate, description
                    FROM transactions
                    WHERE transactionID = ?
                """;

        String getLedgerLinesSql =
                """
                    SELECT accID, transactionID, debit_amount_cents,  credit_amount_cents
                    FROM ledgerLines
                    WHERE transactionID = ?
                """;

        try (Connection c = db.connect();
            PreparedStatement getTransactionStmt = c.prepareStatement(getTransactionsSql);
            PreparedStatement getLedgerLinesStmt = c.prepareStatement(getLedgerLinesSql);){

            //Fetch transaction header
            getTransactionsStmt.setString(1, transactionID);

            ResultSet transactionResults = getTransactionStmt.executeQuery();

            Transaction tx = null;

            while(transactionResults.next()) {
                tx = new Transaction(
                    transactionResults.getDate("transactionDate"),
                    transactionResults.getString("description"),
                    new ArrayList<>(),
                    0,
                    0
                )
            }

            //fetch ledger lines
            getLedgerLinesStmt.setString(1, transactionID);

            ResultSet ledgerLinesResults = getLedgerLinesSql.executeQuery();

            List<LedgerLine> ledgerLines = new ArrayList<>();

            while(ledgerLinesResults.next()) {
                ledgerLines.add(new LedgerLine(
                        ledgerLinesResults.getString("accID"),
                        ledgerLinesResults.getString("debit_amount_cents");
                        ledgerLinesResults.getString("credit_amount_cents");
                ));
            }

            return new Transaction(
                    tx.getTransactionDate(),
                    tx.getDescription(),
                    ledgerLines,
                    0,
                    0
            )


        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    //Reversal Transaction Rules:
    //Never delete or modify a transaction.
    //you must add a new transaction that cancels the other out.
    public void reverseTransaction(String transactionID) {
        //get transaction from database with the specific transactionID

        //create a transactions with exactly opposite values to cancel the other out.
    }

}
