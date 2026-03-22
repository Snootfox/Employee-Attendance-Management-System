package panels;

import navigation.NavigationManager;
import model.AttendanceRecord;
import model.Employee;
import service.AttendanceService;
import ui.StyledComponents;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserPanel extends BasePanel {
    private final AttendanceService attendanceService;
    private JLabel dateLabel;
    private JLabel timeLabel;
    private JLabel welcomeLabel;
    private Employee currentEmployee;

    public UserPanel(NavigationManager navManager, AttendanceService attendanceService) {
        super(navManager);
        this.attendanceService = attendanceService;
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());
        buildUI();
    }

    public void setEmployee(Employee employee) {
        this.currentEmployee = employee;
        welcomeLabel.setText("Welcome, " + employee.getName() + "!");
    }

    private void buildUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 10, 5, 10);

        welcomeLabel = new JLabel("", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Inter", Font.BOLD, 18));
        welcomeLabel.setForeground(StyledComponents.PRIMARY_COLOR);

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Inter", Font.PLAIN, 15));
        dateLabel.setForeground(new Color(100, 100, 100));

        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Inter", Font.BOLD, 35));
        timeLabel.setForeground(StyledComponents.PRIMARY_COLOR);

        startClock();

        JLabel clockIcon = buildClockIcon();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setOpaque(false);

        JButton clockIn   = StyledComponents.actionButton("CLOCK IN");
        JButton clockOut  = StyledComponents.actionButton("CLOCK OUT");
        JButton logoutBtn = StyledComponents.mutedButton("LOGOUT");

        clockIn.addActionListener(e   -> handleClockIn());
        clockOut.addActionListener(e  -> handleClockOut());
        logoutBtn.addActionListener(e -> {
            currentEmployee = null;
            navManager.navigateTo("MAIN");
        });

        buttonPanel.add(clockIn);
        buttonPanel.add(clockOut);

        gbc.gridy = 0; add(welcomeLabel, gbc);
        gbc.gridy = 1; add(dateLabel,    gbc);
        gbc.gridy = 2; add(timeLabel,    gbc);
        gbc.gridy = 3; gbc.insets = new Insets(20, 0, 20, 0);
        add(clockIcon,   gbc);
        gbc.gridy = 4; gbc.insets = new Insets(5, 10, 5, 10);
        add(buttonPanel, gbc);
        gbc.gridy = 5;
        add(logoutBtn,   gbc);
    }

    private void handleClockIn() {
        if (currentEmployee == null) {
            JOptionPane.showMessageDialog(this, "No employee logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (attendanceService.isClockedIn(currentEmployee.getId())) {
            JOptionPane.showMessageDialog(this,
                    currentEmployee.getName() + " is already clocked in today.",
                    "Already Clocked In", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AttendanceRecord record = attendanceService.clockIn(currentEmployee);
        JOptionPane.showMessageDialog(this,
                "Successfully Clocked In!\n" +
                        "Employee: " + currentEmployee.getName() + "\n" +
                        "Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a, MMM dd yyyy")) + "\n" +
                        "Status: " + record.getStatus().name(),
                "Clock In", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleClockOut() {
        if (currentEmployee == null) {
            JOptionPane.showMessageDialog(this, "No employee logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AttendanceRecord record = attendanceService.clockOut(currentEmployee.getId());
        if (record == null) {
            JOptionPane.showMessageDialog(this,
                    currentEmployee.getName() + " has not clocked in yet.",
                    "Not Clocked In", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Successfully Clocked Out!\n" +
                        "Employee: " + currentEmployee.getName() + "\n" +
                        "Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a, MMM dd yyyy")),
                "Clock Out", JOptionPane.INFORMATION_MESSAGE);
        currentEmployee = null;
        navManager.navigateTo("MAIN");
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            dateLabel.setText(now.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd")));
            timeLabel.setText(now.format(DateTimeFormatter.ofPattern("h:mm a")).toUpperCase());
        });
        timer.start();
    }

    private JLabel buildClockIcon() {
        return new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 50, 90));
                g2.setStroke(new BasicStroke(7f));
                int size = 120;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.drawOval(x, y, size, size);
                g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(getWidth() / 2, getHeight() / 2, getWidth() / 2, getHeight() / 2 - 35);
                g2.drawLine(getWidth() / 2, getHeight() / 2, getWidth() / 2 + 30, getHeight() / 2);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(160, 160); }
        };
    }
}