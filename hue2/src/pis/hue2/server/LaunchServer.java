package pis.hue2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * LaunchServer dient zum Starten des Servers
 * @author Thomas Fidorin, Fabian Woyda
 */

public class LaunchServer {

    /**
     * Für die gesendeten Files vom LaunchClient
     * @see pis.hue2.client.LaunchClient
     */


    /**
     * Main Klasse zum Starten des Servers
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8080);
        while (true) {
            Socket client = ss.accept();
            new Thread(new ClientHandler(client)).start();
        }
    }
}
