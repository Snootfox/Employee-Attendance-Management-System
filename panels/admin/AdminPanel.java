package panels.admin;

import navigation.NavigationManager;
import panels.BasePanel;
import service.*;
import ui.StyledComponents;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminPanel extends BasePanel {
    private final AttendanceService attendanceService;
    private final AuthService       authService;
    private final LeaveService      leaveService;
    private final HolidayService    holidayService;
    private final PayrollService    payrollService;

    private CardLayout adminLayout;
    private JPanel     adminCards;
    private final Map<String, BasePanel> subPanels = new LinkedHashMap<>();

    public AdminPanel(NavigationManager navManager,
                      AttendanceService attendanceService,
                      AuthService authService,
                      LeaveService leaveService,
                      HolidayService holidayService,
                      PayrollService payrollService) {
        super(navManager);
        this.attendanceService = attendanceService;
        this.authService       = authService;
        this.leaveService      = leaveService;
        this.holidayService    = holidayService;
        this.payrollService    = payrollService;
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel sidebar     = buildSidebar();
        JPanel contentArea = buildContentArea();

        JButton logoutButton = StyledComponents.dangerButton("LOGOUT");
        logoutButton.addActionListener(e -> navManager.navigateTo("MAIN"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(StyledComponents.SURFACE_COLOR);
        bottomPanel.add(logoutButton);

        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.add(contentArea, BorderLayout.CENTER);
        rightWrapper.add(bottomPanel, BorderLayout.SOUTH);

        add(sidebar,      BorderLayout.WEST);
        add(rightWrapper, BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(220, 1000));

        JLabel amsLabel = new JLabel("AMS");
        amsLabel.setFont(new Font("Inter", Font.BOLD, 48));
        amsLabel.setForeground(new Color(15, 35, 65));

        JPanel amsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        amsPanel.setBackground(Color.WHITE);
        amsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        amsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        amsPanel.add(amsLabel);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(200, 200, 200));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(amsPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(20));

        panel.add(sectionHeader("Main"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(makeSidebarButton("Dashboard"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(makeSidebarButton("Employees"));
        panel.add(Box.createVerticalStrut(20));

        panel.add(sectionHeader("Attendance"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(makeSidebarButton("Attendance Logs"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(makeSidebarButton("Leaves"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(makeSidebarButton("Holidays"));
        panel.add(Box.createVerticalStrut(20));

        panel.add(sectionHeader("Finance"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(makeSidebarButton("Payroll"));

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JLabel sectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 12));
        label.setForeground(new Color(150, 150, 150));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        return label;
    }

    private JButton makeSidebarButton(String name) {
        JButton btn = StyledComponents.sidebarButton(name);
        btn.addActionListener(e -> showAdminSection(name));
        return btn;
    }

    private JPanel buildContentArea() {
        adminLayout = new CardLayout();
        adminCards  = new JPanel(adminLayout);

        subPanels.put("Dashboard",       new DashboardPanel(navManager, attendanceService, holidayService));
        subPanels.put("Employees",       new EmployeePanel(navManager, authService));
        subPanels.put("Attendance Logs", new AttendanceLogsPanel(navManager, attendanceService, authService));
        subPanels.put("Leaves",          new LeavesPanel(navManager, leaveService, authService));
        subPanels.put("Holidays",        new HolidaysPanel(navManager, holidayService));
        subPanels.put("Payroll",         new PayrollPanel(navManager, attendanceService, authService, leaveService, payrollService));

        subPanels.forEach((name, panel) -> adminCards.add(panel, name));
        return adminCards;
    }

    public void showAdminSection(String name) {
        adminLayout.show(adminCards, name);
        BasePanel panel = subPanels.get(name);
        if (panel != null) panel.onShow();
    }

    @Override
    public void onShow() {
        showAdminSection("Dashboard");
    }
}