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
                System.out.println("In: " + temp);
                // vergleicht Nachricht vom Server mit temp
                if (Instruction.CON.toString().equals(temp)) {

                    // Es sind nur 3 Clients gleichzeitig erlaubt, wenn schon 3 Clients mit dem Server verbunden sind,
                    // bekommt der 4te ein Denied zurueck vom Server
                    if (counter < 3) {
                        System.out.println("print: "+ Instruction.ACK);
                        out.println(Instruction.ACK);
                        out.flush();
                        counter++;
                    } else {
                        System.out.println("print: "+ Instruction.DND);
                        out.println(Instruction.DND);
                        out.flush();
                    }
                }

                // vergleicht Nachricht vom Server mit temp
                if (Instruction.PUT.toString().equals(temp)) {
                    System.out.println("print: "+ Instruction.ACK);
                    out.println(Instruction.ACK);
                    out.flush();

                    // Neuer Thread um Dateien hochzuladen wird gestartet

                        // DataInputStream wird zuruekgesetzt
                        DataInputStream dataInputStream = null;

                        try {
                            dataInputStream = new DataInputStream(client.getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Laenge des Dateinamens
                        int fileNameLength = 0;
                        try {
                            System.out.println("in: reads the length of the file name");
                            fileNameLength = dataInputStream.readInt();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byte[] fileNameBytes= null;
                        byte[] fileContentBytes = null;
                        if(fileNameLength>0){
                            fileNameBytes = new byte[fileNameLength];
                            try {
                                System.out.println("in: reads the content of the file name");
                                dataInputStream.readFully(fileNameBytes,0,fileNameBytes.length);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            int fileContentLength = 0;
                            try {
                                System.out.println("in: reads the length of the content of the file");
                                fileContentLength = dataInputStream.readInt();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(fileContentLength>0){
                                fileContentBytes = new byte[fileContentLength];
                                try {
                                    System.out.println("in: reads the content of the file");
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
                    System.out.println("print: "+ Instruction.DSC);
                    out.println(Instruction.DSC);
                    out.flush();

                    // Wenn ein Client disconnected geht der counter ein runter, damit der naechste Client sich
                    // mit dem Server verbinden kann

                    counter--;
                    client.close();
                    break;

                }

                // vergleicht Nachricht vom Server mit temp
                if (Instruction.LST.toString().equals(temp)){
                    System.out.println("print: "+ Instruction.ACK);
                    out.println(Instruction.ACK);
                    out.flush();
                    System.out.println("In: " + Instruction.ACK);
                    temp = in.readLine();
                    if (Instruction.ACK.toString().equals(temp)){
                        System.out.println("print: "+ Instruction.DAT);
                        out.println(Instruction.DAT);
                        out.flush();
                        System.out.println("print: The size of the list");
                        DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                        dataOutputStream.writeInt(MyFile.myFiles.size());
                        int i = 1;
                        for (Object myFile: MyFile.myFiles) {
                            System.out.println("print:the " + i+" file");
                            i++;
                            if(myFile instanceof Object) {
                                MyFile file = (MyFile) myFile;
                                out.println(file.getId() + "   " + file.getName());
                                out.flush();
                            }
                        }
                    }
                }
                if(Instruction.DEL.toString().equals(temp)){
                    System.out.println("In: The id of the file to delete");
                    int fileToDelete = Integer.parseInt(in.readLine());
                    if(MyFile.Delete(fileToDelete)){
                        System.out.println("Print: "+Instruction.ACK);
                        out.println(Instruction.ACK);
                        out.flush();
                    }
                    else {
                        System.out.println("Print: "+Instruction.DND);
                        out.println(Instruction.DND);
                        out.flush();
                    }
                }
                if(Instruction.GET.toString().equals(temp)){
                    System.out.println("In: Which file to send to the client");
                    int fileToDownload = Integer.parseInt(in.readLine());
                    if(fileToDownload-1<MyFile.myFiles.size()){
                        System.out.println("Out: "+ Instruction.ACK);
                        out.println(Instruction.ACK);
                        out.flush();
                        System.out.println("In: "+ Instruction.ACK);
                        temp = in.readLine();
                        if (Instruction.ACK.toString().equals(temp))
                        {
                            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                            if(MyFile.myFiles.get(fileToDownload-1) instanceof MyFile){
                                MyFile file = (MyFile) MyFile.myFiles.get(fileToDownload-1);
                                String name = file.getName();
                                byte[] nameBytes = name.getBytes();
                                System.out.println("out: The length of the name of the file");
                                dataOutputStream.writeInt(nameBytes.length);
                                System.out.println("out: The name of the file");
                                dataOutputStream.write(nameBytes);
                                System.out.println("out: The length of the file");
                                dataOutputStream.writeInt(file.getData().length);
                                System.out.println("out: The length of the content of the file");
                                dataOutputStream.write(file.getData());
                            }
                        }
                    }else {
                        System.out.println(Instruction.DND);
                        out.println(Instruction.DND);
                        out.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
