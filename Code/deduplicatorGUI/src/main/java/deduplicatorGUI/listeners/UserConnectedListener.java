package deduplicatorGUI.listeners;

import deduplicatorGUI.communication.Client;

/**
 * È il listener che viene usato dal
 * {@link deduplicatorGUI.layouts.ConnectionJPanel} per segnalare alla classe
 * {@link deduplicatorGUI.layouts.MainJFrame} che un utente ha inserito delle
 * credeziali valide.
 */
public interface UserConnectedListener {

    public void userConnected(Client client);
}