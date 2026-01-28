import java.util.ArrayList;

public class Row {
    protected String rowName;
    protected double rowValue;

    public Row(String rowName, double rowValue){
        this.rowName = rowName;
        this.rowValue = rowValue;
    }

    public void getValues(){
        System.out.println("Expense: " + rowName + " Amount: " + rowValue);
    }

    public String getRowName() {
        return rowName;
    }

    public double getRowValue() {
        return rowValue;
    }
}
