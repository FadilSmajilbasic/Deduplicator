
package deduplicatorGUI.layouts;

import java.awt.event.ActionEvent;
import java.util.Calendar;

import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

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

                reportsComboBox = new javax.swing.JComboBox<>();
                infoButton = new javax.swing.JButton();
                jScrollPane1 = new javax.swing.JScrollPane();
                filesScannedList = new javax.swing.JList<>();
                applyDateLabel = new javax.swing.JLabel();
                dateTextField = new javax.swing.JFormattedTextField();
                timeSpiner = new javax.swing.JSpinner();
                jLabel1 = new javax.swing.JLabel();
                applyButton = new javax.swing.JButton();

                infoButton.setText("Info");

                filesScannedList.setModel(new AbstractListModel<String>() {
                        private static final long serialVersionUID = 1L;
                        String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

                        public int getSize() {
                                return strings.length;
                        }

                        public String getElementAt(int i) {
                                return strings[i];
                        }
                });
                jScrollPane1.setViewportView(filesScannedList);

                applyDateLabel.setText("Apply date:");

                dateTextField.setText("date");

                jLabel1.setText("Apply time:");

                applyButton.setText("Apply");
                applyButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                applyButtonActionPerformed(evt);
                        }
                });

                reportsComboBox.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                reportsComboBoxActionPerformed(evt);
                        }
                });

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jScrollPane1)
                                                .addGroup(layout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(layout.createSequentialGroup().addComponent(
                                                                                reportsComboBox,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                200,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(18, 18, 18)
                                                                                .addComponent(infoButton))
                                                                .addGroup(layout.createSequentialGroup().addGroup(layout
                                                                                .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                false)
                                                                                .addComponent(applyDateLabel)
                                                                                .addComponent(dateTextField,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                80, Short.MAX_VALUE))
                                                                                .addPreferredGap(
                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addGroup(layout.createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                                .addComponent(jLabel1)
                                                                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                                .addComponent(timeSpiner,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                66,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                .addPreferredGap(
                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                                167,
                                                                                                                                Short.MAX_VALUE)
                                                                                                                .addComponent(applyButton))))))
                                                .addContainerGap()));
                layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(reportsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(infoButton)).addGap(18, 18, 18)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(layout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(layout.createSequentialGroup()
                                                                                .addPreferredGap(
                                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addGroup(layout.createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                .addComponent(applyDateLabel)
                                                                                                .addComponent(jLabel1))
                                                                                .addPreferredGap(
                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                .addGroup(layout.createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                .addComponent(dateTextField,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addComponent(timeSpiner,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                layout.createSequentialGroup()
                                                                                                .addPreferredGap(
                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE)
                                                                                                .addComponent(applyButton)
                                                                                                .addContainerGap()))));
        }

        protected void reportsComboBoxActionPerformed(ActionEvent evt) {
                String selected = reportsComboBox.getSelectedItem().toString();

                updateFilesScannedList(selected.split(":")[0]);
        }

        public void updateFilesScannedList(String id) {
                Object response = getClient().get("report/" + id);

                if (response != null) {
                        JSONObject[] array = getArray((JSONArray)((JSONObject) response).get("file"));

                        filesScannedList.setModel(new DefaultComboBoxModel() {
                                public int getSize() {
                                        return array.length;
                                }

                                public String getElementAt(int i) {
                                        return array[i].get("path").toString();       
                                }
                        });
                } else {
                        JOptionPane.showMessageDialog(this, "Unable to get retrieve files", "Get error ",
                                        JOptionPane.INFORMATION_MESSAGE);
                }

        }

        private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {
                // TODO add your handling code here:
        }

        @Override
        public void tabSelected() {
                updateScansCheckBox();
        }

        private void updateScansCheckBox() {
                Object response = getClient().get("report/");

                if (response != null) {
                        JSONObject[] array = getArray((JSONArray) response);
                        reportsComboBox.setModel(new DefaultComboBoxModel() {
                                public int getSize() {
                                        return array.length;
                                }

                                private Calendar cal = Calendar.getInstance();

                                public String getElementAt(int i) {
                                        cal.setTimeInMillis(Long.parseLong(array[i].get("start").toString()));
                                        return array[i].get("id").toString() + ": " + cal.getTime().toString();
                                }
                        });
                } else {
                        JOptionPane.showMessageDialog(this, "Unable to get retrieve reports", "Get error ",
                                        JOptionPane.INFORMATION_MESSAGE);
                }

                reportsComboBox.revalidate();
                reportsComboBox.repaint();
        }

        private javax.swing.JButton applyButton;
        private javax.swing.JLabel applyDateLabel;
        private javax.swing.JFormattedTextField dateTextField;
        private javax.swing.JButton infoButton;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JList<String> filesScannedList;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JComboBox<String> reportsComboBox;
        private javax.swing.JSpinner timeSpiner;

}
