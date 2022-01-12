package pis.hue2.client;

import pis.hue2.server.MyFile;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Dient zum verarbeiten der in der GUI eingegebenen Befehle
 * Die Befehle werden durch einen Button dargestellt
 * @author Thomas Fidorin, Fabian Woyda
 * @see GUI
 */
public class Client {

    /**
     * Die aktuelle IP {@Value 127.0.0.1}.
     */
    private static final String IP = "127.0.0.1";

    /**
     * Der aktuelle Port {@Value 8080}.
     */
    private static final int PORT = 8080;

    // für eingehende Nachrichten/Befehle
    private static BufferedReader in;

    // für gesendete Nachrichten/Befehle
    private static PrintWriter out;

    //Der Client
    private static Socket client;
    private static DataOutputStream dataOutputStream;
    private static DataInputStream dataInputStream;

    /**
     * Baut eine Verbindung mit dem Server auf
     * Erlaubt der Server die Verbindung mit dem Client nicht, werden Client, Reader und Writer geschlossen.
     * @return Boolean Wert, ob die Verbindung Acknowledged oder Denied wurde
     * @throws IOException
     */
    public boolean Connect() throws IOException {

        // Der Socket bekommt die ID und den Port
        client = new Socket(IP, PORT);

        // Reader und Writer werden deklariert
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream());


        //schickt den Befehl "CON" an den Server
        System.out.println("Print: "+Instruction.CON);
        out.println(Instruction.CON);

        //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
        out.flush();

        // um die Nachricht des Servers zu speichern. Wird benoetigt, um zu ueberpruefen welche Antwort vom Server kommt,
        //z. B. ACK oder DND
        System.out.print("In: ACK oder DND, empfangen: ");
        String temp = in.readLine();
        System.out.println(temp);
        if(Instruction.ACK.toString().equals(temp)){
            dataOutputStream = new DataOutputStream(client.getOutputStream());
            dataInputStream = new DataInputStream(client.getInputStream());
            return true;
        }
        else {
            client.close();
            return false;
        }

    }

    /**
     * Trennt die Verbindung von Server und Client und schickt den Befehl "DSC" an den Server
     * @return Boolean Wert, damit Disconnect durchgefuehrt wird
     * @throws IOException
     */
    public boolean Disconnect() throws IOException{
        System.out.println("Print: "+ Instruction.DSC);
        out.println(Instruction.DSC);
        out.flush();
        System.out.println("In: "+Instruction.DSC);
        String temp= in.readLine();
        if(Instruction.DSC.toString().equals(temp))
        {
            client.close();
            return true;
        }
        return false;
    }


    public boolean Delete(String fileToDelete) throws IOException {
        System.out.println("Print: "+ Instruction.DEL);
        out.println(Instruction.DEL);
        out.flush();
        System.out.println("Print: die id der Datei, den der Server löschen soll. Id: "+fileToDelete);
        out.println(fileToDelete);
        out.flush();
        System.out.print("In: ACK oder DND, empfangen: ");
        String temp = in.readLine();
        System.out.println(temp);
        if(Instruction.ACK.toString().equals(temp))
        {
            return true;
        }
        return false;
    }


    /**
     * Laed eine ausgewaehlte Datei hoch
     * Erlaubt der Server die Verbindung mit dem Client nicht, werden Client, Reader und Writer geschlossen.
     * @param file
     * @return Boolean Wert, ob die Verbindung Acknowledged oder Denied wurde
     * @throws IOException
     */
    public boolean Upload(File file) throws IOException {
        // schickt den Befehl "PUT" an den Server
        System.out.println("Print: "+Instruction.PUT);
        out.println(Instruction.PUT);

        // leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
        out.flush();

        // um die Nachricht des Servers zu speichern
        System.out.println("In: "+Instruction.ACK);
        String temp = in.readLine();
        if(Instruction.ACK.toString().equals(temp)){
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            // speichert den Dateinamen
            String fileName = file.getName();

            // Byte Array für FileName damit dieser in Bytes umgewandelt wird
            byte[] fileNameBytes = fileName.getBytes();

            // Byte Array für Dateiinhalt damit dieser in Bytes umgewandelt wird
            byte[] fileContentBytes = new byte[(int) file.length()];
            fileInputStream.read(fileContentBytes);
            System.out.println("Print: The length if the name");
            dataOutputStream.writeInt(fileNameBytes.length);
            System.out.println("Print: The name");
            dataOutputStream.write(fileNameBytes);
            System.out.println("Print: The length if the data");
            dataOutputStream.writeInt(fileContentBytes.length);
            System.out.println("Print: The data");
            dataOutputStream.write(fileContentBytes);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Wenn man sich alle Dateien auf dem Server in einer Liste anzeigen lassen moechte
     * Erlaubt der Server die Verbindung mit dem Client nicht, werden Client, Reader und Writer geschlossen.
     * @return lst ArrayList vom Typ String
     * @throws IOException
     */
    public ArrayList<String> Lst() throws IOException{
        ArrayList<String> lst = new ArrayList<>();

        // schickt den Befehl "LST" an den Server
        System.out.println("Print: "+Instruction.LST);
        out.println(Instruction.LST);

        // leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
        out.flush();

        // um die Nachricht des Servers zu speichern
        System.out.println("In: "+Instruction.ACK);
        String temp = in.readLine();

        if (Instruction.ACK.toString().equals(temp)){
            // schickt den Befehl "ACK" an den Server
            System.out.println("Print: "+Instruction.ACK);
            out.println(Instruction.ACK);

            // leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
            out.flush();

            // um die Nachricht des Servers zu speichern
            System.out.println("In: "+Instruction.DAT);
            temp = in.readLine();

            // prueft ob der Befehl mit DAT uebereinstimmt
            if (Instruction.DAT.toString().equals(temp)){

                // zum abspeichern der Bytes
                DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
                System.out.println("In: The length of the list");
                int count = dataInputStream.readInt();


                for (int i = 0; i<count; i++){
                    System.out.println("In: The "+(i+1)+" element of the list");
                    lst.add(in.readLine());
                }
                System.out.println("Print: "+Instruction.ACK);
                out.println(Instruction.ACK);
                out.flush();
            }
        }
        return lst;
    }

    public MyFile Download(String str) throws IOException {
        MyFile myFile = null;
        System.out.println("Print: "+Instruction.GET);
        out.println(Instruction.GET);
        out.flush();
        System.out.println("Print: the id of the file");
        out.println(str);
        out.flush();
        System.out.println("In: "+Instruction.ACK);
        String temp = in.readLine();
        if(Instruction.ACK.toString().equals(temp))
        {
            System.out.println("Print: "+Instruction.ACK);
            out.println(Instruction.ACK);
            out.flush();
            System.out.println("In: the length of the name of the file");
            int nameLength = dataInputStream.readInt();
            String name = null;
            byte[] data;
            if(nameLength>0){
                byte[] nameBytes = new byte[nameLength];
                System.out.println("In: The name of the file");
                dataInputStream.readFully(nameBytes,0,nameLength);
                name = new String(nameBytes);
                System.out.println("In: the length of the content");
                int dataLength = dataInputStream.readInt();
                if(dataLength>0){
                    data= new byte[dataLength];
                    System.out.println("In: the content of the file");
                    dataInputStream.readFully(data,0,dataLength);
                    myFile = new MyFile(name, data);
                }
            }
            out.println(Instruction.ACK);
            out.flush();
        }
        return myFile;
    }
}
