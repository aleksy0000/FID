package budget;

public class Row {
    private String rowName;
    private double rowValue;
    //private int dayDue;
    private Category rowCategory;


    public Row(String rowName, double rowValue, String categoryName, int catType, double budgetedAmount){
        setRowName(rowName);
        setRowValue(rowValue);
        if(catType == 1){//if expense
            this.rowCategory = new Category(0, "categoryName", CategoryType.EXPENSE, budgetedAmount);
        }else if(catType == 2){//if income
            this.rowCategory = new Category(0, "categoryName", CategoryType.INCOME, budgetedAmount);

        }else if(catType == 3){//if transfer
            this.rowCategory = new Category(0, categoryName, CategoryType.TRANSFER, budgetedAmount);
        }else{
            System.out.println("Invalid Category Type");
            return;
        }
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

    /*public void setDayDue(int dayOfTheMonth){
        this.dayDue = dayOfTheMonth;
    }*/

    public void setRowCategory(int categoryID, String categoryName, CategoryType categoryType, double budgetedAmount){
        rowCategory.setCategoryID(categoryID);
        rowCategory.setCategoryName(categoryName);
        rowCategory.setCategoryType(categoryType);
        rowCategory.setBudgetedAmount(budgetedAmount);
    }
}
