
package deduplicatorGUI.layouts;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import deduplicatorGUI.communication.Client;
import deduplicatorGUI.listeners.UserConnectedListener;

/**
 *
 * @author Fadil Smajilbasic
 */
public class MainJFrame extends JFrame implements UserConnectedListener, ChangeListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates new form MainJFrame
     */
    public MainJFrame() {
        initComponents();
    }

    private void initComponents() {

        menu = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        connectionPanel = new ConnectionJPanel();
        duplicatePanel = new DuplicateJPanel();
        scanPanel = new ScanJPanel();
        pathPanel = new PathJPanel();
        schedulePanel = new ScheduleJPanel();

        connectionPanel.setListener(this);

        menu.add("Connection", connectionPanel);
        menu.add("Duplicates", duplicatePanel);
        menu.add("Scan", scanPanel);
        menu.add("Path", pathPanel);
        menu.add("Schedule", schedulePanel);

        menu.addChangeListener(this);

        changeTabsAccessibility(menu, false);

        this.add(menu);        

        pack();
        this.setSize(500, 500);
    }

    private void changeTabsAccessibility(JTabbedPane menu, boolean state) {
        for (int i = 1; i < menu.getTabCount(); i++) {
            menu.setEnabledAt(i, state);
        }
    }

    private ConnectionJPanel connectionPanel;
    private DuplicateJPanel duplicatePanel;
    private ScanJPanel scanPanel;
    private PathJPanel pathPanel;
    private ScheduleJPanel schedulePanel;

    private javax.swing.JTabbedPane menu;
    private Client client;

    @Override
    public void userConnected(Client client) {
        changeTabsAccessibility(menu, true);
        this.client = client;

        for (int i = 1; i < menu.getTabCount(); i++) {
            ((BaseJPanel) menu.getComponentAt(i)).setClient(client);
        }

    }

    @Override
    public void stateChanged(ChangeEvent e) {
       ( (BaseJPanel)menu.getSelectedComponent()).tabSelected();
    }
}
