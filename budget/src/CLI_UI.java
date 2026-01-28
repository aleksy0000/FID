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
                case "0" -> running = false;
                default -> System.out.println("Invalid option!");
            }
        }
    }//end run()

    private void printMenu(){
        System.out.println("""
            ===== Budget CLI =====
            1. Create Budget
            2. Create Tier
            3. Create Row
            4. Print Budget
            0. Exit
            """);
        System.out.print("Choice: ");
    }

    private void createBudget(){
        this.budget = new Budget();

        System.out.println("Budget Created");
    }

    private void createTier(){
        System.out.println("Enter Tier Priority:");

        String input = scanner.nextLine();

        int priority;

        try{
            priority = Integer.parseInt(input);
        } catch (NumberFormatException e){
            System.out.println("Invalid Number.");
            return;
        }

        budget.createTier(priority);
    }

    private void createRow(){
        if(budget == null){
            System.out.println("Create a budget first!");
            return;
        }

        System.out.println("Choose Tier");

        String tierInput = scanner.nextLine();

        int tierIndex = 0;

        try{
            tierIndex = Integer.parseInt(tierInput);
        } catch(NumberFormatException e) {
            System.out.println("Invalid Value.");
        }

        if(tierIndex > budget.tiers.size() - 1 && tierIndex >= 0){
            System.out.println("Tier doesn't exist!");
            return;
        }

        System.out.println("Enter Row Name:");

        String rowName = scanner.nextLine();

        if(rowName.isEmpty()){
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Enter Row Value:");

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
            System.out.println("Tier " + i + " (priority " + tier.getPriority() + "):");

            double tierTotal = 0.0;

            for (int j = 0; j < tier.getExpenses().size(); j++) {
                Row row = tier.getExpenses().get(j);

                System.out.printf(
                        "  [%d] %s : %.2f%n",
                        j,
                        row.getRowName(),
                        row.getRowValue()
                );

                tierTotal += row.getRowValue();
            }

            System.out.printf("  Tier total: %.2f%n%n", tierTotal);
        }
    }
}
