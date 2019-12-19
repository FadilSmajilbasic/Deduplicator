
package deduplicatorGUI.layouts;

import java.awt.Dimension;
import javax.swing.*;
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
     * Creates new form MainJFrame
     */
    public MainJFrame() {
        initComponents();
    }

    private void initComponents() {

        menu = new JTabbedPane();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
        this.setSize(new Dimension(770, 560));

        setResizable(false);
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

    private JTabbedPane menu;

    /**
     * Il metodo userConnected viene chiamato quando l'utente inserisce delle credenziali giuste enlla schermata Login.
     * Questo metodo abilita la selezione e uso delle altre schermate e gli imposta anche il client da usare per la comunicazione.
     */
    @Override
    public void userConnected(Client client) {
        changeTabsAccessibility(menu, true);
        for (int i = 1; i < menu.getTabCount(); i++) {
            ((BaseJPanel) menu.getComponentAt(i)).setClient(client);
        }

    }

    @Override
    public void stateChanged(ChangeEvent e) {
       ( (BaseJPanel)menu.getSelectedComponent()).tabSelected();
    }
}
