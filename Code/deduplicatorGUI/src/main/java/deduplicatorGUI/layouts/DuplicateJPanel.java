
package deduplicatorGUI.layouts;

import java.awt.event.ActionEvent;
import java.util.Calendar;

import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Fadil Smajilbasic
 */
public class DuplicateJPanel extends BaseJPanel {

        /**
         * Generated serial id
         */
        private static final long serialVersionUID = -4929891125402326305L;

        /**
         * Creates new form DuplicateJPanel
         */
        public DuplicateJPanel() {
                initComponents();
        }

        private void initComponents() {
                java.awt.GridBagConstraints gridBagConstraints;

                reportsComboBox = new JComboBox<>();
                infoButton = new JButton();
                duplicatesComboBox = new JComboBox<String>();
                filesScannedJScrollPane = new JScrollPane();
                filesScannedJList = new JList<>();
                applyButton = new JButton();
                dateTextField = new JFormattedTextField();
                timeSpinner = new JSpinner();
                applyDateLabel = new JLabel();
                applyTimeLabel = new JLabel();
                fileScrollPane = new JScrollPane();

                reportsComboBox.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                reportsComboBoxActionPerformed(evt);
                        }
                });

                duplicatesComboBox.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                duplicatesComboBoxActionPerformed(evt);
                        }
                });

                java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
                layout.columnWidths = new int[] { 0, 31, 0, 31, 0 };
                layout.rowHeights = new int[] { 0, 14, 0, 14, 0, 14, 0 };
                setLayout(layout);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.ipadx = 60;
                add(reportsComboBox, gridBagConstraints);

                infoButton.setText("info");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 4;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.ipadx = 30;
                gridBagConstraints.ipady = 14;
                add(infoButton, gridBagConstraints);

                filesScannedJScrollPane.setViewportView(filesScannedJList);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 0;
                // gridBagConstraints.ipady = 150;
                gridBagConstraints.gridwidth = 5;
                // gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
                add(duplicatesComboBox, gridBagConstraints);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.ipady = 150;
                gridBagConstraints.gridwidth = 5;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
                add(new JPanel(), gridBagConstraints);

                applyButton.setText("Apply");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 4;
                gridBagConstraints.gridy = 6;
                gridBagConstraints.ipadx = 30;
                gridBagConstraints.ipady = 14;
                add(applyButton, gridBagConstraints);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 6;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                add(dateTextField, gridBagConstraints);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 6;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                add(timeSpinner, gridBagConstraints);

                applyDateLabel.setText("Apply date:");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 4;
                add(applyDateLabel, gridBagConstraints);

                applyTimeLabel.setText("Apply time:");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 4;
                add(applyTimeLabel, gridBagConstraints);
        }

        protected void reportsComboBoxActionPerformed(ActionEvent evt) {
                String selected = reportsComboBox.getSelectedItem().toString();
                updateDuplicatesComboBox(selected.split(":")[0]);
        }

        public void updateDuplicatesComboBox(String id) {
                Object response = getClient().get("report/duplicate/" + id);

                if (response != null) {
                        JSONObject[] array = getArray((JSONArray) response);
                        duplicatesComboBox.setModel(new DefaultComboBoxModel() {
                                public int getSize() {
                                        return array.length;
                                }
                                public String getElementAt(int i) {
                                        return "Count: " + array[i].get("count").toString();
                                }
                        });
                } else {
                        JOptionPane.showMessageDialog(this, "Unable to retireve duplicates", "Get error ",
                                        JOptionPane.INFORMATION_MESSAGE);
                }


                
        }

        protected void duplicatesComboBoxActionPerformed(ActionEvent evt) {
                //TODO get hash from duplciates combo box
                updateDuplicates("asdsd");
        }

        private void updateDuplicates(String hash) {

                Object response = getClient().get("report/duplicate/" + id);

        }

        private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {
                // TODO add your handling code here:
        }

        @Override
        public void tabSelected() {
                updateScansCheckBox();
        }

        private void updateScansCheckBox() {
                Object response = getClient().get("report/all");

                if (response != null) {
                        JSONObject[] array = getArray((JSONArray) response);
                        reportsComboBox.setModel(new DefaultComboBoxModel() {
                                public int getSize() {
                                        return array.length;
                                }

                                private Calendar cal = Calendar.getInstance();

                                public String getElementAt(int i) {
                                        cal.setTimeInMillis(Long.parseLong(array[i].get("start").toString()));
                                        return array[i].get("id") + ":" + cal.getTime().toString();
                                }
                        });
                } else {
                        JOptionPane.showMessageDialog(this, "Unable to get retrieve reports", "Get error ",
                                        JOptionPane.INFORMATION_MESSAGE);
                }

                reportsComboBox.revalidate();
                reportsComboBox.repaint();
        }

        private JButton applyButton;
        private JLabel applyDateLabel;
        private JFormattedTextField dateTextField;
        private JButton infoButton;
        private JLabel applyTimeLabel;
        private JScrollPane filesScannedJScrollPane;
        private JList<String> filesScannedJList;
        private JScrollPane fileScrollPane;
        private JComboBox<String> duplicatesComboBox;
        private JComboBox<String> reportsComboBox;
        private JSpinner timeSpinner;

}
