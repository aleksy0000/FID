package serviceTest;

import accounts.AccountType;
import accounts.Currency;
import db.Db;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import service.FinanceService;
import transactions.LedgerLine;
import transactions.Transaction;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FinanceServiceTest {

    @TempDir
    Path tempDir;

    private Db db;
    private FinanceService financeService;

    @BeforeEach
    public void setUp() {
        db = new Db(tempDir, "finance-service-test.db");
        db.init();
        financeService = new FinanceService(db);
    }

    @Test
    public void create_ledger_line_returns_balanced_debit_and_credit_lines() {
        List<LedgerLine> ledgerLines = financeService.createLedgerLine("acc-001", 1200, 1200);

        assertEquals(2, ledgerLines.size());
        assertEquals(new LedgerLine("acc-001", 1200, 0), ledgerLines.get(0));
        assertEquals(new LedgerLine("acc-001", 0, 1200), ledgerLines.get(1));
    }

    @Test
    public void create_ledger_line_rejects_unbalanced_amounts() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> financeService.createLedgerLine("acc-001", 1200, 900)
        );

        assertEquals("debit must equal credit", exception.getMessage());
    }

    @Test
    public void new_transaction_calculates_totals_from_ledger_lines() {
        Date date = new Date();
        List<LedgerLine> lines = List.of(
                new LedgerLine("cash", 4500, 0),
                new LedgerLine("income", 0, 4500)
        );

        Transaction transaction = FinanceService.newTransaction(date, "consulting income", lines);

        assertEquals(date, transaction.getTransactionDate());
        assertEquals("consulting income", transaction.getDescription());
        assertEquals(lines, transaction.getLedgerLines());
        assertEquals(4500, transaction.getTotalDebitsInCents());
        assertEquals(4500, transaction.getTotalCreditsInCents());
    }

    @Test
    public void create_new_account_and_insert_into_db_persists_account_record() {
        financeService.createNewAccountAndInsertIntoDB("Emergency Fund", AccountType.ASSET, Currency.EUR);

        List<String> accounts = financeService.accounts().accListToString();

        assertEquals(1, accounts.size());
        assertTrue(accounts.get(0).contains(": Emergency Fund: ASSET (EUR)"));
    }
}
