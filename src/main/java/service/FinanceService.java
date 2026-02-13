package service;

import accounts.Account;
import accounts.AccountType;
import accounts.Currency;
import repo.*;
import transactions.LedgerLine;
import transactions.Transaction;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class FinanceService {
    private final AccountRepo accounts = new AccountRepo();
    private final TransactionRepo tx = new TransactionRepo();

    public void createAccount(String name, AccountType type, Currency currency) {
        Account newAccount = new Account(name, type, currency );

        accounts.createTable(newAccount.getAccID(), newAccount.getAccName(), newAccount.getAccTypeString(), newAccount.getCurrencyString());
    }

    //create new ledger line, must be 2 rows, where debit = credit
    public List<LedgerLine> createLedgerLine(String accID, int debit_amount_cents, int credit_amount_cents){
        List<LedgerLine> ledgerLines = new ArrayList<>();
        ledgerLines.add(new LedgerLine(accID,debit_amount_cents,0));
        ledgerLines.add(new LedgerLine(accID,0,credit_amount_cents));

        return ledgerLines;
    }

    //create new transaction, this will hold the 2 ledger line rows
    public static Transaction newTransaction(Date transactionDate, String description, List<LedgerLine> lines){

        if(lines.size() < 2){
            throw new IllegalArgumentException("Ledger entry must have at least 2 lines.");
        }

        long totalDebits = 0;
        long totalCredits = 0;

        for (LedgerLine line : lines) {
            totalDebits += line.debit_amount_cents();
            totalCredits += line.credit_amount_cents();
        }

        if(totalDebits != totalCredits){
            throw new IllegalArgumentException("Unbalanced Entry: Debits do not equal to credits");
        }

        return new Transaction(transactionDate, description, lines, totalDebits, totalCredits);

    }


    public AccountRepo accounts() { return accounts; }
    public TransactionRepo transactions() { return tx; }
}
