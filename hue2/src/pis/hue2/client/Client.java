package pis.hue2.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
        out.println("CON");
        out.flush();
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

    }

    public boolean Disconnect() throws IOException{
        out.println("DSC");
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
    public boolean Upload(File file) throws IOException {
        out.println("PUT");
        out.flush();
        String temp = in.readLine();
        if(Instruction.ifACK(temp)){
            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
            String fileName = file.getName();
            byte[] fileNameBytes = fileName.getBytes();
            byte[] fileContentBytes = new byte[(int) file.length()];
            dataOutputStream.write(fileNameBytes.length);
            dataOutputStream.write(fileNameBytes);
            dataOutputStream.write(fileContentBytes.length);
            dataOutputStream.write(fileContentBytes);
            dataOutputStream.close();
            return true;
        }
        else {
            return false;
        }
    }
}
