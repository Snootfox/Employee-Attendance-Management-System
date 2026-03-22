import file.CSVHandler;
import navigation.NavigationManager;
import panels.*;
import panels.admin.AdminPanel;
import service.*;

import javax.swing.*;
import java.awt.*;

public class App {
    public void start() {
        CSVHandler.initialize();

        AuthService       authService       = new AuthService();
        AttendanceService attendanceService = new AttendanceService();
        LeaveService      leaveService      = new LeaveService();
        HolidayService    holidayService    = new HolidayService();
        PayrollService    payrollService    = new PayrollService();

        JFrame frame = new JFrame("Attendance Management System");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        NavigationManager navManager = new NavigationManager();
        UserPanel userPanel = new UserPanel(navManager, attendanceService);

        navManager.register("MAIN",       new MainMenuPanel(navManager));
        navManager.register("ADMINLOGIN", new AdminLoginPanel(navManager, authService));
        navManager.register("USERLOGIN",  new UserLoginPanel(navManager, authService, userPanel));
        navManager.register("USER",       userPanel);
        navManager.register("ADMIN",      new AdminPanel(navManager, attendanceService,
                authService, leaveService,
                holidayService, payrollService));

        frame.add(navManager.getContainer(), BorderLayout.CENTER);
        navManager.navigateTo("MAIN");
        frame.setVisible(true);
    }
}