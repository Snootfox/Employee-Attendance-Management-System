package panels;
import navigation.NavigationManager;
import ui.StyledComponents;

import javax.swing.*;
import java.awt.*;

// MainMenuPanel shows the home screen with USER and ADMIN buttons.
public class MainMenuPanel extends BasePanel {
    public MainMenuPanel(NavigationManager navManager) {
        super(navManager);
        setLayout(new BorderLayout());
        add(buildTopPanel(),    BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 10, 20, 10));

        JLabel line1 = new JLabel("Attendance", SwingConstants.CENTER);
        line1.setForeground(StyledComponents.PRIMARY_COLOR);
        line1.setFont(new Font("Inter", Font.BOLD, 35));

        JLabel line2 = new JLabel("Management System", SwingConstants.CENTER);
        line2.setForeground(StyledComponents.PRIMARY_COLOR);
        line2.setFont(new Font("Inter", Font.BOLD, 35));

        panel.add(line1);
        panel.add(line2);
        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        JButton userButton  = buildIconButton("USER",  "/users.png");
        JButton adminButton = buildIconButton("ADMIN", "/user.png");

        userButton.addActionListener(e  -> navManager.navigateTo("USERLOGIN"));
        adminButton.addActionListener(e -> navManager.navigateTo("ADMINLOGIN"));

        panel.add(userButton);
        panel.add(adminButton);
        return panel;
    }

    private JButton buildIconButton(String label, String iconPath) {
        JButton button = new JButton(label);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaled = icon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {}

        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setFont(new Font("Inter", Font.BOLD, 20));
        button.setForeground(StyledComponents.PRIMARY_COLOR);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
