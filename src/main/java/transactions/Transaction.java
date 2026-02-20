/*
    Every financial event = at least two entries, total debits = total credits.

    invariant: SUM(debits) == SUM(credits)
*/
package transactions;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Transaction {
    private final String transactionID = UUID.randomUUID().toString();
    private final Date transactionDate;
    private final String description;
    private final List<LedgerLine> ledgerLines;
    private final long totalDebits;
    private final long totalCredits;

    public Transaction(Date transactionDate, String description, List<LedgerLine> lines, long totalCredits, long totalDebits){
        this.transactionDate = transactionDate;
        this.description = description;
        this.ledgerLines = lines;
        this.totalCredits = totalCredits;
        this.totalDebits = totalDebits;
    }

    public String getTransactionID(){
        return transactionID;
    }

    public Date getTransactionDate(){
        return transactionDate;
    }

    public String getDescription(){
        return description;
    }

    public List<LedgerLine> getLedgerLines(){
        return ledgerLines;
    }

    public long getTotalCreditsInCents() {
        return totalCredits;
    }

    public long getTotalDebitsInCents() {
        return totalDebits;
    }

    public void assertBalance(){
        if (ledgerLines == null || ledgerLines.size() < 2) {
            throw new IllegalStateException("Transaction must have at least 2 ledger lines.");
        }

        long totalDebits = 0;
        long totalCredits = 0;

        for (LedgerLine line : ledgerLines) {
            totalDebits += line.debit_amount_cents();
            totalCredits += line.credit_amount_cents();
        }

        if (totalDebits != totalCredits) {
            throw new IllegalStateException(
                    "Unbalanced transaction. Debits=" + totalDebits +
                            " Credits=" + totalCredits
            );
        }
    }
}
