package transactionsTest;

import org.junit.jupiter.api.Test;
import transactions.LedgerLine;
import transactions.Transaction;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    public void transaction_has_generated_uuid() {
        Transaction transaction = new Transaction(
                new Date(),
                "salary payment",
                List.of(
                        new LedgerLine("cash", 1000, 0),
                        new LedgerLine("income", 0, 1000)
                ),
                1000,
                1000
        );

        assertNotNull(transaction.getTransactionID());
        assertDoesNotThrow(() -> UUID.fromString(transaction.getTransactionID()));
    }

    @Test
    public void transaction_getters_return_constructor_values() {
        Date date = new Date();
        List<LedgerLine> lines = List.of(
                new LedgerLine("cash", 2500, 0),
                new LedgerLine("revenue", 0, 2500)
        );

        Transaction transaction = new Transaction(date, "invoice paid", lines, 2500, 2500);

        assertEquals(date, transaction.getTransactionDate());
        assertEquals("invoice paid", transaction.getDescription());
        assertEquals(lines, transaction.getLedgerLines());
        assertEquals(2500, transaction.getTotalCreditsInCents());
        assertEquals(2500, transaction.getTotalDebitsInCents());
    }

    @Test
    public void assert_balance_accepts_balanced_transaction() {
        Transaction transaction = new Transaction(
                new Date(),
                "balanced transfer",
                List.of(
                        new LedgerLine("cash", 5000, 0),
                        new LedgerLine("equity", 0, 5000)
                ),
                5000,
                5000
        );

        assertDoesNotThrow(transaction::assertBalance);
    }

    @Test
    public void assert_balance_rejects_transactions_with_fewer_than_two_lines() {
        Transaction transaction = new Transaction(
                new Date(),
                "invalid transfer",
                List.of(new LedgerLine("cash", 5000, 0)),
                0,
                5000
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class, transaction::assertBalance);
        assertEquals("Transaction must have at least 2 ledger lines.", exception.getMessage());
    }

    @Test
    public void assert_balance_rejects_unbalanced_transactions() {
        Transaction transaction = new Transaction(
                new Date(),
                "unbalanced transfer",
                List.of(
                        new LedgerLine("cash", 5000, 0),
                        new LedgerLine("equity", 0, 3000)
                ),
                3000,
                5000
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class, transaction::assertBalance);
        assertEquals("Unbalanced transaction. Debits=5000 Credits=3000", exception.getMessage());
    }
}
