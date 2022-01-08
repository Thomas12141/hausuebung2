package pis.hue2.client;

import javax.swing.*;

public class LaunchClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI g = new GUI("127.0.0.1",8080);
            }
        });
    }
}
