import budget.Budget;
import budget.Row;
import budget.Tier;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Swing_UI {
    private Budget budget;
    private JTable table;
    private BudgetTableModel tableModel;
    private final JLabel summaryLabel = new JLabel("No budget created.");
    private final SortState sortState = new SortState();

    public void run() {
        SwingUtilities.invokeLater(this::createAndShowUI);
    }

    private void createAndShowUI() {
        JFrame frame = new JFrame("budget.Budget GUI");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        tableModel = new BudgetTableModel();
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        BudgetCellRenderer renderer = new BudgetCellRenderer(tableModel);
        table.setDefaultRenderer(Object.class, renderer);
        table.setDefaultRenderer(Double.class, renderer);

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                toggleSort(column);
            }
        });

        installRowDeleteAction();
        updateTierEditor();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(new JButton(new AbstractAction("Create budget.Budget") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                budget = new Budget();
                renderBudget();
            }
        }));
        buttonPanel.add(new JButton(new AbstractAction("Add budget.Tier") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                addTier();
            }
        }));
        buttonPanel.add(new JButton(new AbstractAction("Add budget.Row") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                addRow();
            }
        }));
        buttonPanel.add(new JButton(new AbstractAction("Edit budget.Row") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                editRow();
            }
        }));
        buttonPanel.add(new JButton(new AbstractAction("Delete budget.Row") {
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
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(summaryLabel, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addTier() {
        if (!ensureBudget()) {
            return;
        }

        Integer priority = promptInt("Enter budget.Tier Priority:");
        if (priority == null) {
            return;
        }

        String name = promptString("Enter budget.Tier Name:");
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

        Integer tierIndex = promptInt("Choose budget.Tier (0 = Income):");
        if (tierIndex == null) {
            return;
        }

        if (tierIndex < 0 || tierIndex >= budget.getTiers().size()) {
            message("budget.Tier doesn't exist!");
            return;
        }

        String rowName = promptString("Enter budget.Row Name:");
        if (rowName == null || rowName.isEmpty()) {
            message("Name cannot be empty.");
            return;
        }

        Double value = promptDouble("Enter budget.Row Value:");
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

        Integer tierIndex = promptInt("Choose budget.Tier (0 = Income):");
        if (tierIndex == null) {
            return;
        }

        if (tierIndex < 0 || tierIndex >= budget.getTiers().size()) {
            message("budget.Tier doesn't exist!");
            return;
        }

        Tier tier = budget.getTier(tierIndex);
        if (tier.getExpenses().isEmpty()) {
            message("No rows in this tier.");
            return;
        }

        Integer rowIndex = promptInt("Choose budget.Row Index:");
        if (rowIndex == null) {
            return;
        }

        if (rowIndex < 0 || rowIndex >= tier.getExpenses().size()) {
            message("budget.Row doesn't exist!");
            return;
        }

        String rowName = promptString("Enter New budget.Row Name:");
        if (rowName == null || rowName.isEmpty()) {
            message("Name cannot be empty.");
            return;
        }

        Double value = promptDouble("Enter New budget.Row Value:");
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

        Integer tierIndex = promptInt("Choose budget.Tier (0 = Income):");
        if (tierIndex == null) {
            return;
        }

        if (tierIndex < 0 || tierIndex >= budget.getTiers().size()) {
            message("budget.Tier doesn't exist!");
            return;
        }

        Tier tier = budget.getTier(tierIndex);
        if (tier.getExpenses().isEmpty()) {
            message("No rows in this tier.");
            return;
        }

        Integer rowIndex = promptInt("Choose budget.Row Index:");
        if (rowIndex == null) {
            return;
        }

        if (rowIndex < 0 || rowIndex >= tier.getExpenses().size()) {
            message("budget.Row doesn't exist!");
            return;
        }

        tier.removeRow(rowIndex);
        renderBudget();
    }

    private void renderBudget() {
        if (budget == null) {
            tableModel.clear();
            summaryLabel.setText("No budget created.");
            return;
        }

        tableModel.rebuild(budget, sortState);
        updateTierEditor();

        double totalIncome = budget.getTotalIncome();
        double totalAllTiers = budget.getTotalTierTotals();
        double totalExpenses = totalAllTiers - totalIncome;
        summaryLabel.setText(String.format(
                "Total income: %.2f    Total expenses: %.2f    Total revenue: %.2f",
                totalIncome, totalExpenses, budget.getTotalRevenue()
        ));
    }

    private void toggleSort(int column) {
        if (budget == null) {
            return;
        }

        if (column == BudgetTableModel.COL_TIER
                || column == BudgetTableModel.COL_ROW
                || column == BudgetTableModel.COL_AMOUNT) {
            sortState.toggle(column);
            renderBudget();
        }
    }

    private void installRowDeleteAction() {
        table.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("DELETE"), "deleteRow");
        table.getActionMap().put("deleteRow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedRow();
            }
        });
    }

    private void deleteSelectedRow() {
        if (budget == null) {
            return;
        }

        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        BudgetTableModel.Entry entry = tableModel.getEntry(modelRow);
        if (entry.type != BudgetTableModel.RowType.DATA) {
            return;
        }

        entry.tier.getExpenses().remove(entry.row);
        renderBudget();
    }

    private void updateTierEditor() {
        if (table == null) {
            return;
        }

        JComboBox<Tier> combo = new JComboBox<>();
        if (budget != null) {
            for (Tier tier : budget.getTiers()) {
                combo.addItem(tier);
            }
        }

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tier) {
                    setText(tableModel.getTierLabel((Tier) value));
                }
                return this;
            }
        });

        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(combo));
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

    private static final class SortState {
        private int column = -1;
        private boolean ascending = true;

        void toggle(int column) {
            if (this.column == column) {
                ascending = !ascending;
            } else {
                this.column = column;
                ascending = true;
            }
        }

        boolean isActive() {
            return column != -1;
        }
    }

    private static final class BudgetTableModel extends AbstractTableModel {
        static final int COL_TIER = 0;
        static final int COL_ROW = 1;
        static final int COL_AMOUNT = 2;

        enum RowType {
            HEADER,
            DATA,
            TOTAL,
            NEW_ROW
        }

        static final class Entry {
            final RowType type;
            Tier tier;
            Row row;
            String label;
            Double total;
            Tier newTier;
            String newRowName;
            Double newAmount;

            private Entry(RowType type) {
                this.type = type;
            }

            static Entry header(Tier tier, String label) {
                Entry entry = new Entry(RowType.HEADER);
                entry.tier = tier;
                entry.label = label;
                return entry;
            }

            static Entry data(Tier tier, Row row) {
                Entry entry = new Entry(RowType.DATA);
                entry.tier = tier;
                entry.row = row;
                return entry;
            }

            static Entry total(Tier tier, double total) {
                Entry entry = new Entry(RowType.TOTAL);
                entry.tier = tier;
                entry.total = total;
                return entry;
            }

            static Entry newRow() {
                return new Entry(RowType.NEW_ROW);
            }
        }

        private final List<Entry> entries = new ArrayList<>();
        private final Map<Tier, String> tierLabels = new HashMap<>();
        private Budget budget;
        private SortState sortState;

        @Override
        public int getRowCount() {
            return entries.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            return switch (column) {
                case COL_TIER -> "Tier";
                case COL_ROW -> "Row";
                case COL_AMOUNT -> "Amount";
                default -> "";
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case COL_AMOUNT -> Double.class;
                default -> Object.class;
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            Entry entry = entries.get(rowIndex);
            return (entry.type == RowType.DATA || entry.type == RowType.NEW_ROW)
                    && (columnIndex == COL_TIER || columnIndex == COL_ROW || columnIndex == COL_AMOUNT);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Entry entry = entries.get(rowIndex);
            return switch (entry.type) {
                case HEADER -> columnIndex == COL_TIER ? entry.label : "";
                case TOTAL -> switch (columnIndex) {
                    case COL_ROW -> "Tier total";
                    case COL_AMOUNT -> entry.total;
                    default -> "";
                };
                case DATA -> switch (columnIndex) {
                    case COL_TIER -> entry.tier;
                    case COL_ROW -> entry.row.getRowName();
                    case COL_AMOUNT -> entry.row.getRowValue();
                    default -> "";
                };
                case NEW_ROW -> switch (columnIndex) {
                    case COL_TIER -> entry.newTier;
                    case COL_ROW -> entry.newRowName == null ? "" : entry.newRowName;
                    case COL_AMOUNT -> entry.newAmount;
                    default -> "";
                };
            };
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Entry entry = entries.get(rowIndex);
            if (entry.type == RowType.DATA) {
                handleDataEdit(entry, aValue, columnIndex);
                return;
            }

            if (entry.type == RowType.NEW_ROW) {
                handleNewRowEdit(entry, aValue, columnIndex);
            }
        }

        Entry getEntry(int rowIndex) {
            return entries.get(rowIndex);
        }

        String getTierLabel(Tier tier) {
            return tierLabels.getOrDefault(tier, "");
        }

        void clear() {
            entries.clear();
            tierLabels.clear();
            fireTableDataChanged();
        }

        void rebuild(Budget budget, SortState sortState) {
            this.budget = budget;
            this.sortState = sortState;
            entries.clear();
            tierLabels.clear();

            if (budget == null) {
                fireTableDataChanged();
                return;
            }

            List<Tier> tiers = new ArrayList<>(budget.getTiers());
            if (sortState.isActive() && sortState.column == COL_TIER) {
                tiers.sort(java.util.Comparator.comparing(tier -> buildTierLabel(budget, tier,
                        budget.getTiers().indexOf(tier), false), String.CASE_INSENSITIVE_ORDER));
            }

            for (int i = 0; i < tiers.size(); i++) {
                Tier tier = tiers.get(i);
                int tierIndex = budget.getTiers().indexOf(tier);
                String baseLabel = buildTierLabel(budget, tier, tierIndex, false);
                String headerLabel = buildTierLabel(budget, tier, tierIndex, true);
                tierLabels.put(tier, baseLabel);

                entries.add(Entry.header(tier, headerLabel));

                List<Row> rows = new ArrayList<>(tier.getExpenses());
                if (sortState.isActive() && sortState.column != COL_TIER) {
                    rows.sort(createRowComparator(sortState));
                }

                for (Row row : rows) {
                    entries.add(Entry.data(tier, row));
                }

                if (!rows.isEmpty()) {
                    entries.add(Entry.total(tier, tier.calcTierExpenseTotal()));
                }
            }

            entries.add(Entry.newRow());
            fireTableDataChanged();
        }

        private void handleDataEdit(Entry entry, Object aValue, int columnIndex) {
            if (columnIndex == COL_TIER && aValue instanceof Tier) {
                Tier newTier = (Tier) aValue;
                if (newTier != entry.tier) {
                    entry.tier.getExpenses().remove(entry.row);
                    newTier.getExpenses().add(entry.row);
                    entry.tier = newTier;
                    refresh();
                }
                return;
            }

            if (columnIndex == COL_ROW) {
                String name = aValue == null ? "" : aValue.toString().trim();
                if (!name.isEmpty()) {
                    entry.row.setRowName(name);
                    refresh();
                }
                return;
            }

            if (columnIndex == COL_AMOUNT) {
                Double amount = parseDouble(aValue);
                if (amount != null) {
                    entry.row.setRowValue(amount);
                    refresh();
                }
            }
        }

        private void handleNewRowEdit(Entry entry, Object aValue, int columnIndex) {
            if (columnIndex == COL_TIER && aValue instanceof Tier) {
                entry.newTier = (Tier) aValue;
            } else if (columnIndex == COL_ROW) {
                String name = aValue == null ? "" : aValue.toString().trim();
                entry.newRowName = name;
            } else if (columnIndex == COL_AMOUNT) {
                entry.newAmount = parseDouble(aValue);
            }

            if (entry.newTier != null && entry.newRowName != null
                    && !entry.newRowName.isEmpty() && entry.newAmount != null) {
                entry.newTier.createRow(entry.newRowName, entry.newAmount);
                entry.newTier = null;
                entry.newRowName = null;
                entry.newAmount = null;
                refresh();
            }
        }

        private void refresh() {
            rebuild(budget, sortState);
        }

        private static Double parseDouble(Object value) {
            if (value == null) {
                return null;
            }
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            try {
                return Double.parseDouble(value.toString().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        private static java.util.Comparator<Row> createRowComparator(SortState sortState) {
            java.util.Comparator<Row> comparator;
            if (sortState.column == COL_AMOUNT) {
                comparator = java.util.Comparator.comparingDouble(Row::getRowValue);
            } else {
                comparator = java.util.Comparator.comparing(Row::getRowName, String.CASE_INSENSITIVE_ORDER);
            }

            if (!sortState.ascending) {
                comparator = comparator.reversed();
            }
            return comparator;
        }

        private static String buildTierLabel(Budget budget, Tier tier, int index, boolean includePriority) {
            String baseName;
            if (index == 0 && tier == budget.getIncomeTier()) {
                baseName = (tier.getTierName() == null || tier.getTierName().isEmpty())
                        ? "Income"
                        : "Income - " + tier.getTierName();
            } else {
                baseName = (tier.getTierName() == null || tier.getTierName().isEmpty())
                        ? "budget.Tier " + index
                        : "budget.Tier " + index + " - " + tier.getTierName();
            }

            if (includePriority && !(index == 0 && tier == budget.getIncomeTier())) {
                return baseName + " (priority " + tier.getPriority() + ")";
            }
            return baseName;
        }
    }

    private static final class BudgetCellRenderer extends DefaultTableCellRenderer {
        private final BudgetTableModel model;
        private final NumberFormat numberFormat = NumberFormat.getNumberInstance();

        BudgetCellRenderer(BudgetTableModel model) {
            this.model = model;
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setMaximumFractionDigits(2);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            int modelRow = table.convertRowIndexToModel(row);
            BudgetTableModel.Entry entry = model.getEntry(modelRow);

            setHorizontalAlignment(column == BudgetTableModel.COL_AMOUNT ? RIGHT : LEFT);

            if (column == BudgetTableModel.COL_AMOUNT) {
                if (value instanceof Number) {
                    setText(numberFormat.format(((Number) value).doubleValue()));
                } else {
                    setText("");
                }
            } else if (value instanceof Tier) {
                setText(model.getTierLabel((Tier) value));
            }

            if (!isSelected) {
                if (entry.type == BudgetTableModel.RowType.HEADER) {
                    setFont(getFont().deriveFont(Font.BOLD));
                    setBackground(new Color(230, 230, 230));
                } else if (entry.type == BudgetTableModel.RowType.TOTAL) {
                    setFont(getFont().deriveFont(Font.ITALIC));
                    setBackground(table.getBackground());
                } else if (entry.type == BudgetTableModel.RowType.NEW_ROW) {
                    setFont(getFont().deriveFont(Font.ITALIC));
                    setBackground(table.getBackground());
                    if (column == BudgetTableModel.COL_ROW && (value == null || value.toString().isEmpty())) {
                        setText("Add new row...");
                        setForeground(new Color(120, 120, 120));
                    } else {
                        setForeground(table.getForeground());
                    }
                } else {
                    setFont(table.getFont());
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
            }

            return this;
        }
    }
}
