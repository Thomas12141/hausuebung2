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

                        //schickt den Befehl "ACK" an den Server
                        out.println(Instruction.ACK);

                        //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
                        out.flush();
                        counter++;
                    } else {

                        //schickt den Befehl "DND" an den Server
                        out.println(Instruction.DND);

                        //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
                        out.flush();
                    }
                }

                // vergleicht Nachricht vom Server mit temp
                if (Instruction.PUT.toString().equals(temp)) {

                    //schickt den Befehl "ACK" an den Server
                    out.println(Instruction.ACK);

                    //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
                    out.flush();

                    // Neuer Thread um Dateien hochzuladen wird gestartet

                        // DataInputStream wird auf null gesetzt (zum Datenlesen)
                        DataInputStream dataInputStream = null;

                        try {

                            // dataInputStream wird gefuellt
                            dataInputStream = new DataInputStream(client.getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Laenge des Dateinamens
                        int fileNameLength = 0;
                        try {
                            // liest die naechsten 4 Bytes aus dem InputStream und uebergibt sie der int Variablen
                            fileNameLength = dataInputStream.readInt();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byte[] fileNameBytes= null;
                        byte[] fileContentBytes = null;
                        if(fileNameLength>0){
                            fileNameBytes = new byte[fileNameLength];
                            try {

                                // liest bytes equal zur laenge von fileNamesBytes
                                dataInputStream.readFully(fileNameBytes,0,fileNameBytes.length);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            int fileContentLength = 0;
                            try {
                                // liest die naechsten 4 Bytes aus dem InputStream und uebergibt sie der int Variablen
                                fileContentLength = dataInputStream.readInt();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(fileContentLength>0){
                                fileContentBytes = new byte[fileContentLength];
                                try {

                                    // liest bytes equal zur laenge von fileContentBytes
                                    dataInputStream.readFully(fileContentBytes,0,fileContentBytes.length);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if(fileContentBytes!=null&&fileNameBytes!=null){

                            MyFile myFile = new MyFile(new String(fileNameBytes), fileContentBytes);
                            MyFile.myFiles.add(myFile);
                        }
                        // Thread wird gestartet
                }

                // vergleicht Nachricht vom Server mit temp
                if(Instruction.DSC.toString().equals(temp))
                {
                    //schickt den Befehl "DSC" an den Server
                    out.println(Instruction.DSC);

                    //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
                    out.flush();

                    // Wenn ein Client disconnected geht der counter ein runter, damit der naechste Client sich
                    // mit dem Server verbinden kann
                    counter--;
                }

                // vergleicht Nachricht vom Server mit temp
                if (Instruction.LST.toString().equals(temp)){

                    //schickt den Befehl "ACK" an den Server
                    out.println(Instruction.ACK);

                    //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
                    out.flush();
                    temp = in.readLine();
                    if (Instruction.ACK.toString().equals(temp)){

                        //schickt den Befehl "DAT" an den Server
                        out.println(Instruction.DAT);

                        //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
                        out.flush();
                        DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                        dataOutputStream.writeInt(MyFile.myFiles.size());
                        for (MyFile myFile: MyFile.myFiles) {
                            out.println(myFile.getId()+"   "+myFile.getName());

                            //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
                            out.flush();
                        }
                    }
                }
                if(Instruction.DEL.toString().equals(temp)){

                    int fileToDelete = Integer.parseInt(in.readLine());
                    if(MyFile.Delete(fileToDelete)){

                        //schickt den Befehl "ACK" an den Server
                        out.println(Instruction.ACK);

                        //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
                        out.flush();
                    }
                    else {

                        //schickt den Befehl "DND" an den Server
                        out.println(Instruction.DND);

                        //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
                        out.flush();
                    }
                }
                if(Instruction.GET.toString().equals(temp)){
                    int fileToDownload = Integer.parseInt(in.readLine());
                    if(fileToDownload-1<MyFile.myFiles.size()){

                        //schickt den Befehl "ACK" an den Server
                        out.println(Instruction.ACK);

                        //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
                        out.flush();
                        temp = in.readLine();
                        if (Instruction.ACK.toString().equals(temp))
                        {
                            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                            String name = MyFile.myFiles.get(fileToDownload-1).getName();
                            byte[] nameBytes = name.getBytes();
                            dataOutputStream.writeInt(nameBytes.length);
                            dataOutputStream.write(nameBytes);
                            dataOutputStream.writeInt(MyFile.myFiles.get(fileToDownload-1).getData().length);
                            dataOutputStream.write(MyFile.myFiles.get(fileToDownload-1).getData());
                        }
                    }else {
                        //schickt den Befehl "DND" an den Server
                        out.println(Instruction.DND);

                        //leert den Ausgabestream und erzwingt das Ausschreiben aller Ausgabytes
                        out.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
