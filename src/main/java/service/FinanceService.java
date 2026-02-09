package service;

import repo.*;

import java.time.OffsetDateTime;

public final class FinanceService {
    private final AccountRepo accounts = new AccountRepo();
    private final TransactionRepo tx = new TransactionRepo();

    public long createAccount(String name, String type,String currency) {
        return accounts.create(name, type, currency);
    }

    public long addTransaction(long accountId, double amount, String description) {
        int cents = (int) Math.round(amount * 100.0); // ok for input; DB stores int
        String now = OffsetDateTime.now().toString();
        return tx.add(accountId, cents, description, now);
    }

    public AccountRepo accounts() { return accounts; }
    public TransactionRepo transactions() { return tx; }
}
