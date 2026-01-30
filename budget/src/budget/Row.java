package budget;

public class Row {
    private String rowName;
    private double rowValue;
    private int dayDue;
    private Category rowCategory;


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

    public void setDayDue(int dayOfTheMonth){
        this.dayDue = dayOfTheMonth;
    }

    public void setRowCategory(int categoryID, String categoryName, CategoryType categoryType, double budgetedAmount){
        rowCategory.setCategoryID(categoryID);
        rowCategory.setCategoryName(categoryName);
        rowCategory.setCategoryType(categoryType);
        rowCategory.setBudgetedAmount(budgetedAmount);
    }
}
