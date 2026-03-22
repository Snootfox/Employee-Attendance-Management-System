import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        loadInterFont();
        SwingUtilities.invokeLater(() -> new App().start());
    }

    private static void loadInterFont() {
        try {
            java.awt.Font regular = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
                    Main.class.getResourceAsStream("/fonts/Inter_18pt-Regular.ttf"));
            java.awt.Font bold = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
                    Main.class.getResourceAsStream("/fonts/Inter_24pt-Bold.ttf"));

            java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(regular);
            ge.registerFont(bold);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
