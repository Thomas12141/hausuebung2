package pis.hue2.server;

import java.io.*;
import java.net.Socket;
public class ClientHandler implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private static int counter = 0;
    public ClientHandler(Socket client) throws IOException {
        this.client = client;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream());
    }
    @Override
    public void run() {
        try {
            while (true) {
                String temp = in.readLine();
                if (Instruction.ifCON(temp)) {
                    if (counter < 3) {
                        out.println(Instruction.ACK);
                        out.flush();
                        counter++;
                    } else {
                        out.println(Instruction.DND);
                        out.flush();
                    }
                }
                if (Instruction.ifPUT(temp)) {
                    out.println(Instruction.ACK);
                    out.flush();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataInputStream dataInputStream = new DataInputStream(client.getInputStream());

                            int fileNameLength = dataInputStream.readInt();

                            if(fileNameLength>0){
                                byte[] fileNameBytes = new byte[fileNameLength];
                                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                                String fileName = new String(fileNameBytes);

                                int fileContentLength = dataInputStream.readInt();

                                if(fileContentLength>0){
                                    byte[] fileContentBytes = new byte[fileContentLength];
                                    dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
                                }
                            }
                        }
                    }).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileExtension(String fileName){
        int i = fileName.lastIndexOf('.');
        if(i>0)
            return fileName.substring(i+1);
        return "";
    }
}
