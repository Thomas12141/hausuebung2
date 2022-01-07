package pis.hue2.server;

import java.io.*;
import java.net.Socket;
public class ClientHandler implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private static int counter = 0;
    public ClientHandler(Socket client) throws IOException {
        this.client = client;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream());
    }
    @Override
    public void run() {
        try {
            String temp = in.readLine();
            if(Instruction.ifCON(temp)) {
                if (counter < 3) {
                    out.println(Instruction.ACK);
                } else {
                    out.println(Instruction.DND);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
