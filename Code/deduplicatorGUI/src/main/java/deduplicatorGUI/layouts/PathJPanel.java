
package deduplicatorGUI.layouts;

import java.awt.event.ActionEvent;

import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Fadil Smajilbasic
 */
public class PathJPanel extends BaseJPanel {

    private static final long serialVersionUID = 2506225118625343558L;

    /**
     * Creates new form PathJPanel
     */
    public PathJPanel() {
        initComponents();
    }

    private void initComponents() {

        pathTextField = new javax.swing.JTextField();
        typeComboBox = new javax.swing.JComboBox<>();
        insertButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        pathJScrollPane = new javax.swing.JScrollPane();
        pathJList = new javax.swing.JList<>();

        pathTextField.setText("path");

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Scan", "Ignore" }));

        insertButton.setText("Insert");

        insertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertButtonActionPerformed(evt);
            }

        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        pathJScrollPane.setViewportView(pathJList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(pathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 163,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18).addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(pathJScrollPane))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup().addGap(18, 18, 18).addComponent(insertButton).addGap(0,
                                0, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                layout.createSequentialGroup().addGap(39, 39, 39).addComponent(deleteButton)))
                .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(pathTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(insertButton))
                        .addGap(18, 18, 18)
                        .addComponent(pathJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 188,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteButton).addContainerGap()));

    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (pathJList.getSelectedValue() != null) {
            int n = JOptionPane.showConfirmDialog(this,
                    "Do you really want to delete this path:" + pathJList.getSelectedValue().toString(), "Login error",
                    JOptionPane.OK_CANCEL_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                ResponseEntity<String> response = getClient().delete("path", pathJList.getSelectedValue().toString());

                if (response.getStatusCode() == HttpStatus.OK) {
                    updatePathsList();

                } else {
                    JOptionPane.showMessageDialog(this, "Unable to delete :" + pathJList.getSelectedValue().toString(),
                            "Delete error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    private void insertButtonActionPerformed(ActionEvent evt) {

        ResponseEntity<String> response = getClient().insertPath(pathTextField.getText(),
                !typeComboBox.getSelectedItem().toString().equals("Scan"));

        if (response.getStatusCode() == HttpStatus.OK) {
            updatePathsList();

        } else {
            JOptionPane.showMessageDialog(this, "Unable to insert :" + pathTextField.getText(),
                    "Insert error: " + response.getBody(), JOptionPane.INFORMATION_MESSAGE);
        }
        updatePathsList();
    }

    @Override
    public void tabSelected() {

        updatePathsList();

    }

    private void updatePathsList() {
        Object response = getClient().get("path/");

        if (response != null) {
            JSONObject[] array = getArray((JSONArray) response);
            pathJList.setModel(new AbstractListModel<String>() {
                public int getSize() {
                    return array.length;
                }

                public String getElementAt(int i) {
                    return array[i].get("path").toString();
                }
            });

        } else {
            JOptionPane.showMessageDialog(this, "Unable to get retrieve paths", "Get error ",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        pathJScrollPane.revalidate();
        pathJScrollPane.repaint();
    }

    private javax.swing.JTextField pathTextField;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton insertButton;
    private javax.swing.JList<String> pathJList;
    private javax.swing.JScrollPane pathJScrollPane;
    private javax.swing.JComboBox<String> typeComboBox;

}
