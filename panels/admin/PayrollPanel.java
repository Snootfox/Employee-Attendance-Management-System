package panels.admin;

import navigation.NavigationManager;
import model.AttendanceRecord;
import model.Employee;
import panels.BasePanel;
import service.AttendanceService;
import service.AuthService;
import service.LeaveService;
import service.PayrollService;
import service.PayrollService.PayrollSummary;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.List;



public class PayrollPanel extends BasePanel {
    private final AttendanceService attendanceService;
    private final AuthService       authService;
    private final LeaveService      leaveService;
    private final PayrollService    payrollService;
    private DefaultTableModel tableModel;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JLabel totalLabel;

    private static final NumberFormat PHP = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    public PayrollPanel(NavigationManager navManager,
                        AttendanceService attendanceService,
                        AuthService authService,
                        LeaveService leaveService,
                        PayrollService payrollService) {
        super(navManager);
        this.attendanceService = attendanceService;
        this.authService       = authService;
        this.leaveService      = leaveService;
        this.payrollService    = payrollService;
        setBackground(new Color(230, 232, 235));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        // Header
        JLabel title = new JLabel("Payroll");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(new Color(15, 35, 65));

        JLabel subtitle = new JLabel("Monthly payroll with late deductions per 30-minute block");
        subtitle.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        header.add(title,    BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        String[] months = {"January","February","March","April","May","June",
                "July","August","September","October","November","December"};
        monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        monthCombo.setPreferredSize(new Dimension(130, 30));
        monthCombo.setFont(new Font("Inter", Font.PLAIN, 13));

        int currentYear = LocalDate.now().getYear();
        yearCombo = new JComboBox<>();
        for (int y = currentYear - 2; y <= currentYear; y++) yearCombo.addItem(y);
        yearCombo.setSelectedItem(currentYear);
        yearCombo.setPreferredSize(new Dimension(80, 30));
        yearCombo.setFont(new Font("Inter", Font.PLAIN, 13));

        JButton generateBtn = new JButton("Generate");
        generateBtn.setFont(new Font("Inter", Font.BOLD, 13));
        generateBtn.setBackground(new Color(25, 50, 75));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFocusPainted(false);
        generateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateBtn.setPreferredSize(new Dimension(100, 30));
        generateBtn.addActionListener(e -> refreshTable());

        toolbar.add(new JLabel("Month:"));  toolbar.add(monthCombo);
        toolbar.add(new JLabel("Year:"));   toolbar.add(yearCombo);
        toolbar.add(generateBtn);

        // Table
        String[] cols = {"#","Employee ID","Name","Dept","Daily Rate",
                "Days Present","Days Late","Absent","On Leave",
                "Gross Pay","Late Deduction","Absent Deduction","Net Pay"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Inter", Font.PLAIN, 12));
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Inter", Font.BOLD, 12));
        th.setBackground(new Color(15, 35, 65));
        th.setForeground(Color.WHITE);
        th.setPreferredSize(new Dimension(0, 36));
        th.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 246, 248));
                    // Highlight net pay column
                    if (col == 12) {
                        setForeground(new Color(20, 110, 50));
                        setFont(new Font("Inter", Font.BOLD, 12));
                    } else if (col == 10 || col == 11) {
                        setForeground(new Color(180, 30, 30));
                        setFont(new Font("Inter", Font.PLAIN, 12));
                    } else {
                        setForeground(new Color(30, 30, 30));
                        setFont(new Font("Inter", Font.PLAIN, 12));
                    }
                }
                setHorizontalAlignment(col >= 4 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        int[] widths = {30, 80, 140, 100, 90, 90, 80, 70, 70, 90, 110, 120, 90};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scroll.getViewport().setBackground(Color.WHITE);

        // Total bar
        totalLabel = new JLabel("Total Net Pay: —");
        totalLabel.setFont(new Font("Inter", Font.BOLD, 14));
        totalLabel.setForeground(new Color(15, 35, 65));

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);
        bottomBar.add(totalLabel);

        JPanel mainContent = new JPanel(new BorderLayout(0, 10));
        mainContent.setOpaque(false);
        mainContent.add(toolbar,    BorderLayout.NORTH);
        mainContent.add(scroll,     BorderLayout.CENTER);
        mainContent.add(bottomBar,  BorderLayout.SOUTH);

        add(header,      BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        int month = monthCombo.getSelectedIndex() + 1;
        int year  = (Integer) yearCombo.getSelectedItem();

        Map<String, Employee> employees = authService.getAllEmployees();
        List<Employee> sorted = new ArrayList<>(employees.values());
        sorted.sort((a, b) -> a.getId().compareTo(b.getId()));

        double totalNet = 0;
        int rowNum = 1;

        for (Employee emp : sorted) {
            List<AttendanceRecord> records = attendanceService.getRecordsForEmployee(emp.getId());
            long approvedLeaves = leaveService.countApprovedLeavesInMonth(emp.getId(), year, month);
            PayrollSummary s = payrollService.compute(emp, records, approvedLeaves, year, month);

            tableModel.addRow(new Object[]{
                    rowNum++,
                    s.employeeId, s.name, s.department,
                    PHP.format(s.dailyRate),
                    s.daysPresent, s.daysLate, s.daysAbsent, s.daysOnLeave,
                    PHP.format(s.grossPay),
                    s.lateDeduction  > 0 ? "-" + PHP.format(s.lateDeduction)  : PHP.format(0),
                    s.absentDeduction > 0 ? "-" + PHP.format(s.absentDeduction) : PHP.format(0),
                    PHP.format(s.netPay)
            });
            totalNet += s.netPay;
        }
        totalLabel.setText("Total Net Pay: " + PHP.format(totalNet)
                + "   |   " + Month.of(month).name().charAt(0)
                + Month.of(month).name().substring(1).toLowerCase() + " " + year);
    }

    @Override
    public void onShow() {
        refreshTable();
    }
}
