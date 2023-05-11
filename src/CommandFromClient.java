import java.io.Serializable;

public class CommandFromClient implements Serializable
{
    // The command being sent to the server
    private int command;
    // Text data for the command
    private String data ="";
    public static boolean restartRED = false;
    public static  boolean restartBLACK = false;

    // Command list
    public static final int MOVE    =0;
    public static final int OPEN    =9;
    public static final int RESTART =8;
    public static final int CLOSE =5;


    public CommandFromClient(int command, String data) {
        this.command = command;
        this.data = data;
    }

    public int getCommand() {
        return command;
    }

    public String getData() {
        return data;
    }
}
