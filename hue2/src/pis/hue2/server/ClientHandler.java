package pis.hue2.server;

import java.io.*;
import java.net.Socket;
public class ClientHandler implements Runnable{
    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;
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
                if (Instruction.CON.toString().equals(temp)) {
                    if (counter < 3) {
                        out.println(Instruction.ACK);
                        out.flush();
                        counter++;
                    } else {
                        out.println(Instruction.DND);
                        out.flush();
                    }
                }
                if (Instruction.PUT.toString().equals(temp)) {
                    out.println(Instruction.ACK);
                    out.flush();
                    new Thread(() -> {
                        DataInputStream dataInputStream = null;
                        try {
                            dataInputStream = new DataInputStream(client.getInputStream());
                        } catch (IOException e) {
                            out.println(Instruction.DND);
                            out.flush();
                        }

                        int fileNameLength = 0;
                        try {
                            fileNameLength = dataInputStream.readInt();
                        } catch (IOException e) {
                            out.println(Instruction.DND);
                            out.flush();
                        }
                        int fileContentLength = 0;
                        try {
                            fileContentLength = dataInputStream.readInt();
                        } catch (IOException e) {
                            out.println(Instruction.DND);
                            out.flush();
                        }
                        if (fileContentLength>0&&fileNameLength>0) {
                            byte[] fileNameBytes = new byte[fileNameLength];
                            try {
                                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                            } catch (IOException e) {
                                out.println(Instruction.DND);
                                out.flush();
                            }
                            String fileName = new String(fileNameBytes);
                            byte[] fileContentBytes = new byte[fileContentLength];
                            try {
                                dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
                            } catch (IOException e) {
                                out.println(Instruction.DND);
                                out.flush();
                            }
                            LaunchServer.myFiles.add(new MyFile(new String(fileNameBytes), fileContentBytes));
                            out.println(Instruction.ACK);
                            out.flush();
                        }
                    }).start();
                }
                if(Instruction.DSC.toString().equals(temp))
                {
                    out.println(Instruction.DSC);
                    out.flush();
                    counter--;
                }
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
