package pis.hue2.server;

public enum Instruction {
    CON, DSC, ACK, DND, LST, PUT,GET, DEL,DAT;

    public static boolean ifACK(String temp)
    {
        return ACK.equals(temp);
    }

    public static boolean ifDSC(String temp){
        return DSC.equals(temp);
    }

    public static boolean ifDEL(String temp){
        return DEL.equals(temp);
    }

    public static boolean ifCON(String temp){
        return "CON".equals(temp);
    }
}