package panels;
import navigation.NavigationManager;
import javax.swing.*;

    // BasePanel is an abstract class all panels extend.


public class BasePanel extends JPanel{
    protected final NavigationManager navManager;

    public BasePanel(NavigationManager navManager) {
        this.navManager = navManager;
    }


     //Called by NavigationManager each time this panel becomes visible.
     // Override to refresh data, reset forms, etc.
    public void onShow() {

    }
}
