package transactions;

/**
 * @param debit_amount_cents  in cents
 * @param credit_amount_cents in cents
 */
public record LedgerLine(String accID, int debit_amount_cents, int credit_amount_cents) {

    @Override
    public int credit_amount_cents() {
        return credit_amount_cents;
    }
}
