import budget.Budget;
import budget.Row;
import budget.Tier;

import java.util.List;
import java.util.Scanner;

public class BudgetCli {
    private final Budget budget = new Budget();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        System.out.println("Budget CLI");
        System.out.println("Type a menu number to continue.");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Select option: ");
            switch (choice) {
                case 1:
                    createTier();
                    break;
                case 2:
                    addRow();
                    break;
                case 3:
                    listTiers();
                    break;
                case 4:
                    listRows();
                    break;
                case 5:
                    showTotals();
                    break;
                case 6:
                    removeRow();
                    break;
                case 7:
                    running = false;
                    break;
                default:
                    System.out.println("Unknown option.");
                    break;
            }
        }
        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1) Create tier");
        System.out.println("2) Add row to tier");
        System.out.println("3) List tiers");
        System.out.println("4) List rows in tier");
        System.out.println("5) Show totals");
        System.out.println("6) Remove row from tier");
        System.out.println("7) Exit");
    }

    private void createTier() {
        int priority = readInt("Tier priority: ");
        String name = readLine("Tier name: ");
        budget.createTier(priority);
        int tierIndex = budget.getTiers().size() - 1;
        budget.getTier(tierIndex).setTierName(name);
        System.out.println("Created tier #" + tierIndex + " (" + name + ").");
    }

    private void addRow() {
        int tierIndex = readInt("Tier index: ");
        Tier tier = getTierOrNull(tierIndex);
        if (tier == null) {
            System.out.println("Invalid tier index.");
            return;
        }

        String rowName = readLine("Row name: ");
        double rowValue = readDouble("Row value: ");
        String categoryName = readLine("Category name: ");
        int catType = readInt("Category type (1=expense, 2=income, 3=transfer): ");
        double budgetedAmount = readDouble("Budgeted amount: ");
        int accID = readInt("Enter Account ID: ");
        int dueDay = readDayOfMonth("Enter due day (1-31): ");

        tier.createRow(rowName, rowValue, categoryName, catType, budgetedAmount, accID, dueDay);
        System.out.println("Added row to tier #" + tierIndex + ".");
    }

    private void listTiers() {
        List<Tier> tiers = budget.getTiers();
        if (tiers.isEmpty()) {
            System.out.println("No tiers.");
            return;
        }

        for (int i = 0; i < tiers.size(); i++) {
            Tier tier = tiers.get(i);
            String name = tier.getTierName();
            if (name == null || name.trim().isEmpty()) {
                name = "(unnamed)";
            }
            System.out.println(
                "#" + i + " " + name + " | priority " + tier.getPriority()
                    + " | total " + tier.calcTierExpenseTotal()
            );
        }
    }

    private void listRows() {
        int tierIndex = readInt("Tier index: ");
        Tier tier = getTierOrNull(tierIndex);
        if (tier == null) {
            System.out.println("Invalid tier index.");
            return;
        }

        List<Row> rows = tier.getExpenses();
        if (rows.isEmpty()) {
            System.out.println("No rows in tier #" + tierIndex + ".");
            return;
        }

        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            System.out.println(
                "#" + i + " " + row.getRowName() + " | " + row.getRowValue()
            );
        }
    }

    private void showTotals() {
        double income = budget.getTotalIncome();
        double total = budget.getTotalTierTotals();
        double expenses = total - income;
        double revenue = budget.getTotalRevenue();

        System.out.println("Income: " + income);
        System.out.println("Expenses: " + expenses);
        System.out.println("Revenue: " + revenue);
    }

    private void removeRow() {
        int tierIndex = readInt("Tier index: ");
        Tier tier = getTierOrNull(tierIndex);
        if (tier == null) {
            System.out.println("Invalid tier index.");
            return;
        }

        int rowIndex = readInt("Row index to remove: ");
        if (rowIndex < 0 || rowIndex >= tier.getExpenses().size()) {
            System.out.println("Invalid row index.");
            return;
        }

        tier.removeRow(rowIndex);
        System.out.println("Removed row #" + rowIndex + " from tier #" + tierIndex + ".");
    }

    private Tier getTierOrNull(int index) {
        if (index < 0 || index >= budget.getTiers().size()) {
            return null;
        }
        return budget.getTier(index);
    }

    private int readInt(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Enter a whole number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Enter a number.");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int readDayOfMonth(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                int day = Integer.parseInt(input.trim());
                if (day < 1 || day > 31) {
                    System.out.println("Enter a day between 1 and 31.");
                    continue;
                }
                return day;
            } catch (NumberFormatException e) {
                System.out.println("Enter a whole number.");
            }
        }
    }
}
