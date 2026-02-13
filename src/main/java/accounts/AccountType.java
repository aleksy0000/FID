package accounts;

public enum AccountType {
    CURRENT, //This account the regular use account
    INCOME, //This account is for tracking money coming into current CURRENT=DEBIT / INCOME=CREDIT
    SAVINGS,//This account is for savings from current, SAVINGS=DEBIT / CURRENT=CREDIT
    EXPENSES, //This account tracks expenses, current=credit / expenses=debit
    LIABILITY,
    ASSET,
    EQUITY
}
