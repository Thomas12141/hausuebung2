package pis.hue2.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {

    /*
     Die Variablen die man für einen Client Server Socket benötigt
     BufferedReader für die eingehenden Nachrichten/Befehle
     PrintWriter für die rausgehenden Nachrichten/Befehle
    */
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8080;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Socket client;

    /*
     Connect
     Boolean Methode um eine Verbindung mit dem Server aufzubauen.
     Der Socket bekommt die IP und den Port
     Reader und Writer werden deklariert
     flush() leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
     temp um die Nachricht des Server zu speichern. Wird benötigt, um zu überprüfen welche Antwort vom Server kommt,
     z. B. ACK oder DND
     Erlaubt der Server die Verbindung mit dem Client nicht, werden Client, Reader und Writer geschlossen.
    */
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

    /*
     Disconnect
     Trennt die Verbindung von Server und Client und schickt den Befehl "DSC" an der Server
    */
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

    /*
     Upload
     schickt den Befehl "PUT" an den Server und leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
     temp um die Nachricht des Server zu speichern.
     Zuerst wird geschaut, ob eine Verbindung zum Server besteht.
     Deklaration von DataOutputStream, um primitive Datentypen in den OutputStream zu schreiben
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
