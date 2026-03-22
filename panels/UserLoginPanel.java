package panels;

import navigation.NavigationManager;
import model.Employee;
import service.AuthService;
import ui.StyledComponents;

import javax.swing.*;
import java.awt.*;

// UserLoginPanel handles employee login via Employee ID.

public class UserLoginPanel extends BasePanel {
    private final AuthService authService;
    private final UserPanel userPanel;
    private JTextField idField;

    public UserLoginPanel(NavigationManager navManager, AuthService authService, UserPanel userPanel) {
        super(navManager);
        this.authService = authService;
        this.userPanel = userPanel;
        setBackground(StyledComponents.BG_COLOR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        buildUI();
    }

    private void buildUI() {
        idField = StyledComponents.styledTextField();
        idField.setMaximumSize(new Dimension(350, 35));
        idField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = StyledComponents.primaryButton("LOGIN");
        JButton backButton  = StyledComponents.mutedButton("BACK");

        loginButton.addActionListener(e -> handleLogin());
        backButton.addActionListener(e  -> resetAndGoBack());
        idField.addActionListener(e     -> handleLogin());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(StyledComponents.BG_COLOR);
        buttonPanel.setMaximumSize(new Dimension(350, 50));
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        add(StyledComponents.titleLabel("User Login"));
        add(Box.createVerticalStrut(40));
        add(buildFieldPanel("Employee ID:", idField));
        add(Box.createVerticalStrut(20));
        add(buttonPanel);
    }

    private JPanel buildFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(StyledComponents.BG_COLOR);
        panel.setMaximumSize(new Dimension(350, 70));
        panel.add(StyledComponents.fieldLabel(labelText), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void handleLogin() {
        String id = idField.getText().trim().toUpperCase();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Employee ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee employee = authService.findEmployee(id);
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Employee ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Pass the found employee into UserPanel before navigating
        userPanel.setEmployee(employee);

        idField.setText("");
        navManager.navigateTo("USER");
    }

    private void resetAndGoBack() {
        idField.setText("");
        navManager.navigateTo("MAIN");
    }

    @Override
    public void onShow() {
        idField.setText("");
    }
}