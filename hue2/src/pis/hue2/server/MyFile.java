package pis.hue2.server;

/**
 * In der Klasse MyFile werden die Dateien verarbeitet
 * @author Thomas Fidorin, Fabian Woyda
 */
public class MyFile {

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
}
