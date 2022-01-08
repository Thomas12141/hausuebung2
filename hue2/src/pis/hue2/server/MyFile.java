package pis.hue2.server;

public class MyFile {
    private int id;
    private String name;
    private byte[] data;
    private String fileExtension;
    private int idCounter =0;

    public MyFile(String name, byte[] data, String fileExtension){
        this.id= idCounter;
        idCounter--;
        this.name= name;
        this.data = data;
        this.fileExtension = fileExtension;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
