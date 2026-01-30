import budget.Budget;
import budget.Row;
import budget.Tier;

import java.util.Scanner;

public class CLI_UI {
    private final Scanner scanner = new Scanner(System.in);
    private Budget budget;

    public void run(){
        boolean running = true;

        while(running){
            printMenu();
            String input = scanner.nextLine().trim();

            switch(input) {
                case "1" -> createBudget();
                case "2" -> createTier();
                case "3" -> createRow();
                case "4" -> printBudget();
                case "5" -> editRow();
                case "6" -> deleteRow();
                case "0" -> running = false;
                default -> System.out.println("Invalid option!");
            }
        }
    }//end run()

    private void printMenu(){
        System.out.println("""
            ===== budget.Budget CLI =====
            1. Create budget.Budget
            2. Create budget.Tier
            3. Create budget.Row
            4. Print budget.Budget
            5. Edit budget.Row
            6. Delete budget.Row
            0. Exit
            """);
        System.out.print("Choice: ");
    }

    private void createBudget(){
        this.budget = new Budget();

        System.out.println("budget.Budget Created");
    }

    private void createTier(){
        if(budget == null){
            System.out.println("Create a budget first!");
            return;
        }

        System.out.println("Enter budget.Tier Priority:");

        String input = scanner.nextLine();

        int priority;

        try{
            priority = Integer.parseInt(input);
        } catch (NumberFormatException e){
            System.out.println("Invalid Number.");
            return;
        }

        budget.createTier(priority);
        int newTierIndex = budget.getTiers().size() - 1;

        System.out.println("Enter budget.Tier Name:");

        String tierName = scanner.nextLine();

        if(tierName.isEmpty()){
            System.out.println("Name cannot be empty.");
            return;
        }

        budget.getTier(newTierIndex).setTierName(tierName);
    }

    private void createRow(){
        if(budget == null){
            System.out.println("Create a budget first!");
            return;
        }

        System.out.println("Choose budget.Tier (0 = Income)");

        String tierInput = scanner.nextLine();

        int tierIndex = 0;

        try{
            tierIndex = Integer.parseInt(tierInput);
        } catch(NumberFormatException e) {
            System.out.println("Invalid Value.");
        }

        if(tierIndex > budget.getTiers().size() - 1 && tierIndex >= 0){
            System.out.println("budget.Tier doesn't exist!");
            return;
        }

        System.out.println("Enter budget.Row Name:");

        String rowName = scanner.nextLine();

        if(rowName.isEmpty()){
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Enter budget.Row Value:");

        String valueInput = scanner.nextLine();

        double value;

        try{
            value = Double.parseDouble(valueInput);
        }catch(NumberFormatException e){
            System.out.println("Invalid Number.");
            return;
        }


        budget.getTier(tierIndex).createRow(rowName,value);




    }

    private void printBudget() {
        if (budget == null) {
            System.out.println("No budget created.");
            return;
        }

        System.out.println("===== BUDGET =====");

        for (int i = 0; i < budget.getTiers().size(); i++) {
            Tier tier = budget.getTiers().get(i);
            if (i == 0 && tier == budget.getIncomeTier()) {
                String displayName = tier.getTierName() == null || tier.getTierName().isEmpty()
                        ? "Income"
                        : "Income - " + tier.getTierName();
                System.out.println(displayName + ":");
            } else {
                String displayName = tier.getTierName() == null || tier.getTierName().isEmpty()
                        ? "budget.Tier " + i
                        : "budget.Tier " + i + " - " + tier.getTierName();
                System.out.println(displayName + " (priority " + tier.getPriority() + "):");
            }

            for (int j = 0; j < tier.getExpenses().size(); j++) {
                Row row = tier.getExpenses().get(j);

                System.out.printf(
                        "  [%d] %s : %.2f%n",
                        j,
                        row.getRowName(),
                        row.getRowValue()
                );
            }

            System.out.printf("  budget.Tier total: %.2f%n%n", tier.calcTierExpenseTotal());
        }

        double totalIncome = budget.getTotalIncome();
        double totalAllTiers = budget.getTotalTierTotals();
        double totalExpenses = totalAllTiers - totalIncome;
        System.out.printf("Total income: %.2f%n", totalIncome);
        System.out.printf("Total expenses: %.2f%n", totalExpenses);
        System.out.printf("Total revenue: %.2f%n", budget.getTotalRevenue());
    }

    private void editRow(){
        if(budget == null){
            System.out.println("Create a budget first!");
            return;
        }

        System.out.println("Choose budget.Tier (0 = Income)");

        String tierInput = scanner.nextLine();

        int tierIndex;

        try{
            tierIndex = Integer.parseInt(tierInput);
        } catch(NumberFormatException e) {
            System.out.println("Invalid Value.");
            return;
        }

        if(tierIndex < 0 || tierIndex >= budget.getTiers().size()){
            System.out.println("budget.Tier doesn't exist!");
            return;
        }

        Tier tier = budget.getTier(tierIndex);

        if(tier.getExpenses().isEmpty()){
            System.out.println("No rows in this tier.");
            return;
        }

        for (int i = 0; i < tier.getExpenses().size(); i++) {
            Row row = tier.getExpenses().get(i);
            System.out.printf("  [%d] %s : %.2f%n", i, row.getRowName(), row.getRowValue());
        }

        System.out.println("Choose budget.Row");

        String rowInput = scanner.nextLine();

        int rowIndex;

        try{
            rowIndex = Integer.parseInt(rowInput);
        } catch(NumberFormatException e) {
            System.out.println("Invalid Value.");
            return;
        }

        if(rowIndex < 0 || rowIndex >= tier.getExpenses().size()){
            System.out.println("budget.Row doesn't exist!");
            return;
        }

        System.out.println("Enter New budget.Row Name:");

        String rowName = scanner.nextLine();

        if(rowName.isEmpty()){
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Enter New budget.Row Value:");

        String valueInput = scanner.nextLine();

        double value;

        try{
            value = Double.parseDouble(valueInput);
        }catch(NumberFormatException e){
            System.out.println("Invalid Number.");
            return;
        }

        tier.getExpenses().set(rowIndex, new Row(rowName, value));
    }

    private void deleteRow(){
        if(budget == null){
            System.out.println("Create a budget first!");
            return;
        }

        System.out.println("Choose budget.Tier (0 = Income)");

        String tierInput = scanner.nextLine();

        int tierIndex;

        try{
            tierIndex = Integer.parseInt(tierInput);
        } catch(NumberFormatException e) {
            System.out.println("Invalid Value.");
            return;
        }

        if(tierIndex < 0 || tierIndex >= budget.getTiers().size()){
            System.out.println("budget.Tier doesn't exist!");
            return;
        }

        Tier tier = budget.getTier(tierIndex);

        if(tier.getExpenses().isEmpty()){
            System.out.println("No rows in this tier.");
            return;
        }

        System.out.println("Choose budget.Row");

        String rowInput = scanner.nextLine();

        int rowIndex;

        try{
            rowIndex = Integer.parseInt(rowInput);
        } catch(NumberFormatException e) {
            System.out.println("Invalid Value.");
            return;
        }

        if(rowIndex < 0 || rowIndex >= tier.getExpenses().size()){
            System.out.println("budget.Row doesn't exist!");
            return;
        }

        tier.getExpenses().remove(rowIndex);
    }
}
