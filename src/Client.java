import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        Socket client = new Socket(IP,PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream());
    }
}
