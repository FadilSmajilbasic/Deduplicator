
package deduplicatorGUI.layouts;

import java.awt.event.ActionEvent;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
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
    private static final int MARGIN = 20;

    /**
     * Creates new form PathJPanel
     */
    public PathJPanel() {
        initComponents();
    }

    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        pathTextField = new javax.swing.JTextField();
        typeComboBox = new javax.swing.JComboBox<>();
        pathJScrollPane = new javax.swing.JScrollPane();
        pathJList = new javax.swing.JList<>();
        typeJScrollPane = new javax.swing.JScrollPane();
        typeJList = new javax.swing.JList<>();
        insertButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        layout.rowHeights = new int[] { 0, 10, 0, 10, 0, 10, 0, 10, 0 };
        setLayout(layout);

        insertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertButtonActionPerformed(evt);
            }

        });

        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 350;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(pathTextField, gridBagConstraints);

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Scan", "Ignore" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        add(typeComboBox, gridBagConstraints);

        pathJScrollPane.setViewportView(pathJList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pathJScrollPane, gridBagConstraints);

        typeJScrollPane.setViewportView(typeJList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(typeJScrollPane, gridBagConstraints);

        deleteButton.setText("Delete");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(deleteButton, gridBagConstraints);

        insertButton.setText("Insert");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        add(insertButton, gridBagConstraints);

        jLabel1.setText("Path");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel1, gridBagConstraints);

        jLabel2.setText("Ignore");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel2, gridBagConstraints);

        this.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
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

            typeJList.setModel(new AbstractListModel<String>() {
                public int getSize() {
                    return array.length;
                }

                public String getElementAt(int i) {
                    return array[i].get("ignoreFile").toString() ;
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
    private javax.swing.JList<String> typeJList;
    private javax.swing.JScrollPane pathJScrollPane;
    private javax.swing.JScrollPane typeJScrollPane;
    private javax.swing.JComboBox<String> typeComboBox;

    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
}
