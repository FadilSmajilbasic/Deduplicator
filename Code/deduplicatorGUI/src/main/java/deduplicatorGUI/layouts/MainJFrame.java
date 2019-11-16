
package deduplicatorGUI.layouts;

/**
 *
 * @author duck
 */
public class MainJFrame extends javax.swing.JFrame {

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
        menu.add("Connection",connectionPanel);
        menu.add("Duplicates",duplicatePanel);
        menu.add("Scan",scanPanel);
        menu.add("Path",pathPanel);
        menu.add("Schedule",schedulePanel);



        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );




        pack();
    }

    private ConnectionJPanel connectionPanel;
    private DuplicateJPanel duplicatePanel;
    private ScanJPanel scanPanel;
    private PathJPanel pathPanel;
    private ScheduleJPanel schedulePanel;

    private javax.swing.JTabbedPane menu;
}
