package pis.hue2.client;

public enum Instruction {
    CON, DSC, ACK, DND, LST, PUT,GET, DEL,DAT;

    public static boolean ifACK(String temp)
    {
        return "ACK".equals(temp);
    }
}
