import java.io.*;
import java.net.Socket;
public class ClientHandler implements Runnable{
    private Socket client;

    public ClientHandler(Socket client){
        this.client = client;
    }
    @Override
    public void run() {

    }
}
