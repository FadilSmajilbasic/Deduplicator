
package deduplicatorGUI.layouts;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.util.LinkedMultiValueMap;

import deduplicatorGUI.ActionType;

/**
 * y
 * 
 * @author Fadil Smajilbasic
 */
public class DuplicateJPanel extends BaseJPanel implements ListSelectionListener {

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

                filesScannedJList.addListSelectionListener(this);

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
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.ipady = 150;
                gridBagConstraints.gridwidth = 5;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
                add(filesScannedJScrollPane, gridBagConstraints);

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
                        model = new DuplicatesComboBoxModel(array);
                        duplicatesComboBox.setModel(model);

                } else {
                        JOptionPane.showMessageDialog(this, "Unable to retireve duplicates", "Get error ",
                                        JOptionPane.INFORMATION_MESSAGE);
                }

        }

        protected void duplicatesComboBoxActionPerformed(ActionEvent evt) {

                updateDuplicates(model.getHash(duplicatesComboBox.getSelectedIndex()));
        }

        private void updateDuplicates(String hash) {
                String selected = reportsComboBox.getSelectedItem().toString();

                Object response = getClient().get("report/duplicate/" + selected.split(":")[0] + "/" + hash);
                
                if (response != null && response != "[]") {
                        JSONObject[] array = getArray((JSONArray) response);                
                        System.out.println("I'm here");
                        filesScannedJList.setModel(new AbstractListModel<String>() {
                                public int getSize() {
                                        return array.length;
                                }

                                public String getElementAt(int i) {
                                        return array[i].get("path").toString();
                                }
                        });

                }
                filesScannedJScrollPane.revalidate();
                filesScannedJScrollPane.repaint();

        }

        private void applyButtonActionPerformed(ActionEvent evt) {
                actions.values().forEach(action -> {
                        System.out.println("actions: " + action);
                });
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

        @Override
        public void valueChanged(ListSelectionEvent arg0) {
                JPanel panel = new JPanel();
                panel.add(new JLabel("What action would you like to apply"));
                int result = JOptionPane.showOptionDialog(null, panel, "Action", JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE, null, popupOptions, null);
                switch (result) {
                case 0:
                        actions.put(ActionType.DELETE, filesScannedJList.getSelectedValue());
                        filesScannedJList.remove(filesScannedJList.getSelectedIndex());
                        break;
                case 1:
                        panel = new JPanel();
                        panel.add(new JLabel("Insert new path:"));
                        JTextField textField = new JTextField(10);
                        panel.add(textField);
                        int nestedResult = JOptionPane.showOptionDialog(null, panel, "Move",
                                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                        actions.put(ActionType.DELETE, filesScannedJList.getSelectedValue());
                        if (nestedResult == 0) {
                                boolean valid = false;
                                do {
                                        LinkedMultiValueMap<String, Object> validationData = new LinkedMultiValueMap<String, Object>();

                                        validationData.add("path", textField.getText());

                                        valid = (getClient().post("/action/path", validationData).toString()
                                                        .equals("true"));

                                        filesScannedJList.remove(filesScannedJList.getSelectedIndex());
                                } while (!valid);

                                actions.put(ActionType.MOVE, filesScannedJList.getSelectedValue() + PATH_SEPARATOR
                                                + textField.getText());

                        }
                        break;
                case 2:
                        actions.put(ActionType.IGNORE, filesScannedJList.getSelectedValue());
                        filesScannedJList.remove(filesScannedJList.getSelectedIndex());
                        break;

                default:
                        break;
                }

                filesScannedJScrollPane.revalidate();
                filesScannedJScrollPane.repaint();
        }

        private Map<String, Object> actions = new HashMap<String, Object>();
        private Object[] popupOptions = { "Delete", "Move", "Ignore" };
        private JButton applyButton;
        private JLabel applyDateLabel;
        private JFormattedTextField dateTextField;
        private JButton infoButton;
        private JLabel applyTimeLabel;
        private JScrollPane filesScannedJScrollPane;
        private JList<String> filesScannedJList;
        private JComboBox<String> duplicatesComboBox;
        private JComboBox<String> reportsComboBox;
        private JSpinner timeSpinner;
        private DuplicatesComboBoxModel model;
        private final static String PATH_SEPARATOR = "&#47;";

}
