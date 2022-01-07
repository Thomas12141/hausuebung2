package pis.hue2.client;

import java.awt.*;
import javax.swing.*;


public class GUI extends JFrame{

    /*
     Um Nachrichten anzuzeigen
    */
    private JTextField tf;

    /*
     Um pis.hue2.server.Server Adresse und Port anzuzeigen
    */
    private JTextField tfServer, tfPort;

    /*
     Buttons zum Connection, Datei löschen, Disconnecten, Datei Download, anzeigen der Order und Datei Hochladen
    */
    private JButton btnConnect, btnDelete, btnDisconnect, btnDownload,btnLst,btnUpload;

    private boolean connected;

    private Client client;

    private int defaultPort;

    private String defaultHost;

    /*
     Konstruktor
    */
    GUI(String host, int port){
        defaultHost = host;
        defaultPort = port;

        /*
         Textfelder für Default Werte
        */
        tfServer = new JTextField(host);
        tfPort = new JTextField("" + port);
        tfPort.setHorizontalAlignment(SwingConstants.RIGHT);
        createGUI();
    }

    void createGUI(){
        JFrame frame = new JFrame();
        frame.setTitle("Datatransfer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3,1));

        /*
        Buttons Connect, Delete, Disconnect, Download, List, Upload
        */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnConnect = new JButton("Connect");
        btnPanel.add(btnConnect);

        btnDelete = new JButton("Delete");
        btnPanel.add(btnDelete);

        btnDisconnect = new JButton("Disconnect");
        btnPanel.add(btnDisconnect);

        btnDownload = new JButton("Download Data");
        btnPanel.add(btnDownload);

        btnLst = new JButton("List");
        btnPanel.add(btnLst);

        btnUpload = new JButton("Upload Data");
        btnPanel.add(btnUpload);
    }

}
