package accounts;

public enum AccountType {
    //You compute balance based on account type
    ASSET,      //Debit Increases / Credit Decreases
    EXPENSES,   //Debit Increases / Credit Decreases
    EQUITY,     //Credit Increases / Debit Decreases
    INCOME,     //Credit Increases / Debit Decreases
    LIABILITY   //Credit Increases / Debit Decreases

    //Assets = Liabilities + Equity
    //Equity = Assets - Liabilities
}
