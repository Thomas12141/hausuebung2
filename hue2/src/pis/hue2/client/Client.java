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
        out.println(Instruction.CON);

        //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
        out.flush();

        // um die Nachricht des Servers zu speichern. Wird benoetigt, um zu ueberpruefen welche Antwort vom Server kommt,
        //z. B. ACK oder DND
        String temp = in.readLine();
        dataOutputStream = new DataOutputStream(client.getOutputStream());
        dataInputStream = new DataInputStream(client.getInputStream());
        if(Instruction.ACK.toString().equals(temp)){
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
        out.println(Instruction.DSC);
        client.close();
        return true;
    }


    /**
     *
     * @param fileToDelete
     * @return boolean Wert ob Verbindung hergestellt werden darf
     * @throws IOException
     */
    public boolean Delete(String fileToDelete) throws IOException {
        //schickt den Befehl "DEL" an den Server
        out.println(Instruction.DEL);

        //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
        out.flush();

        //Welche Datei soll geloescht werden
        out.println(fileToDelete);

        //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
        out.flush();

        // um die Nachricht des Servers zu speichern
        String temp = in.readLine();
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
        out.println(Instruction.PUT);

        // leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
        out.flush();

        // um die Nachricht des Servers zu speichern
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
            dataOutputStream.writeInt(fileNameBytes.length);
            dataOutputStream.write(fileNameBytes);
            dataOutputStream.writeInt(fileContentBytes.length);
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
        out.println(Instruction.LST);

        // leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
        out.flush();

        // um die Nachricht des Servers zu speichern
        String temp = in.readLine();

        if (Instruction.ACK.toString().equals(temp)){
            // schickt den Befehl "ACK" an den Server
            out.println(Instruction.ACK);

            // leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
            out.flush();

            // um die Nachricht des Servers zu speichern
            temp = in.readLine();

            // prueft ob der Befehl mit DAT uebereinstimmt
            if (Instruction.DAT.toString().equals(temp)){

                // zum abspeichern der Bytes
                DataInputStream dataInputStream = new DataInputStream(client.getInputStream());

                // liest die naechsten 4 Bytes aus dem InputStream und uebergibt sie der int Variablen
                int count = dataInputStream.readInt();

                // gibt Liste aus
                for (int i = 0; i<count; i++){
                    lst.add(in.readLine());
                }

                // schickt den Befehl "ACK" an den Server
                out.println(Instruction.ACK);

                // leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
                out.flush();
            }
        }
        return lst;
    }

    /**
     * Die Funktionalität um Dateien vom Server zu downloaden
     * @param str
     * @return myFile Die erstellte Datei
     * @throws IOException
     */
    public MyFile Download(String str) throws IOException {
        MyFile myFile = null;

        // schickt den Befehl "LST" an den Server
        out.println(Instruction.GET);

        // leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
        out.flush();

        // Welche Datei soll gedownloaded werden
        out.println(str);

        // leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
        out.flush();

        // um die Nachricht des Servers zu speichern
        String temp = in.readLine();

        // prueft ob der Befehl mit ACK uebereinstimmt
        if(Instruction.ACK.toString().equals(temp))
        {
            // schickt den Befehl "LST" an den Server
            out.println(Instruction.ACK);

            // leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
            out.flush();

            // liest die naechsten 4 Bytes aus dem InputStream und uebergibt sie der int Variablen
            int nameLength = dataInputStream.readInt();
            String name = null;
            byte[] data;

            // Wird geprueft ob ein Name vorhanden ist
            if(nameLength>0){
                // Byte Array wird mit Laenge des Namens gefuellt
                byte[] nameBytes = new byte[nameLength];

                // liest bytes equal zur laenge von nameBytes
                dataInputStream.readFully(nameBytes,0,nameLength);

                // Byte Array wird zu String und in name gespeichert
                name = new String(nameBytes);

                // liest die naechsten 4 Bytes aus dem InputStream und uebergibt sie der int Variablen
                int dataLength = dataInputStream.readInt();

                if(dataLength>0){
                    data= new byte[dataLength];

                    // liest bytes equal zur laenge von data
                    dataInputStream.readFully(data,0,dataLength);

                    // Neue Datei wird erstellt
                    myFile = new MyFile(name, data);
                }
            }
            // schickt den Befehl "LST" an den Server
            out.println(Instruction.ACK);

            // leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabebytes
            out.flush();
        }
        return myFile;
    }
}
