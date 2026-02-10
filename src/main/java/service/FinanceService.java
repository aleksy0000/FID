package service;

import accounts.Account;
import accounts.AccountType;
import accounts.Currency;
import repo.*;

import java.time.OffsetDateTime;

public final class FinanceService {
    private final AccountRepo accounts = new AccountRepo();
    private final TransactionRepo tx = new TransactionRepo();

    public void createAccount(String name, AccountType type, Currency currency) {
        Account newAccount = new Account(name, type, currency );

        accounts.createTable(newAccount.getAccID(), newAccount.getAccName(), newAccount.getAccTypeString(), newAccount.getCurrencyString());
    }

    public long addTransaction(String accountId, String counterAccountId, double amount, String description) {
        int cents = (int) Math.round(amount * 100.0); // ok for input; DB stores int
        String now = OffsetDateTime.now().toString();
        return tx.add(accountId, counterAccountId, cents, description, now);
    }

    public AccountRepo accounts() { return accounts; }
    public TransactionRepo transactions() { return tx; }
}
