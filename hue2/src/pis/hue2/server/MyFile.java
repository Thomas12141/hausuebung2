package pis.hue2.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.WeakHashMap;

/**
 * In der Klasse MyFile werden die Dateien verarbeitet
 * @author Thomas Fidorin, Fabian Woyda
 */
public class MyFile {
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    private int id;
    private String name;
    private byte[] data;
    private int idCounter =0;

    /**
     * Konstruktor der Klasse MyFile
     * @param name Name der Datei
     * @param data ByteArray fuer den Inhalt der Datei
     */
    public MyFile(String name, byte[] data){
        this.id= idCounter;
        idCounter++;
        this.name= name;
        this.data = data;
    }

    /**
     * Liefert die ID
     * @return  ID der Datei
     */
    public int getId() {
        return id;
    }

    /**
     * Liefert den Namen
     * @return  Namen der Datei
     */
    public String getName() {
        return name;
    }

    public static void main(String[] args) {

        System.out.println(myFiles.size());
    }

    public static void addMyFile(MyFile myFile){

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(myFile.name));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(line != null){
            myFiles.add(myFile);
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
