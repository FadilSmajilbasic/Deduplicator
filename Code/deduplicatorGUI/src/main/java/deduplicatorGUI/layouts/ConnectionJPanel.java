package deduplicatorGUI.layouts;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import deduplicatorGUI.communication.Client;
import deduplicatorGUI.listeners.UserConnectedListener;

/**
 *
 * @author Fadil Smajilbasic
 */
public class ConnectionJPanel extends BaseJPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1720057564758232802L;

    /**
     * Creates new form ConnectionJPanel
     */
    public ConnectionJPanel() {
        initComponents();
    }

    private void initComponents() {

        usernameTextField = new javax.swing.JTextField();
        ipTextField1 = new javax.swing.JTextField();
        passwordTextField = new javax.swing.JTextField();
        connectButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();


        ipTextField1.setText("localhost:8080");
        usernameTextField.setText("admin");
        passwordTextField.setText("admin");

        connectButton.setText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("IP:");

        jLabel2.setText("Username:");

        jLabel3.setText("Password:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup().addContainerGap(321, Short.MAX_VALUE).addComponent(connectButton)
                                .addContainerGap())
                .addGroup(layout.createSequentialGroup().addGap(25, 25, 25).addGroup(layout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel3)
                        .addComponent(jLabel2).addComponent(jLabel1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(ipTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                                .addComponent(usernameTextField).addComponent(passwordTextField)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addContainerGap().addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipTextField1, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3).addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2).addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addComponent(connectButton).addContainerGap()));
    }

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String[] address = ipTextField1.getText().split(":");

        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        Client client = new Client(username, password);
        try {
            if (address.length == 2) {
                if (client.isAuthenticated(InetAddress.getByName(address[0]), Integer.parseInt(address[1]))) {
                    System.out.println("connected");
                    listener.userConnected(client);
                    JOptionPane.showMessageDialog(this, "User connected successfully ", "Login",
                            JOptionPane.INFORMATION_MESSAGE);

                } else {
                    JOptionPane.showMessageDialog(this, "User credentials invalid ", "Login error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this, "Ip invalid ", "Login error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (UnknownHostException | NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Ip invalid ", "Login error", JOptionPane.ERROR_MESSAGE);
        }


    }

    private javax.swing.JTextField ipTextField1;
    private javax.swing.JButton connectButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField passwordTextField;
    private javax.swing.JTextField usernameTextField;
    private UserConnectedListener listener;

    public void setListener(UserConnectedListener listener) {
        this.listener = listener;
    }
}
