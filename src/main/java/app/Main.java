package app;

import accounts.Account;
import accounts.AccountType;
import accounts.Currency;
import db.Db;
import repo.BalanceRepo;
import repo.TransactionRepo;
import service.FinanceService;
import transactions.LedgerLine;
import transactions.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {


    }

}