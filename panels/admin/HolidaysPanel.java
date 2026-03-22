package panels.admin;

import navigation.NavigationManager;
import model.Holiday;
import panels.BasePanel;
import service.HolidayService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;


public class HolidaysPanel extends BasePanel {
    private final HolidayService holidayService;
    private DefaultTableModel tableModel;
    private JSpinner  dateSpinner;
    private JTextField nameField;

    public HolidaysPanel(NavigationManager navManager, HolidayService holidayService) {
        super(navManager);
        this.holidayService = holidayService;
        setBackground(new Color(230, 232, 235));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        // Header
        JLabel title = new JLabel("Holidays");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(new Color(15, 35, 65));

        JLabel subtitle = new JLabel("Manage public holidays — employees are not marked absent on these dates");
        subtitle.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        header.add(title,    BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        // Add holiday form
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(130, 30));
        dateSpinner.setFont(new Font("Inter", Font.PLAIN, 13));

        nameField = new JTextField(20);
        nameField.setPreferredSize(new Dimension(200, 30));
        nameField.setFont(new Font("Inter", Font.PLAIN, 13));

        JButton addBtn = new JButton("Add Holiday");
        addBtn.setFont(new Font("Inter", Font.BOLD, 13));
        addBtn.setBackground(new Color(25, 50, 75));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setPreferredSize(new Dimension(120, 30));
        addBtn.addActionListener(e -> handleAdd());

        form.add(new JLabel("Date:"));    form.add(dateSpinner);
        form.add(new JLabel("Name:"));    form.add(nameField);
        form.add(addBtn);

        // Table
        String[] cols = { "#", "Date", "Holiday Name", "Day" };
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
                    // Highlight today's holiday
                    String dateStr = (String) t.getModel().getValueAt(row, 1);
                    boolean isToday = dateStr.equals(LocalDate.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                    if (isToday) {
                        setBackground(new Color(255, 250, 180));
                        setFont(new Font("Inter", Font.BOLD, 13));
                        setForeground(new Color(120, 90, 0));
                    } else {
                        setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 246, 248));
                        setFont(new Font("Inter", Font.PLAIN, 13));
                        setForeground(new Color(30, 30, 30));
                    }
                }
                setHorizontalAlignment(col == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        int[] widths = { 40, 120, 280, 100 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Remove button
        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setFont(new Font("Inter", Font.BOLD, 12));
        removeBtn.setBackground(new Color(220, 53, 69));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> handleRemove(table));

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomBar.setOpaque(false);
        bottomBar.add(removeBtn);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel mainContent = new JPanel(new BorderLayout(0, 10));
        mainContent.setOpaque(false);
        mainContent.add(form,      BorderLayout.NORTH);
        mainContent.add(scroll,    BorderLayout.CENTER);
        mainContent.add(bottomBar, BorderLayout.SOUTH);

        add(header,      BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }

    private void handleAdd() {
        java.util.Date spinnerDate = (java.util.Date) dateSpinner.getValue();
        LocalDate date = spinnerDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a holiday name.", "Missing Name", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!holidayService.addHoliday(date, name)) {
            JOptionPane.showMessageDialog(this, "A holiday already exists on that date.", "Duplicate", JOptionPane.WARNING_MESSAGE);
            return;
        }
        nameField.setText("");
        onShow();
    }

    private void handleRemove(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a holiday to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String dateStr = (String) tableModel.getValueAt(row, 1);
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (Exception ex) { return; }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove holiday on " + dateStr + "?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            holidayService.removeHoliday(date);
            onShow();
        }
    }

    @Override
    public void onShow() {
        tableModel.setRowCount(0);
        List<Holiday> holidays = holidayService.getAllHolidays();
        holidays.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        int num = 1;
        for (Holiday h : holidays) {
            tableModel.addRow(new Object[]{
                    num++,
                    h.getFormattedDate(),
                    h.getName(),
                    h.getDate().getDayOfWeek().toString().charAt(0)
                            + h.getDate().getDayOfWeek().toString().substring(1).toLowerCase()
            });
        }
    }
}