package deduplicatorGUI.layouts;

import javax.swing.JPanel;

import deduplicatorGUI.communication.Client;

/**
 * BaseJPanel
 */
public class BaseJPanel extends JPanel{

    private Client client;

    /**
     * @param client the client to set
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Method to be overridden
     */
    public void tabSelected(){

    }
    
}