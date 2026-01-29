import javax.swing.*;
import java.awt.*;

public class Swing_UI {
    private Budget budget;
    private final JTextArea output = new JTextArea(18, 60);

    public void run() {
        SwingUtilities.invokeLater(this::createAndShowUI);
    }

    private void createAndShowUI() {
        JFrame frame = new JFrame("Budget GUI");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(new JButton(new AbstractAction("Create Budget") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                budget = new Budget();
                output.setText("Budget created.\n");
            }
        }));
        buttonPanel.add(new JButton(new AbstractAction("Add Tier") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                addTier();
            }
        }));
        buttonPanel.add(new JButton(new AbstractAction("Add Row") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                addRow();
            }
        }));
        buttonPanel.add(new JButton(new AbstractAction("Edit Row") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                editRow();
            }
        }));
        buttonPanel.add(new JButton(new AbstractAction("Delete Row") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                deleteRow();
            }
        }));
        buttonPanel.add(new JButton(new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                renderBudget();
            }
        }));

        JPanel root = new JPanel(new BorderLayout());
        root.add(buttonPanel, BorderLayout.NORTH);
        root.add(new JScrollPane(output), BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addTier() {
        if (!ensureBudget()) {
            return;
        }

        Integer priority = promptInt("Enter Tier Priority:");
        if (priority == null) {
            return;
        }

        String name = promptString("Enter Tier Name:");
        if (name == null || name.isEmpty()) {
            message("Name cannot be empty.");
            return;
        }

        budget.createTier(priority);
        int newTierIndex = budget.getTiers().size() - 1;
        budget.getTier(newTierIndex).setTierName(name);
        renderBudget();
    }

    private void addRow() {
        if (!ensureBudget()) {
            return;
        }

        Integer tierIndex = promptInt("Choose Tier (0 = Income):");
        if (tierIndex == null) {
            return;
        }

        if (tierIndex < 0 || tierIndex >= budget.getTiers().size()) {
            message("Tier doesn't exist!");
            return;
        }

        String rowName = promptString("Enter Row Name:");
        if (rowName == null || rowName.isEmpty()) {
            message("Name cannot be empty.");
            return;
        }

        Double value = promptDouble("Enter Row Value:");
        if (value == null) {
            return;
        }

        budget.getTier(tierIndex).createRow(rowName, value);
        renderBudget();
    }

    private void editRow() {
        if (!ensureBudget()) {
            return;
        }

        Integer tierIndex = promptInt("Choose Tier (0 = Income):");
        if (tierIndex == null) {
            return;
        }

        if (tierIndex < 0 || tierIndex >= budget.getTiers().size()) {
            message("Tier doesn't exist!");
            return;
        }

        Tier tier = budget.getTier(tierIndex);
        if (tier.getExpenses().isEmpty()) {
            message("No rows in this tier.");
            return;
        }

        Integer rowIndex = promptInt("Choose Row Index:");
        if (rowIndex == null) {
            return;
        }

        if (rowIndex < 0 || rowIndex >= tier.getExpenses().size()) {
            message("Row doesn't exist!");
            return;
        }

        String rowName = promptString("Enter New Row Name:");
        if (rowName == null || rowName.isEmpty()) {
            message("Name cannot be empty.");
            return;
        }

        Double value = promptDouble("Enter New Row Value:");
        if (value == null) {
            return;
        }

        Row row = tier.getExpense(rowIndex);
        row.setRowName(rowName);
        row.setRowValue(value);
        renderBudget();
    }

    private void deleteRow() {
        if (!ensureBudget()) {
            return;
        }

        Integer tierIndex = promptInt("Choose Tier (0 = Income):");
        if (tierIndex == null) {
            return;
        }

        if (tierIndex < 0 || tierIndex >= budget.getTiers().size()) {
            message("Tier doesn't exist!");
            return;
        }

        Tier tier = budget.getTier(tierIndex);
        if (tier.getExpenses().isEmpty()) {
            message("No rows in this tier.");
            return;
        }

        Integer rowIndex = promptInt("Choose Row Index:");
        if (rowIndex == null) {
            return;
        }

        if (rowIndex < 0 || rowIndex >= tier.getExpenses().size()) {
            message("Row doesn't exist!");
            return;
        }

        tier.removeRow(rowIndex);
        renderBudget();
    }

    private void renderBudget() {
        if (budget == null) {
            output.setText("No budget created.\n");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===== BUDGET =====\n");

        for (int i = 0; i < budget.getTiers().size(); i++) {
            Tier tier = budget.getTiers().get(i);
            if (i == 0 && tier == budget.getIncomeTier()) {
                String displayName = tier.tierName == null || tier.tierName.isEmpty()
                        ? "Income"
                        : "Income - " + tier.tierName;
                sb.append(displayName).append(":\n");
            } else {
                String displayName = tier.tierName == null || tier.tierName.isEmpty()
                        ? "Tier " + i
                        : "Tier " + i + " - " + tier.tierName;
                sb.append(displayName)
                        .append(" (priority ")
                        .append(tier.getPriority())
                        .append("):\n");
            }

            for (int j = 0; j < tier.getExpenses().size(); j++) {
                Row row = tier.getExpenses().get(j);
                sb.append(String.format("  [%d] %s : %.2f%n", j, row.getRowName(), row.getRowValue()));
            }

            sb.append(String.format("  Tier total: %.2f%n%n", tier.calcTierExpenseTotal()));
        }

        double totalIncome = budget.getTotalIncome();
        double totalAllTiers = budget.getTotalTierTotals();
        double totalExpenses = totalAllTiers - totalIncome;
        sb.append(String.format("Total income: %.2f%n", totalIncome));
        sb.append(String.format("Total expenses: %.2f%n", totalExpenses));
        sb.append(String.format("Total revenue: %.2f%n", budget.getTotalRevenue()));

        output.setText(sb.toString());
    }

    private boolean ensureBudget() {
        if (budget == null) {
            message("Create a budget first!");
            return false;
        }
        return true;
    }

    private Integer promptInt(String message) {
        String input = JOptionPane.showInputDialog(null, message);
        if (input == null) {
            return null;
        }
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            message("Invalid number.");
            return null;
        }
    }

    private Double promptDouble(String message) {
        String input = JOptionPane.showInputDialog(null, message);
        if (input == null) {
            return null;
        }
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            message("Invalid number.");
            return null;
        }
    }

    private String promptString(String message) {
        String input = JOptionPane.showInputDialog(null, message);
        if (input == null) {
            return null;
        }
        return input.trim();
    }

    private void message(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}
