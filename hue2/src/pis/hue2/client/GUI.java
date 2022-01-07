package pis.hue2.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class GUI implements ActionListener{

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

    public int defaultPort;

    public String defaultHost;

    /*
     Konstruktor
    */
    GUI(String host, int port){
        defaultHost = host;
        defaultPort = port;
        createGUI();
    }

    void createGUI(){
        JFrame frame = new JFrame();
        frame.setSize(650, 500);

        frame.setTitle("Datatransfer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3,1));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setVisible(true);

        /*
         Textfelder für Default Werte
        */
        JPanel serverPortPanel = new JPanel();

        serverPortPanel.setLayout(new BoxLayout(serverPortPanel, BoxLayout.X_AXIS));
        tfServer = new JTextField(defaultHost);
        tfPort = new JTextField("" + defaultPort);
        tfPort.setHorizontalAlignment(SwingConstants.LEFT);
        tfPort.setEditable(false);
        tfServer.setEditable(false);


        serverPortPanel.add(new JLabel("Host:  "));
        serverPortPanel.add(tfServer);
        serverPortPanel.add(new JLabel("Port:  "));
        serverPortPanel.add(tfPort);
        panel.add(serverPortPanel);

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


        /*
         Visibility
        */
        frame.add(btnPanel, BorderLayout.SOUTH);
        frame.add(serverPortPanel,BorderLayout.NORTH);
        btnConnect.setVisible(true);
        btnDelete.setVisible(true);
        btnDisconnect.setVisible(true);
        btnDownload.setVisible(true);
        btnLst.setVisible(true);
        btnUpload.setVisible(true);
        btnPanel.setVisible(true);
        tfServer.setVisible(true);
        tfPort.setVisible(true);

        serverPortPanel.setVisible(true);
        //frame.pack();
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*
         Senden von Nachrichten nach klicken eines Buttons
        */
    }
}
