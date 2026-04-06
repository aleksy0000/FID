/*
    FinanceService.java

    Purpose: FinanceService.java is a service layer coordinating Accounts and Transactions in the Business Layer with the Accounts and Transactions in the Data Layer.

    Functionality:
    - public void createNewAccountAndInsertIntoDB(String name, AccountType type, Currency currency) -> creates new account object and insert it into the database.
    - public List<LedgerLine> createLedgerLine(String accID, int debit_amount_cents, int credit_amount_cents) -> Creates an ArrayList that holds two ledgerLine records that will be passed to form one transaction
    - public static Transaction newTransaction(Date transactionDate, String description, List<LedgerLine> lines) -> Creates a new transaction with the ledgerLine ArrayList created in createLedgerLine()
 */

package service;

import accounts.Account;
import accounts.AccountType;
import accounts.Currency;
import db.Db;
import repo.*;
import transactions.LedgerLine;
import transactions.Transaction;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class FinanceService {
    // AI GENERATED CODE
    private final AccountRepo accounts;
    private final TransactionRepo tx;

    public FinanceService(Db db) {
        this.accounts = new AccountRepo(db);
        this.tx = new TransactionRepo(db);
    }

    //Creates a new account object and passes its data to insertAccountRecord() which inserts it into the database.
    public void createNewAccountAndInsertIntoDB(String name, AccountType type, Currency currency) {
        Account newAccount = new Account(name, type, currency );

        accounts.insertAccountRecord(newAccount.getAccID(), newAccount.getAccName(), newAccount.getAccTypeString(), newAccount.getCurrencyString());
    }

    //create new ledger line, must be 2 rows, where debit = credit, returns the ArrayList with the 2 rows.
    public List<LedgerLine> createLedgerLine(String accID, int debit_amount_cents, int credit_amount_cents){

        if(debit_amount_cents != credit_amount_cents) {
            throw new IllegalArgumentException("debit must equal credit");
        }

        List<LedgerLine> ledgerLines = new ArrayList<>();
        ledgerLines.add(new LedgerLine(accID, debit_amount_cents, 0));
        ledgerLines.add(new LedgerLine(accID, 0, credit_amount_cents));

        return ledgerLines;

    }

    //create new transaction, this will hold the 2 ledger line rows created in createLedgerLine()
    public static Transaction newTransaction(Date transactionDate, String description, List<LedgerLine> lines){

        long totalDebits = 0;
        long totalCredits = 0;

        for (LedgerLine line : lines) {
            totalDebits += line.debit_amount_cents();
            totalCredits += line.credit_amount_cents();
        }

        return new Transaction(transactionDate, description, lines, totalCredits, totalDebits);

    }

    //I'm not sure what this is for...
    public AccountRepo accounts() { return accounts; }
    public TransactionRepo transactions() { return tx; }
}
