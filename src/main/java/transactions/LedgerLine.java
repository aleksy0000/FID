package transactions;

/**
 * @param debit_amount_cents  in cents
 * @param credit_amount_cents in cents
 */
public record LedgerLine(String accID, int debit_amount_cents, int credit_amount_cents) {

    public LedgerLine(String accID, int debit_amount_cents, int credit_amount_cents){
        if(accID == null){
            throw new IllegalArgumentException("Account ID Cannot Be null");
        }
        if(debit_amount_cents == 0 && credit_amount_cents == 0){
            throw new IllegalArgumentException("Both credit and debit cannot be 0");
        }

        boolean hasCredit = credit_amount_cents > 0;
        boolean hasDebit = debit_amount_cents > 0;

        if(hasDebit == hasCredit){
            throw new IllegalArgumentException("Debit/Credit cannot both have a value (one-sided)");
        }

        this.accID = accID;
        this.debit_amount_cents = debit_amount_cents;
        this.credit_amount_cents = credit_amount_cents;
    }
    @Override
    public int credit_amount_cents() {
        return credit_amount_cents;
    }

    public int debit_amount_cents(){
        return debit_amount_cents;
    }

    public String accID(){
        return accID;
    }
}
