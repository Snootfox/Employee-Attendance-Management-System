package panels.admin;

import navigation.NavigationManager;
import model.Employee;
import panels.BasePanel;
import service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeePanel extends BasePanel {
    private final AuthService authService;
    private DefaultTableModel tableModel;
    private JLabel countLabel;

    // Standard shift for all employees
    private static final String STANDARD_SHIFT = "8:00 AM – 5:00 PM";
    private static final String WORK_DAYS      = "Mon – Fri";

    public EmployeePanel(NavigationManager navManager, AuthService authService) {
        super(navManager);
        this.authService = authService;
        setBackground(new Color(230, 232, 235));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setLayout(new BorderLayout(0, 20));
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Employees");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(new Color(15, 35, 65));

        JLabel subtitle = new JLabel("All employees are on a standard shift: "
                + STANDARD_SHIFT + "  |  " + WORK_DAYS);
        subtitle.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        header.add(title,    BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        // Table
        String[] columns = { "#", "Employee ID", "Name", "Department", "Shift", "Work Days" };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Inter", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(210, 225, 245));
        table.setSelectionForeground(new Color(15, 35, 65));

        // Header styling
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Inter", Font.BOLD, 13));
        tableHeader.setBackground(new Color(15, 35, 65));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(0, 36));
        tableHeader.setReorderingAllowed(false);

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 246, 248));
                    setForeground(new Color(30, 30, 30));
                }
                // Highlight shift and work days columns in soft green
                if ((col == 4 || col == 5) && !isSelected) {
                    setBackground(new Color(235, 250, 235));
                    setForeground(new Color(20, 110, 50));
                    setFont(new Font("Inter", Font.PLAIN, 12));
                } else {
                    setFont(new Font("Inter", Font.PLAIN, 13));
                }
                setHorizontalAlignment(col == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        // Column widths
        int[] widths = { 40, 100, 180, 120, 160, 100 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Summary bar
        JPanel summaryBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        summaryBar.setOpaque(false);
        countLabel = new JLabel();
        countLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        countLabel.setForeground(new Color(100, 100, 100));
        summaryBar.add(countLabel);

        add(header,     BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(summaryBar, BorderLayout.SOUTH);
    }

    @Override
    public void onShow() {
        tableModel.setRowCount(0);
        Map<String, Employee> employees = authService.getAllEmployees();

        List<Employee> sorted = new ArrayList<>(employees.values());
        sorted.sort((a, b) -> a.getId().compareTo(b.getId()));

        int rowNum = 1;
        for (Employee emp : sorted) {
            tableModel.addRow(new Object[]{
                    rowNum++,
                    emp.getId(),
                    emp.getName(),
                    emp.getDepartment(),
                    STANDARD_SHIFT,
                    WORK_DAYS
            });
        }

        countLabel.setText("Total: " + employees.size() + " employee(s)");
    }
}
