import budget.Budget;
import budget.Row;
import budget.Tier;

import java.util.Scanner;

public class CLI_UI2 {
    private final Scanner scanner = new Scanner(System.in);
    private Budget budget;

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> createBudget();
                case "2" -> createTier();
                case "3" -> createRow();
                case "4" -> printBudget();
                case "0" -> running = false;
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private void printMenu() {
        System.out.println("""
            ===== Budget CLI (v2) =====
            1. Create Budget
            2. Create Tier
            3. Create Row (with Category)
            4. Print Budget
            0. Exit
            """);
        System.out.print("Choice: ");
    }

    private void createBudget() {
        this.budget = new Budget();
        System.out.println("Budget Created");
    }

    private void createTier() {
        if (budget == null) {
            System.out.println("Create a budget first!");
            return;
        }

        System.out.println("Enter Tier Priority:");
        String input = scanner.nextLine();

        int priority;
        try {
            priority = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Number.");
            return;
        }

        budget.createTier(priority);
        int newTierIndex = budget.getTiers().size() - 1;

        System.out.println("Enter Tier Name:");
        String tierName = scanner.nextLine();

        if (tierName.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        budget.getTier(newTierIndex).setTierName(tierName);
    }

    private void createRow() {
        if (budget == null) {
            System.out.println("Create a budget first!");
            return;
        }

        System.out.println("Choose Tier (0 = Income)");
        String tierInput = scanner.nextLine();

        int tierIndex;
        try {
            tierIndex = Integer.parseInt(tierInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Value.");
            return;
        }

        if (tierIndex < 0 || tierIndex >= budget.getTiers().size()) {
            System.out.println("Tier doesn't exist!");
            return;
        }

        System.out.println("Enter Row Name:");
        String rowName = scanner.nextLine();
        if (rowName.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Enter Row Value:");
        String valueInput = scanner.nextLine();
        double value;
        try {
            value = Double.parseDouble(valueInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Number.");
            return;
        }

        System.out.print("Enter Category Name:");
        String categoryName = scanner.nextLine();
        if (categoryName.isEmpty()) {
            System.out.println("Category name cannot be empty.");
            return;
        }

        System.out.print("Enter Category Type (1=Expense, 2=Income, 3=Transfer):");
        String typeInput = scanner.nextLine();
        int catType;
        try {
            catType = Integer.parseInt(typeInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Number.");
            return;
        }

        System.out.print("Enter Category Budgeted Amount:");
        String budgetInput = scanner.nextLine();
        double budgetedAmount;
        try {
            budgetedAmount = Double.parseDouble(budgetInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Number.");
            return;
        }

        budget.getTier(tierIndex).createRow(rowName, value, categoryName, catType, budgetedAmount);
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
                        ? "Tier " + i
                        : "Tier " + i + " - " + tier.getTierName();
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

            System.out.printf("  Tier total: %.2f%n%n", tier.calcTierExpenseTotal());
        }

        double totalIncome = budget.getTotalIncome();
        double totalAllTiers = budget.getTotalTierTotals();
        double totalExpenses = totalAllTiers - totalIncome;
        System.out.printf("Total income: %.2f%n", totalIncome);
        System.out.printf("Total expenses: %.2f%n", totalExpenses);
        System.out.printf("Total revenue: %.2f%n", budget.getTotalRevenue());
    }
}
