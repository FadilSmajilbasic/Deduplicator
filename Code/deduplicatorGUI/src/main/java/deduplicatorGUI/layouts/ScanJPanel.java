
package deduplicatorGUI.layouts;

/**
 *
 * @author Fadil Smajilbasic
 */
public class ScanJPanel extends BaseJPanel {

    private static final long serialVersionUID = 6411962694666602476L;

    /**
     * Creates new form ScanJPanel
     */
    public ScanJPanel() {
        initComponents();
    }

    private void initComponents() {

        refreshButton = new javax.swing.JButton();
        stratScanButton = new javax.swing.JButton();
        pauseScanButton = new javax.swing.JButton();
        stopScanButton = new javax.swing.JButton();
        scanInProgressLabel = new javax.swing.JLabel();
        objectsScannedLabel = new javax.swing.JLabel();
        scanStratedLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextArea();
        detailsLabel = new javax.swing.JLabel();

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        stratScanButton.setText("Start scan");

        pauseScanButton.setText("Pause scan");

        stopScanButton.setText("Stop scan");

        scanInProgressLabel.setText("Scan in progress:");

        objectsScannedLabel.setText("Objects scanned:");

        scanStratedLabel.setText("Scan started:");

        outputArea.setColumns(20);
        outputArea.setRows(5);
        jScrollPane1.setViewportView(outputArea);

        detailsLabel.setText("Details:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                        layout.createSequentialGroup().addComponent(scanInProgressLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(refreshButton))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                        layout.createSequentialGroup().addComponent(detailsLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(stopScanButton))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                        layout.createSequentialGroup().addComponent(objectsScannedLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(stratScanButton))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                        layout.createSequentialGroup().addComponent(scanStratedLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(pauseScanButton)))
                        .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup().addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(refreshButton).addComponent(scanInProgressLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(stratScanButton).addComponent(objectsScannedLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(pauseScanButton).addComponent(scanStratedLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(stopScanButton).addComponent(detailsLabel))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private javax.swing.JLabel detailsLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel objectsScannedLabel;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JButton pauseScanButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JLabel scanInProgressLabel;
    private javax.swing.JLabel scanStratedLabel;
    private javax.swing.JButton stopScanButton;
    private javax.swing.JButton stratScanButton;

}