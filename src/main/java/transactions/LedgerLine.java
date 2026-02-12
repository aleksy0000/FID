package transactions;

public class LedgerLine {
    private final String accID;
    private final int debit_amount_cents; //in cents
    private final int credit_amount_cents; //in cents

    public LedgerLine(String accID, int debit_amount_cents, int credit_amount_cents){
        this.accID = accID;
        this.debit_amount_cents = debit_amount_cents;
        this.credit_amount_cents = credit_amount_cents;
    }

    public int getDebit_amount_cents(){
        return debit_amount_cents;
    }

    public int getCredit_amount_cents() {
        return credit_amount_cents;
    }

    public String getAccID(){
        return accID;
    }
}
