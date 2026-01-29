import java.util.ArrayList;
import java.util.List;

public class Tier {
    public static int rowCounter;
    protected ArrayList<Row> expenses = new ArrayList<>();
    protected int priority;
    protected String tierName;

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

    public void setTierName(String tierName){
        this.tierName = tierName;
    }

    public double calcTierExpenseTotal(){
        double totalTierExpenses = 0;

        for(int i = 0;i < expenses.size();i++){
            totalTierExpenses += expenses.get(i).getRowValue();
        }

        return totalTierExpenses;
    }

    public void removeRow(int rowIndex){
        expenses.remove(rowIndex);
    }
}
