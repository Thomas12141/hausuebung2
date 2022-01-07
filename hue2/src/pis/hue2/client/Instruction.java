package pis.hue2.client;

public enum Instruction {
    CON, DSC, ACK, DND, LST, PUT,GET, DEL,DAT;

    public static boolean ifACK(String temp)
    {
        return ACK.equals(temp);
    }

    public static boolean ifDSC(String temp){
        return DSC.equals(temp);
    }
}
