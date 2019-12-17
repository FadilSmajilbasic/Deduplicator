
package deduplicatorGUI.layouts;

import java.awt.event.ActionEvent;

import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
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

        pathTextField = new JTextField();
        typeComboBox = new JComboBox<>();
        pathJScrollPane = new JScrollPane();
        pathJList = new JList<>();
        typeJScrollPane = new JScrollPane();
        typeJList = new JList<>();
        insertButton = new JButton();
        deleteButton = new JButton();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();

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

        typeComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "Scan", "Ignore" }));
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
        typeJScrollPane.setFocusable(false);

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
                    "Do you really want to delete this path: " + pathJList.getSelectedValue().toString(), "Login error",
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

        if(response != null){
        System.out.println("response: " + response.getBody());
        JSONObject resp = new JSONObject();
        try {
            resp = (JSONObject) parser.parse(response.getBody());
        } catch (ParseException pe) {

        }

        if (resp.get("status") == null) {
            updatePathsList();
        } else {
            JOptionPane.showMessageDialog(this, "Unable to insert :" + pathTextField.getText(),
                    "Insert error: " + resp.get("message") != null ? resp.get("message").toString() : "No message",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        }else{
            JOptionPane.showMessageDialog(this, "Unable to insert :" + pathTextField.getText(),
                        "Insert error",
                        JOptionPane.INFORMATION_MESSAGE);
        }

        updatePathsList();

    }

    @Override
    public void tabSelected() {

        updatePathsList();

    }

    private void updatePathsList() {
        ResponseEntity<String> response = getClient().get("path/");

        if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                JSONObject[] array = getArray((JSONArray) parser.parse(response.getBody()));
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
                        return array[i].get("ignore").toString();
                    }
                });
            } catch (ParseException pe) {
                JOptionPane.showMessageDialog(this, "Unable to get retrieve paths: " + pe.getMessage(), "Get error ",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to get retrieve paths", "Get error ",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        pathJScrollPane.revalidate();
        pathJScrollPane.repaint();
    }

    private JTextField pathTextField;
    private JButton deleteButton;
    private JButton insertButton;
    private JList<String> pathJList;
    private JList<String> typeJList;
    private JScrollPane pathJScrollPane;
    private JScrollPane typeJScrollPane;
    private JComboBox<String> typeComboBox;

    private JLabel jLabel1;
    private JLabel jLabel2;
}
