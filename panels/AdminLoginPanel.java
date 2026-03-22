package panels;
import navigation.NavigationManager;
import service.AuthService;
import ui.StyledComponents;

import javax.swing.*;
import java.awt.*;

public class AdminLoginPanel extends BasePanel{
    private final AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public AdminLoginPanel(NavigationManager navManager, AuthService authService) {
        super(navManager);
        this.authService = authService;
        setBackground(StyledComponents.BG_COLOR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        buildUI();
    }

    private void buildUI() {
        usernameField = StyledComponents.styledTextField();
        passwordField = StyledComponents.styledPasswordField();

        JButton loginButton = StyledComponents.primaryButton("LOGIN");
        JButton backButton  = StyledComponents.mutedButton("BACK");

        loginButton.addActionListener(e -> handleLogin());
        backButton.addActionListener(e  -> resetAndGoBack());
        passwordField.addActionListener(e -> handleLogin());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(StyledComponents.BG_COLOR);
        buttonPanel.setMaximumSize(new Dimension(350, 50));
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        add(StyledComponents.titleLabel("Admin Login"));
        add(Box.createVerticalStrut(40));
        add(buildFieldPanel("Username:", usernameField));
        add(Box.createVerticalStrut(20));
        add(buildFieldPanel("Password:", passwordField));
        add(Box.createVerticalStrut(30));
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
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authService.validateAdmin(username, password)) {
            usernameField.setText("");
            passwordField.setText("");
            navManager.navigateTo("ADMIN");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void resetAndGoBack() {
        usernameField.setText("");
        passwordField.setText("");
        navManager.navigateTo("MAIN");
    }

    @Override
    public void onShow() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
