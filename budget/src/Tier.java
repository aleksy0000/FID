import java.util.ArrayList;
import java.util.List;

public class Tier {
    public static int rowCounter;
    protected ArrayList<Row> expenses = new ArrayList<>();
    protected int priority;

    public Tier (int priority){
        this.priority = priority;
    }

    public void createRow(String rowName, double rowValue){
        Row row = new Row(rowName, rowValue);
        expenses.add(row);
        rowCounter++;
    }

    public Row getExpense(int index){
        return expenses.get(index);
    }

    public int getPriority() {
        return priority;
    }

    public List<Row> getExpenses() {
        return expenses;
    }
}
