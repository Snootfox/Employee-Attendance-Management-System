package navigation;

import panels.BasePanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NavigationManager {
    private final CardLayout cardLayout;
    private final JPanel container;
    private final Map<String, JPanel> panelRegistry;
    private String currentPanel;

    public NavigationManager() {
        this.cardLayout    = new CardLayout();
        this.container     = new JPanel(cardLayout);
        this.panelRegistry = new HashMap<>();
    }

    public void register(String name, JPanel panel) {
        panelRegistry.put(name, panel);
        container.add(panel, name);
    }
    public void navigateTo(String name) {
        if (!panelRegistry.containsKey(name)) {
            throw new IllegalArgumentException("No panel registered under: " + name);
        }
        currentPanel = name;
        cardLayout.show(container, name);

        JPanel panel = panelRegistry.get(name);
        if (panel instanceof BasePanel) {
            ((BasePanel) panel).onShow();
        }
    }

    public JPanel getContainer() {
        return container;
    }

    public String getCurrentPanel() {
        return currentPanel;
    }
}