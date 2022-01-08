package pis.hue2.client;

import javax.swing.*;

/**
 * LaunchClient dient zum Starten des Clients
 * @author Thomas Fidorin, Fabian Woyda
 */
public class LaunchClient {

    /**
     * Main Klasse zum Starten des Servers
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI g = new GUI("127.0.0.1",8080);
            }
        });
    }
}
