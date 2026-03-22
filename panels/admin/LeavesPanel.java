package panels.admin;

import navigation.NavigationManager;
import model.Employee;
import model.LeaveRecord;
import model.LeaveRecord.LeaveStatus;
import model.LeaveRecord.LeaveType;
import panels.BasePanel;
import service.AuthService;
import service.LeaveService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LeavesPanel extends BasePanel {
    private final LeaveService leaveService;
    private final AuthService  authService;
    private DefaultTableModel  tableModel;
    private JComboBox<String>  employeeCombo;
    private JSpinner           dateSpinner;
    private JComboBox<String>  typeCombo;
    private JTextField         reasonField;

    public LeavesPanel(NavigationManager navManager,
                       LeaveService leaveService,
                       AuthService authService) {
        super(navManager);
        this.leaveService = leaveService;
        this.authService  = authService;
        setBackground(new Color(230, 232, 235));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        // Header
        JLabel title = new JLabel("Leaves");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(new Color(15, 35, 65));

        JLabel subtitle = new JLabel("File and manage employee leave requests");
        subtitle.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        header.add(title,    BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        // File Leave form
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        employeeCombo = new JComboBox<>();
        employeeCombo.setPreferredSize(new Dimension(200, 30));
        employeeCombo.setFont(new Font("Inter", Font.PLAIN, 13));

        // Date spinner
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(130, 30));
        dateSpinner.setFont(new Font("Inter", Font.PLAIN, 13));

        typeCombo = new JComboBox<>(new String[]{"SICK", "VACATION", "EMERGENCY", "OTHER"});
        typeCombo.setPreferredSize(new Dimension(120, 30));
        typeCombo.setFont(new Font("Inter", Font.PLAIN, 13));

        reasonField = new JTextField(15);
        reasonField.setPreferredSize(new Dimension(160, 30));
        reasonField.setFont(new Font("Inter", Font.PLAIN, 13));

        JButton fileBtn = new JButton("File Leave");
        fileBtn.setFont(new Font("Inter", Font.BOLD, 13));
        fileBtn.setBackground(new Color(25, 50, 75));
        fileBtn.setForeground(Color.WHITE);
        fileBtn.setFocusPainted(false);
        fileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fileBtn.setPreferredSize(new Dimension(110, 30));
        fileBtn.addActionListener(e -> handleFileLeave());

        form.add(new JLabel("Employee:"));  form.add(employeeCombo);
        form.add(new JLabel("Date:"));      form.add(dateSpinner);
        form.add(new JLabel("Type:"));      form.add(typeCombo);
        form.add(new JLabel("Reason:"));    form.add(reasonField);
        form.add(fileBtn);

        // Table
        String[] cols = {"Employee ID", "Name", "Date", "Type", "Reason", "Status", "Action"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Inter", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Inter", Font.BOLD, 13));
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
                    String status = (String) t.getModel().getValueAt(row, 5);
                    if (col == 5) {
                        if ("APPROVED".equals(status)) {
                            setForeground(new Color(30, 130, 70));
                            setFont(new Font("Inter", Font.BOLD, 12));
                        } else if ("REJECTED".equals(status)) {
                            setForeground(new Color(180, 30, 30));
                            setFont(new Font("Inter", Font.BOLD, 12));
                        } else {
                            setForeground(new Color(180, 100, 0));
                            setFont(new Font("Inter", Font.BOLD, 12));
                        }
                    } else {
                        setForeground(new Color(30, 30, 30));
                        setFont(new Font("Inter", Font.PLAIN, 13));
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        // Approve/Reject buttons in Action column via row selection
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionPanel.setOpaque(false);

        JButton approveBtn = new JButton("Approve");
        approveBtn.setFont(new Font("Inter", Font.BOLD, 12));
        approveBtn.setBackground(new Color(40, 167, 69));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFocusPainted(false);
        approveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        approveBtn.addActionListener(e -> handleUpdateStatus(table, LeaveStatus.APPROVED));

        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setFont(new Font("Inter", Font.BOLD, 12));
        rejectBtn.setBackground(new Color(220, 53, 69));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFocusPainted(false);
        rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rejectBtn.addActionListener(e -> handleUpdateStatus(table, LeaveStatus.REJECTED));

        actionPanel.add(new JLabel("Selected row:"));
        actionPanel.add(approveBtn);
        actionPanel.add(rejectBtn);

        int[] widths = {90, 150, 100, 90, 160, 80, 90};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(scroll,       BorderLayout.CENTER);
        center.add(actionPanel,  BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(form,   BorderLayout.BEFORE_FIRST_LINE);

        JPanel mainContent = new JPanel(new BorderLayout(0, 10));
        mainContent.setOpaque(false);
        mainContent.add(form,   BorderLayout.NORTH);
        mainContent.add(center, BorderLayout.CENTER);

        add(header,      BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }

    private void handleFileLeave() {
        String selected = (String) employeeCombo.getSelectedItem();
        if (selected == null) return;

        String employeeId = selected.split(" - ")[0].trim();
        java.util.Date spinnerDate = (java.util.Date) dateSpinner.getValue();
        LocalDate date = spinnerDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LeaveType type = LeaveType.valueOf((String) typeCombo.getSelectedItem());
        String reason  = reasonField.getText().trim();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a reason.", "Missing Reason", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean filed = leaveService.fileLeave(employeeId, date, type, reason);
        if (!filed) {
            JOptionPane.showMessageDialog(this,
                    "A leave record already exists for this employee on that date.",
                    "Duplicate Leave", JOptionPane.WARNING_MESSAGE);
            return;
        }

        reasonField.setText("");
        JOptionPane.showMessageDialog(this, "Leave filed successfully.", "Filed", JOptionPane.INFORMATION_MESSAGE);
        onShow();
    }

    private void handleUpdateStatus(JTable table, LeaveStatus newStatus) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String employeeId = (String) tableModel.getValueAt(row, 0);
        String dateStr    = (String) tableModel.getValueAt(row, 2);
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (Exception ex) {
            return;
        }

        leaveService.updateStatus(employeeId, date, newStatus);
        onShow();
    }

    @Override
    public void onShow() {
        // Refresh employee dropdown
        employeeCombo.removeAllItems();
        Map<String, Employee> employees = authService.getAllEmployees();
        List<Employee> sorted = new ArrayList<>(employees.values());
        sorted.sort((a, b) -> a.getId().compareTo(b.getId()));
        for (Employee e : sorted) {
            employeeCombo.addItem(e.getId() + " - " + e.getName());
        }

        // Refresh table
        tableModel.setRowCount(0);
        List<LeaveRecord> leaves = leaveService.getAllLeaves();
        leaves.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        for (LeaveRecord l : leaves) {
            Employee emp = authService.findEmployee(l.getEmployeeId());
            String name  = emp != null ? emp.getName() : "—";
            tableModel.addRow(new Object[]{
                    l.getEmployeeId(), name, l.getFormattedDate(),
                    l.getType().name(), l.getReason(), l.getStatus().name(), ""
            });
        }
    }
}