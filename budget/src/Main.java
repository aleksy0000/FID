
public class Main {
    public static void main(String[] args) {
        Budget budget1 = new Budget();

        budget1.createTier(1);
        budget1.getTier(0).createRow("rent", 650);
        budget1.getTier(0).getExpense(0).getValues();
    }
}