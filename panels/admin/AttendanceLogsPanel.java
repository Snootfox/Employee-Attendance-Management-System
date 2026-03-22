package panels.admin;

import navigation.NavigationManager;
import model.AttendanceRecord;
import model.AttendanceRecord.AttendanceStatus;
import model.Employee;
import panels.BasePanel;
import service.AttendanceService;
import service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AttendanceLogsPanel extends BasePanel {
    private final AttendanceService attendanceService;
    private final AuthService authService;
    private DefaultTableModel tableModel;
    private JComboBox<String> employeeCombo;

    public AttendanceLogsPanel(NavigationManager navManager,
                               AttendanceService attendanceService,
                               AuthService authService) {
        super(navManager);
        this.attendanceService = attendanceService;
        this.authService       = authService;
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

        JLabel title = new JLabel("Attendance Logs");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(new Color(15, 35, 65));

        JLabel subtitle = new JLabel("View records and mark absences for today");
        subtitle.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        header.add(title,    BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        // Mark Absent toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel toolbarLabel = new JLabel("Mark Absent:");
        toolbarLabel.setFont(new Font("Inter", Font.BOLD, 13));
        toolbarLabel.setForeground(new Color(15, 35, 65));

        employeeCombo = new JComboBox<>();
        employeeCombo.setFont(new Font("Inter", Font.PLAIN, 13));
        employeeCombo.setPreferredSize(new Dimension(250, 30));

        JButton markButton = new JButton("Mark as Absent");
        markButton.setFont(new Font("Inter", Font.BOLD, 13));
        markButton.setBackground(new Color(220, 53, 69));
        markButton.setForeground(Color.WHITE);
        markButton.setFocusPainted(false);
        markButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        markButton.setPreferredSize(new Dimension(150, 30));
        markButton.addActionListener(e -> handleMarkAbsent());

        toolbar.add(toolbarLabel);
        toolbar.add(employeeCombo);
        toolbar.add(markButton);

        // Table
        String[] columns = { "Employee ID", "Name", "Clock In", "Clock Out", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Inter", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(210, 225, 245));

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Inter", Font.BOLD, 13));
        tableHeader.setBackground(new Color(15, 35, 65));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(0, 36));
        tableHeader.setReorderingAllowed(false);

        // Color-coded rows by status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);

                String status = (String) t.getModel().getValueAt(row, 4);

                if (!isSelected) {
                    // Row background by status
                    if ("ON_TIME".equals(status)) {
                        setBackground(new Color(240, 255, 240));
                    } else if ("LATE".equals(status)) {
                        setBackground(new Color(255, 248, 230));
                    } else if ("ABSENT".equals(status)) {
                        setBackground(new Color(255, 235, 235));
                    } else {
                        setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 246, 248));
                    }

                    // Status column text color
                    if (col == 4) {
                        if ("ON_TIME".equals(status)) {
                            setForeground(new Color(30, 130, 70));
                        } else if ("LATE".equals(status)) {
                            setForeground(new Color(180, 100, 0));
                        } else if ("ABSENT".equals(status)) {
                            setForeground(new Color(180, 30, 30));
                        } else {
                            setForeground(new Color(30, 30, 30));
                        }
                        setFont(new Font("Inter", Font.BOLD, 12));
                    } else {
                        setForeground(new Color(30, 30, 30));
                        setFont(new Font("Inter", Font.PLAIN, 13));
                    }
                }

                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(col == 4 ? SwingConstants.CENTER : SwingConstants.LEFT);
                return this;
            }
        });

        int[] widths = { 100, 160, 170, 170, 90 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        legend.setOpaque(false);
        legend.add(legendDot(new Color(30, 130, 70),  "On Time"));
        legend.add(legendDot(new Color(180, 100, 0),  "Late"));
        legend.add(legendDot(new Color(180, 30, 30),  "Absent"));

        JPanel centerStack = new JPanel(new BorderLayout(0, 8));
        centerStack.setOpaque(false);
        centerStack.add(toolbar,    BorderLayout.NORTH);
        centerStack.add(scrollPane, BorderLayout.CENTER);
        centerStack.add(legend,     BorderLayout.SOUTH);

        add(header,      BorderLayout.NORTH);
        add(centerStack, BorderLayout.CENTER);
    }

    private void handleMarkAbsent() {
        String selected = (String) employeeCombo.getSelectedItem();
        if (selected == null || selected.isEmpty()) return;

        String employeeId = selected.split(" - ")[0].trim();
        Employee emp = authService.findEmployee(employeeId);
        String name = emp != null ? emp.getName() : employeeId;

        if (attendanceService.hasRecordToday(employeeId)) {
            JOptionPane.showMessageDialog(this,
                    name + " already has an attendance record for today.",
                    "Already Recorded", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark " + name + " as ABSENT for today?",
                "Confirm Absence", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            attendanceService.markAbsent(employeeId);
            JOptionPane.showMessageDialog(this,
                    name + " has been marked as Absent.",
                    "Marked Absent", JOptionPane.INFORMATION_MESSAGE);
            onShow();
        }
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

        // Refresh table — most recent first
        tableModel.setRowCount(0);
        List<AttendanceRecord> records = attendanceService.getAllRecords();
        for (int i = records.size() - 1; i >= 0; i--) {
            AttendanceRecord r = records.get(i);
            Employee emp = authService.findEmployee(r.getEmployeeId());
            String name = emp != null ? emp.getName() : "—";
            tableModel.addRow(new Object[]{
                    r.getEmployeeId(),
                    name,
                    r.getStatus() == AttendanceStatus.ABSENT ? "—" : r.getFormattedClockIn(),
                    r.getStatus() == AttendanceStatus.ABSENT ? "—" : r.getFormattedClockOut(),
                    r.getStatus().name()
            });
        }
    }

    private JLabel legendDot(final Color color, String text) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, (getHeight() - 10) / 2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        label.setFont(new Font("Inter", Font.PLAIN, 12));
        label.setForeground(new Color(80, 80, 80));
        return label;
    }
}