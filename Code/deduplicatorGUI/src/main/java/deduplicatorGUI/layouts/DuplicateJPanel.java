
package deduplicatorGUI.layouts;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Dimension;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
                duplicatesScrollPane = new JScrollPane();
                duplicatesJList = new JList<>();
                applyButton = new JButton();
                dateTextField = new JFormattedTextField(new SimpleDateFormat("dd.MM.yyyy"));
                timeTextField = new JFormattedTextField(new SimpleDateFormat("HH:mm"));
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
                applyButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                applyButtonActionPerformed(evt);
                        }
                });

                duplicatesJList.addListSelectionListener(this);
                duplicatesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

                duplicatesScrollPane.setViewportView(duplicatesJList);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.ipady = 150;
                gridBagConstraints.gridwidth = 5;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
                add(duplicatesScrollPane, gridBagConstraints);

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
                add(timeTextField, gridBagConstraints);

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
                        try {
                                JSONObject[] array = getArray((JSONArray) response);

                                List<JSONObject> list = new ArrayList<JSONObject>();
                                for (JSONObject jsonObject : array) {
                                        list.add(jsonObject);
                                }

                                duplicatesJList.setModel(new DefaultListModel<String>() {
                                        public int getSize() {
                                                return list.size();
                                        }

                                        public String getElementAt(int i) {
                                                return list.get(i).get("path").toString();
                                        }

                                        @Override
                                        public String remove(int index) {
                                                JSONObject obj = list.get(index);
                                                list.remove(obj);
                                                return obj.toString();
                                        }
                                });
                        } catch (ClassCastException e) {
                                System.out.println("Unable to cast: " + response.toString());
                        }

                }
                duplicatesScrollPane.revalidate();
                duplicatesScrollPane.repaint();

        }

        private void applyButtonActionPerformed(ActionEvent evt) {
                if (dateTextField.isValid()) {
                        if (timeTextField.isValid()) {
                                actions.values().forEach(action -> {
                                        System.out.println("actions: " + action);
                                        
                                });
                                getClient().put("action", actions);
                                
                        } else {
                                JOptionPane.showMessageDialog(this, "Time format invalid: please use HH:mm",
                                                "Time invalid", JOptionPane.ERROR_MESSAGE);
                        }
                } else {
                        JOptionPane.showMessageDialog(this, "Date format invalid: please use dd.MM.yyyy",
                                        "Date invalid", JOptionPane.ERROR_MESSAGE);
                }

        }

        @Override
        public void tabSelected() {
                updateScansCheckBox();
        }

        private void updateScansCheckBox() {
                Object response = getClient().get("report/all");

                if (response != null) {
                        JSONObject[] array = getArray((JSONArray) response);
                        reportsComboBox.setModel(new DefaultComboBoxModel<String>() {
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
                if (!arg0.getValueIsAdjusting()) {
                        int selectedIndex = arg0.getFirstIndex();
                        if (selectedIndex != -1) {
                                boolean deleteFromList = true;
                                JPanel panel = new JPanel();
                                panel.setPreferredSize(new Dimension(100, 100));
                                panel.add(new JLabel("What action would you like to apply"));
                                int result = JOptionPane.showOptionDialog(null, panel, "Action",
                                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                                                popupOptions, null);

                                switch (result) {
                                case 0:
                                        actions.add(ActionType.DELETE, duplicatesJList.getSelectedValue());
                                        break;
                                case 1:

                                        panel = new JPanel();
                                        panel.add(new JLabel("Insert new path:"));
                                        panel.setSize(new Dimension(500, 120));
                                        JTextField textField = new JTextField(50);
                                        textField.setText(duplicatesJList.getSelectedValue());
                                        panel.add(textField);

                                        boolean valid = false;
                                        do {

                                                int nestedResult = JOptionPane.showOptionDialog(null, panel, "Move",
                                                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                                null, null, null);

                                                if (nestedResult == 0) {

                                                        LinkedMultiValueMap<String, Object> validationData = new LinkedMultiValueMap<String, Object>();

                                                        validationData.add("path", textField.getText());

                                                        ResponseEntity<String> response = (ResponseEntity<String>) getClient()
                                                                        .post("/action/path", validationData);

                                                        if (response != null) {
                                                                if (response.getStatusCode() == HttpStatus.OK) {

                                                                        if (response.getBody().toString()
                                                                                        .equals("true")) {
                                                                                valid = true;
                                                                        }
                                                                }
                                                        }

                                                        actions.add(ActionType.MOVE, duplicatesJList.getSelectedValue()
                                                                        + PATH_SEPARATOR + textField.getText());

                                                } else {
                                                        valid = true;
                                                        deleteFromList = false;
                                                }
                                        } while (!valid);

                                        Map<String, String> values = new HashMap<String, String>();
                                        values.put("old", duplicatesJList.getSelectedValue());
                                        values.put("new", textField.getText());

                                        actions.add(ActionType.MOVE, JSONObject.toJSONString(values));
                                        break;
                                case 2:
                                        actions.add(ActionType.IGNORE, duplicatesJList.getSelectedValue());
                                        break;
                                default:
                                        deleteFromList = false;
                                        break;
                                }
                                if (deleteFromList) {
                                        DefaultListModel<String> model = (DefaultListModel<String>) duplicatesJList
                                                        .getModel();

                                        System.out.println("selected " + selectedIndex);

                                        model.remove(selectedIndex);

                                }
                        }

                }

                duplicatesJList.revalidate();
                duplicatesJList.repaint();

                duplicatesScrollPane.revalidate();
                duplicatesScrollPane.repaint();

        }

        private MultiValueMap<String, Object> actions = new LinkedMultiValueMap<String, Object>();
        private Object[] popupOptions = { "Delete", "Move", "Ignore" };
        private JButton applyButton;
        private JLabel applyDateLabel;
        private JFormattedTextField dateTextField;
        private JButton infoButton;
        private JLabel applyTimeLabel;
        private JScrollPane duplicatesScrollPane;
        private JList<String> duplicatesJList;
        private JComboBox<String> duplicatesComboBox;
        private JComboBox<String> reportsComboBox;
        private JFormattedTextField timeTextField;
        private DuplicatesComboBoxModel model;
        private final static String PATH_SEPARATOR = "&#47;";

}
