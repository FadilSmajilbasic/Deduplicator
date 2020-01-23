package deduplicatorGUI.layouts;

import javax.swing.*;

import org.springframework.web.client.RestClientException;

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

        usernameTextField = new JTextField();
        ipTextField1 = new JTextField();
        passwordTextField = new JPasswordField();
        connectButton = new JButton();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();


        ipTextField1.setText("localhost:8443");
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

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup().addContainerGap(321, Short.MAX_VALUE).addComponent(connectButton)
                                .addContainerGap())
                .addGroup(layout.createSequentialGroup().addGap(25, 25, 25).addGroup(layout
                        .createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jLabel3)
                        .addComponent(jLabel2).addComponent(jLabel1)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(ipTextField1, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                                .addComponent(usernameTextField).addComponent(passwordTextField)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addContainerGap().addComponent(jLabel1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipTextField1, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3).addComponent(jLabel2)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameTextField, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2).addComponent(jLabel3)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordTextField, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addComponent(connectButton).addContainerGap()));
    }

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String[] address = ipTextField1.getText().split(":");

        String username = usernameTextField.getText();
        String password = new String(passwordTextField.getPassword());
        System.out.println("pass: " + password);

        Client client = new Client(username, password);
        try {
            if (address.length == 2) {
                if (client.isAuthenticated(address[0], Integer.parseInt(address[1]))) {
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

        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Ip invalid ", "Login error", JOptionPane.ERROR_MESSAGE);
        }catch(RestClientException rce){
            JOptionPane.showMessageDialog(this, "Server not reachable", "Connection error", JOptionPane.ERROR_MESSAGE);
        }


    }

    private JTextField ipTextField1;
    private JButton connectButton;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JPasswordField passwordTextField;
    private JTextField usernameTextField;
    private UserConnectedListener listener;

    public void setListener(UserConnectedListener listener) {
        this.listener = listener;
    }
}