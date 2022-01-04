import java.io.*;
import java.net.Socket;
public class ClientHandler implements Runnable{
    private Socket client;
    //writer
    //reader
    public ClientHandler(Socket client){
        this.client = client;
        //writer
        //reader
    }
    @Override
    public void run() {

    }
}
