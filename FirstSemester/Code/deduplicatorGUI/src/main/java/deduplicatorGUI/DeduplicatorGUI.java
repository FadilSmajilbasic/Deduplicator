
package deduplicatorGUI;

import deduplicatorGUI.layouts.MainJFrame;

/**
 *
 * @author Fadil Smajilbasic
 */
public class DeduplicatorGUI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainJFrame().setVisible(true);
            }
        });
    }

}
