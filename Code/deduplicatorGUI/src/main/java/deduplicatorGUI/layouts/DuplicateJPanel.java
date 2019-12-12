
package deduplicatorGUI.layouts;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.text.MaskFormatter;

import java.awt.Dimension;
import java.awt.Desktop.Action;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
                dateTextField = new JFormattedTextField(getMaskFormatter("##.##.####"));
                timeTextField = new JFormattedTextField(getMaskFormatter("##:##"));
                applyDateLabel = new JLabel();
                applyTimeLabel = new JLabel();


                Calendar cal = Calendar.getInstance();
                dateTextField.setText(cal.get(Calendar.DAY_OF_MONTH) +"."+cal.get(Calendar.MONTH) +"." + cal.get(Calendar.YEAR) );
                timeTextField.setText(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));

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
                gridBagConstraints.gridwidth = 5;
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

        private MaskFormatter getMaskFormatter(String pattern) {
                MaskFormatter form = null;
                try {
                        form = new MaskFormatter(pattern);
                } catch (java.text.ParseException e) {
                        System.out.println("Mask formatter parse exception: " + e.getMessage());
                }
                return form;
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

                Calendar calDate = Calendar.getInstance();
                Calendar calTime = Calendar.getInstance();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat time = new SimpleDateFormat("HH:mm");

                try {
                        calDate.setTime(dateFormat.parse(dateTextField.getText()));
                        calTime.setTime(time.parse(timeTextField.getText()));

                        JPanel panel = new JPanel();
                        panel.setPreferredSize(new Dimension(200, 150));
                        Long schedulerId = createSchedudler(calDate.getTimeInMillis(),
                                        calTime.get(Calendar.HOUR_OF_DAY) * 60 + calTime.get(Calendar.MINUTE));

                        panel.add(new JLabel("Are you sure you want to execute the current changes?"));
                        if (schedulerId != null) {
                                for (Object object : actions.toArray()) {
                                        JSONObject obj = (JSONObject) object;

                                        MultiValueMap<String, Object> values = new LinkedMultiValueMap<String, Object>();
                                        obj.keySet().forEach(key -> {
                                                values.add(key.toString(), obj.get(key));
                                        });


                                        Calendar test = Calendar.getInstance();
                                        test.setTimeInMillis(calDate.getTimeInMillis());
                                        values.add("scheduler", schedulerId);
                                        getClient().put("action/", values);

                                        actions.remove(object);

                                }
                        } else {
                                JOptionPane.showMessageDialog(this, "Scheduler not created", "Scheduler not created",
                                                JOptionPane.ERROR_MESSAGE);
                        }

                } catch (java.text.ParseException pe) {
                        JOptionPane.showMessageDialog(this,
                                        "Time or date format invalid: please use HH:mm for time and dd.MM.yyyy for year",
                                        "Time or date invalid", JOptionPane.ERROR_MESSAGE);
                }

        }

        private Long createSchedudler(Long date, int hour) {
                try {
                        MultiValueMap<String, Object> values = new LinkedMultiValueMap<String, Object>();
                        values.add("dateStart", date);
                        values.add("repeated", "false");
                        values.add("monthly", "");
                        values.add("weekly", "");
                        values.add("hour", hour);
                        ResponseEntity<String> response = (ResponseEntity<String>) getClient().put("scheduler/",
                                        values);
                        JSONObject resp = (JSONObject) parser.parse(response.getBody());
                        System.out.println("resp: " + response.getBody());
                        return (Long) resp.get("schedulerId");
                } catch (NumberFormatException nfe) {
                        System.out.println("nfe" + nfe.getMessage());
                        return null;
                } catch (ParseException pe) {
                        System.out.println("parse" + pe.getMessage());
                        return null;
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
                                panel.add(new JLabel("Which action would you like to apply"));
                                int result = JOptionPane.showOptionDialog(null, panel, "Action",
                                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                                                popupOptions, null);
                                JSONObject values = new JSONObject();
                                switch (result) {
                                case 0:
                                        values.put("type", ActionType.DELETE);
                                        values.put("path", duplicatesJList.getSelectedValue());
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

                                                        JSONObject obj = null;
                                                        try {
                                                                obj = (JSONObject) parser.parse(response.getBody());
                                                        } catch (ParseException ex) {
                                                                System.out.println("Unable to parse response");
                                                        }
                                                        if (response != null) {
                                                                if (response.getStatusCode() == HttpStatus.OK) {

                                                                        if (obj.get("message").toString()
                                                                                        .equals("true")) {
                                                                                valid = true;
                                                                        }
                                                                }
                                                        }
                                                        JSONObject moveParams = new JSONObject();
                                                        moveParams.put("path", duplicatesJList.getSelectedValue());
                                                        moveParams.put("newPath", textField.getText());
                                                        values.put("type", ActionType.MOVE);
                                                        values.put("moveParams", moveParams);

                                                } else {
                                                        valid = true;
                                                        deleteFromList = false;
                                                }
                                        } while (!valid);

                                        break;
                                case 2:

                                        values.put("type", ActionType.IGNORE);
                                        values.put("path", duplicatesJList.getSelectedValue());
                                        break;
                                default:
                                        deleteFromList = false;
                                        break;
                                }
                                actions.add(values);
                                if (deleteFromList) {
                                        DefaultListModel<String> model = (DefaultListModel<String>) duplicatesJList
                                                        .getModel();
                                        model.remove(selectedIndex);

                                }
                        }

                }
                
                duplicatesScrollPane.repaint();
                duplicatesJList.clearSelection();

        }

        private JSONArray actions = new JSONArray();
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
        JSONParser parser = new JSONParser();

}
