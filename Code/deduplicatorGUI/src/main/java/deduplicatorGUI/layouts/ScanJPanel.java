
package deduplicatorGUI.layouts;

import org.json.simple.parser.ParseException;

import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

                refreshButton = new JButton();
                startScanButton = new JButton();
                pauseScanButton = new JButton();
                stopScanButton = new JButton();
                scanInProgressLabel = new JLabel();
                objectsScannedLabel = new JLabel();
                scanStartedLabel = new JLabel();
                jScrollPane1 = new JScrollPane();
                filesScannedJScrollPane = new JScrollPane();
                detailsLabel = new JLabel();
                fileScannedList = new JList<String>();

                refreshButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                refreshButtonActionPerformed(evt);
                        }
                });
                startScanButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                startButtonActionPerformed(evt);
                        }
                });
                stopScanButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                stopButtonActionPerformed(evt);
                        }
                });
                refreshButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                refreshButtonActionPerformed(evt);
                        }
                });

                pauseScanButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                pauseButtonActionPerformed(evt);
                        }
                });

                refreshButton.setText("Refresh");
                startScanButton.setText("Start scan");
                pauseScanButton.setText("Pause scan");
                stopScanButton.setText("Stop scan");
                scanInProgressLabel.setText("Scan in progress:");
                objectsScannedLabel.setText("Files scanned:");
                scanStartedLabel.setText("Scan started:");

                jScrollPane1.setViewportView(filesScannedJScrollPane);

                detailsLabel.setText("Details:");

                GroupLayout layout = new GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
                                .createSequentialGroup().addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 376,
                                                                Short.MAX_VALUE)
                                                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(scanInProgressLabel)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addComponent(refreshButton))
                                                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(detailsLabel)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addComponent(stopScanButton))
                                                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(objectsScannedLabel)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addComponent(startScanButton))
                                                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(scanStartedLabel)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addComponent(pauseScanButton)))
                                .addContainerGap()));
                layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
                                .createSequentialGroup().addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(refreshButton).addComponent(scanInProgressLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(startScanButton).addComponent(objectsScannedLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(pauseScanButton).addComponent(scanStartedLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(stopScanButton).addComponent(detailsLabel))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        }

        private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {
                ResponseEntity<String> response = getClient().get("scan/status");
                if (response != null) {
                        try {
                                JSONObject respJson = ((JSONObject) parser.parse(response.getBody()));

                                System.out.println("respJson: " + respJson.toString());
                                String progress = respJson.get("status").toString();
                                String count = respJson.get("message").toString();
                                String startDate = respJson.get("timestamp").toString();

                                if (progress.equals(HttpStatus.OK.toString())) {
                                        scanInProgressLabel.setText("Scan in progress: yes");
                                        objectsScannedLabel.setText("Files scanned: " + count);
                                        scanStartedLabel.setText("Scan started: " + startDate);
                                } else {
                                        scanInProgressLabel.setText("Scan in progress: no");
                                        objectsScannedLabel.setText("Files scanned: 0");
                                        scanStartedLabel.setText("Scan started: ");
                                        JOptionPane.showMessageDialog(this, "Scan is not running", "Status Error",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                }
                        } catch (ParseException pe) {
                                JOptionPane.showMessageDialog(this, "Unable to get scan status: " + pe.getMessage(),
                                                "Status Error", JOptionPane.INFORMATION_MESSAGE);
                        }
                } else {
                        JOptionPane.showMessageDialog(this, "Unable to get scan status", "Status Error",
                                        JOptionPane.INFORMATION_MESSAGE);
                }

        }

        public void updateFilesScanned(int id) {
                ResponseEntity<String> response = getClient().get("report/" + id);

                if (response != null) {
                        try {
                                JSONObject[] array = getArray(
                                                (JSONArray) ((JSONObject) parser.parse(response.getBody()))
                                                                .get("file"));

                                fileScannedList.setModel(new DefaultComboBoxModel<>() {
                                        public int getSize() {
                                                return array.length;
                                        }

                                        public String getElementAt(int i) {
                                                return array[i].get("path").toString();
                                        }
                                });
                                filesScannedJScrollPane.update(getGraphics());
                        } catch (ParseException pe) {
                                JOptionPane.showMessageDialog(this, "Unable to get retrieve files: " + pe.getMessage(),
                                                "Get error ", JOptionPane.INFORMATION_MESSAGE);
                        }
                } else {
                        JOptionPane.showMessageDialog(this, "Unable to get retrieve files", "Get error ",
                                        JOptionPane.INFORMATION_MESSAGE);
                }
        }

        private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
                ResponseEntity<String> resp = getClient().post("scan/start", null);
                if (resp != null) {

                        try {
                                JSONObject report = (JSONObject) parser.parse(resp.getBody());
                                scanInProgressLabel.setText("Scan in progress: yes");
                                objectsScannedLabel.setText("Files scanned: " + 0);
                                scanStartedLabel.setText("Scan started: " + report.get("timestamp"));
                        } catch (ClassCastException | ParseException ex) {
                                JOptionPane.showMessageDialog(this, "Unable to start scan: " + ex.getMessage(),
                                                "Scan Error", JOptionPane.INFORMATION_MESSAGE);
                        }
                } else {
                        JOptionPane.showMessageDialog(this, "Unable to start scan", "Scan Error",
                                        JOptionPane.INFORMATION_MESSAGE);
                }

        }

        private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {
                ResponseEntity<String> resp = getClient().post("scan/stop", null);
                if (resp != null) {
                        refreshButtonActionPerformed(evt);
                } else {
                        JOptionPane.showMessageDialog(this, "Unable to stop scan", "Scan Error",
                                        JOptionPane.INFORMATION_MESSAGE);
                }
        }

        private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {
                getClient().post("scan/pause", null);
                refreshButtonActionPerformed(evt);
        }

        private JLabel detailsLabel;
        private JScrollPane jScrollPane1;
        private JLabel objectsScannedLabel;
        private JList<String> fileScannedList;
        private JScrollPane filesScannedJScrollPane;
        private JButton pauseScanButton;
        private JButton refreshButton;
        private JLabel scanInProgressLabel;
        private JLabel scanStartedLabel;
        private JButton stopScanButton;
        private JButton startScanButton;

}
