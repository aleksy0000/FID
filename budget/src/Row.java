import java.util.ArrayList;

public class Row {
    protected String rowName;
    protected double rowValue;

    public Row(String rowName, double rowValue){
        setRowName(rowName);
        setRowValue(rowValue);
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

    public void setRowName(String rowName){
        this.rowName = rowName;
    }

    public void setRowValue(double rowValue){
        this.rowValue = rowValue;
    }
}
