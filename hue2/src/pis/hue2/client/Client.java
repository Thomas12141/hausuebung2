package pis.hue2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8080;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Socket client;

    public boolean Connect() throws IOException {
        client = new Socket(IP, PORT);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream());
        /*
        out.println(Instruction.CON);
        String temp = in.readLine();
        if(Instruction.ifACK(temp)){
            return true;
        }
        else {
            client.close();
            in.close();
            out.close();
            return false;
        }

         */
        return false;
    }

    public boolean Disconnect() throws IOException{
        out.println(Instruction.DSC);
        client.close();
        return true;
    }
/*
    public boolean Delete(){
        out.println(Instruction.DEL);
        String fileToDelete;
        out.println(fileToDelete);
        String temp = in.readLine();
        if(Instruction.ifDEL(temp))
        {
            
        }
    }

 */
}
