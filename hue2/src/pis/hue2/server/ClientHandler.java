package pis.hue2.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;

    // Um Clients zu zaehlen
    private static int counter = 0;

    /**
     * Konstruktor der Klasse ClientHandler
     * @param client
     * @throws IOException
     */
    public ClientHandler(Socket client) throws IOException {
        this.client = client;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream());
    }

    /**
     * Verantwortlich dafür was passiert wenn die Buttons geklickt werden
     */
    @Override
    public void run() {
        try {
            while (true) {

                // um die Nachricht des Servers zu speichern. Wird benoetigt, um zu ueberpruefen welche Antwort vom Server kommt
                String temp = in.readLine();

                // vergleicht Nachricht vom Server mit temp
                if (Instruction.CON.toString().equals(temp)) {

                    // Es sind nur 3 Clients gleichzeitig erlaubt, wenn schon 3 Clients mit dem Server verbunden sind,
                    // bekommt der 4te ein Denied zurueck vom Server
                    if (counter < 3) {
                        out.println(Instruction.ACK);
                        out.flush();
                        counter++;
                    } else {
                        out.println(Instruction.DND);
                        out.flush();
                    }
                }

                // vergleicht Nachricht vom Server mit temp
                if (Instruction.PUT.toString().equals(temp)) {
                    out.println(Instruction.ACK);
                    out.flush();

                    // Neuer Thread um Dateien hochzuladen wird gestartet
                    new Thread(() -> {

                        // DataInputStream wird zuruekgesetzt
                        DataInputStream dataInputStream = null;
                        try {
                            dataInputStream = new DataInputStream(client.getInputStream());
                        } catch (IOException e) {
                            out.println(Instruction.DND);
                            out.flush();
                        }

                        // Laenge des Dateinamens
                        int fileNameLength = 0;
                        try {
                            //bekommt die Laenge der von DataInputStream gelesen wurde
                            fileNameLength = dataInputStream.readInt();
                        } catch (IOException e) {
                            out.println(Instruction.DND);
                            out.flush();
                        }

                        // Laenge des Dateiinhalts
                        int fileContentLength = 0;
                        try {
                            // bekommt die Laenge der von DataInputStream gelesen wurde
                            fileContentLength = dataInputStream.readInt();
                        } catch (IOException e) {
                            out.println(Instruction.DND);
                            out.flush();
                        }

                        // schaut ob Inhalt und Name der Datei > 0 sind
                        if (fileContentLength>0&&fileNameLength>0) {

                            // Byte Array in groeße von Dateiname
                            byte[] fileNameBytes = new byte[fileNameLength];
                            try {
                                // Liest von fileNameBytes startet bei 0 und liest so viele Bytes wie fileNameBytes lang ist
                                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                            } catch (IOException e) {
                                out.println(Instruction.DND);
                                out.flush();
                            }

                            // String in groeße von fileNameBytes
                            String fileName = new String(fileNameBytes);

                            // Byte Array in groeße von Dateiinhalt
                            byte[] fileContentBytes = new byte[fileContentLength];
                            try {
                                // Liest von fileContentBytes startet bei 0 und liest so viele Bytes wie fileContentBytes lang ist
                                dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
                            } catch (IOException e) {
                                out.println(Instruction.DND);
                                out.flush();
                            }
                            // Dateien werden in Form von einem String an den LaunchServer uebergeben
                            LaunchServer.myFiles.add(new MyFile(new String(fileNameBytes), fileContentBytes));
                            out.println(Instruction.ACK);
                            out.flush();
                        }
                        // Thread wird gestartet
                    }).start();
                }

                // vergleicht Nachricht vom Server mit temp
                if(Instruction.DSC.toString().equals(temp))
                {
                    out.println(Instruction.DSC);
                    out.flush();

                    // Wenn ein Client disconnected geht der counter ein runter, damit der naechste Client sich
                    // mit dem Server verbinden kann
                    counter--;
                }

                // vergleicht Nachricht vom Server mit temp
                if (Instruction.LST.toString().equals(temp)){
                    out.println(Instruction.ACK);
                    out.flush();
                    temp = in.readLine();
                    if (Instruction.ACK.toString().equals(temp)){

                        out.println(LaunchServer.myFiles.size());
                        out.flush();
                        for (MyFile myFile: LaunchServer.myFiles) {
                            out.println(myFile.getId()+"   "+myFile.getName());
                            out.flush();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
