package pis.hue2.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Thread;

/**
 * In der Klasse MyFile werden die Dateien verarbeitet
 * @author Thomas Fidorin, Fabian Woyda
 */
public class MyFile {
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    private int id;
    private String name;
    private byte[] data;
    private static int idCounter =1;

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

    private void setId(int id) {
        this.id = id;
    }

    public static boolean Delete(int fileToDelete){
        if (fileToDelete<idCounter) {
            myFiles.remove(fileToDelete-1);
            idCounter -=1;
            for (int i=0;i< idCounter-1;i++)
            {
                myFiles.get(i).setId(i+1);
            }
            return true;
        }
        return false;
    }

    public byte[] getData() {
        return data;
    }
}
