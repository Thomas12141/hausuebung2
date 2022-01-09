package pis.hue2.client;

import pis.hue2.server.ClientHandler;
import pis.hue2.server.MyFile;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.*;
import java.nio.file.*;
import java.util.ArrayList;

/**
 * Die Klasse GUI ist fuer die Optik zuständig und stoeßt die Prozesse, wie z. B. Liste anzeigen, Datei hoch/runterladen
 * an
 * @author Thomas Fidorin, Fabian Woyda
 */
public class GUI{

    private JTextField tfServer, tfPort;
    private JButton btnConnect, btnDelete, btnDisconnect, btnDownload, btnLst, btnUpload;
    private boolean connected;
    private Client client;
    public int defaultPort;
    public String defaultHost;

    /**
     * Konstruktor fuer die Klasse GUI
     * @param host
     * @param port
     * stoeßt createGUI() Methode an
     */
    GUI(String host, int port) {
        defaultHost = host;
        defaultPort = port;
        createGUI();
    }

    /**
     * Erstellung des Designs
     */
    void createGUI() {
        client = new Client();

        // Groeße und Layout Format des Fensters
        JFrame frame = new JFrame();
        frame.setSize(650, 500);
        frame.setTitle("Datatransfer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Groeße und Layout Format des Panels
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setVisible(true);

        // Textfelder für Default Werte
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
        final JPanel[] jPanel = new JPanel[1];
        // Buttons Connect, Delete, Disconnect, Download, List, Upload
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

        // Visibility bei Buttons auf False, damit man sicher "connecten" muss, bevor man Dateien löschen, anzeigen, hochladen oder herunterladen kann
        // Bei einem "Connect" verschwindet der "Connect" Button und alle anderen Button inkl. "Disconnect" erscheinen.
        // Wird "Disconnected" ist es genau umgekehrt
        frame.add(btnPanel, BorderLayout.SOUTH);
        frame.add(serverPortPanel, BorderLayout.NORTH);
        btnConnect.setVisible(true);
        btnDelete.setVisible(false);
        btnDisconnect.setVisible(false);
        btnDownload.setVisible(false);
        btnLst.setVisible(false);
        btnUpload.setVisible(false);
        btnPanel.setVisible(true);
        tfServer.setVisible(true);
        tfPort.setVisible(true);

        serverPortPanel.setVisible(true);
        frame.setVisible(true);

        /**
         * Es wird geprüft, ob eine Verbindung zum Client besteht. Ist dies der Fall öffnet sich ein PopUp Fenster mit der Bestätigung.
         * Alle anderen werden Visible und der "Connect" Button verschwindet, damit man sich nicht mehrmals verbindet.
         * Sollte die Verbindung nicht gelingen, bekommen wir eine Fehlermeldung in der Konsole angezeigt.
         */
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(client.Connect()) {
                        JOptionPane.showMessageDialog(frame, "Mit 127.0.0.1 verbunden.");

                        btnDisconnect.setVisible(true);
                        btnConnect.setVisible(false);
                        btnDelete.setVisible(true);
                        btnDisconnect.setVisible(true);
                        btnDownload.setVisible(true);
                        btnLst.setVisible(true);
                        btnUpload.setVisible(true);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        /*
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.Delete();
                    JOptionPane.showMessageDialog(frame, "Datei wurde gelöscht");
                } catch (IOException ioException){
                    ioException.printStackTrace();
                }
            }
        });
         */

        /**
         * Bei einem Klick auf den Upload Button wird ein neuer SwingWorker erstellt, bei dem sich ein neues Fenster öffnet, in dem man sich die hochzuladende Datei
         * aussuchen kann. Diese Dateien werden in der Klasse MyFile verarbeitet.
         * Auch wird überprüft ob die Datei beim Client Server angekommen ist. Ist dies der Fall, öffnet sich ein PopUp Fenster mit der Bestätigung
         */
        btnUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        JFileChooser jFileChooser = new JFileChooser();
                        jFileChooser.setDialogTitle("Choose a file to upload");
                        jFileChooser.showOpenDialog(null);
                        File file = jFileChooser.getSelectedFile();
                        if (client.Upload(file))
                            JOptionPane.showMessageDialog(frame, "Datei wurde hochgeladen");
                        return null;
                    }
                }.execute();

            }
        });

        /**
         * Bei einem Klick auf den List Button wird eine Liste mit Dateien angezeigt, die auf dem Server liegen
         * Dateien werden in einer ArrayList gespeichert
         */
        btnLst.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Die ArrayList mit den Files soll hier uebergeben werden

                try {
                    ArrayList<String> arrayList = client.Lst();
                    jPanel[0] = new JPanel(new FlowLayout());
                    for (String str:
                         arrayList) {
                        JLabel jLabel = new JLabel(str);
                        jLabel.setVisible(true);
                        jPanel[0].add(jLabel);
                    }
                    frame.add(jPanel[0], BorderLayout.CENTER);
                    frame.setVisible(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                /*
                try {
                    JPanel jPanel = new JPanel();
                    ArrayList<String> lst = client.Lst();
                    for (int i = 0; i<lst.size(); i++){
                        JLabel jLabel = new JLabel(lst.get(i));
                        jLabel.setVisible(true);
                        jPanel.add(jLabel);
                    }
                    jPanel.setVisible(true);
                    frame.add(jPanel, BorderLayout.CENTER);
                } catch (IOException ioException){
                    ioException.printStackTrace();
                }

                 */
            }
        });

        /*
        btnDownload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.Download();
                    Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
                    JOptionPane.showMessageDialog(frame, "Datei wurde heruntergeladen");
                } catch (IOException ioException){
                    ioException.printStackTrace();
                }
            }
        });

         */

        /**
         * Es wird geprueft, ob der Befehl beim Server angekommen ist. Ist dies der Fall oeffnet sich ein PopUp Fenster mit der Bestaetigung,
         * dass die Verbindung getrennt wurde
         * Alle anderen werden Button verschwinden und der "Connect" Button wird Visible, damit man sich wieder verbinden kann.
         * Sollte der "Disconnect" nicht gelingen, bekommen wir eine Fehlermeldung in der Konsole angezeigt
         */
        btnDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(client.Disconnect()){
                        JOptionPane.showMessageDialog(frame, "Verbindung wurde getrennt");
                        btnConnect.setVisible(true);
                        btnDisconnect.setVisible(false);
                        btnDelete.setVisible(false);
                        btnDisconnect.setVisible(false);
                        btnDownload.setVisible(false);
                        btnLst.setVisible(false);
                        btnUpload.setVisible(false);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });


    }
}
