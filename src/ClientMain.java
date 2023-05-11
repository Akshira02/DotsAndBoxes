import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain
{
    public static void main(String[] args)
    {
        try {
            // create an object for the TTT game
            GameData gameData = new GameData();
            System.out.println("Enter the IP Address of Server. Example 127.0.0.1");
            Scanner sc= new Scanner(System.in);
            String ipAddress = sc.nextLine();

            // create a connection to server
            Socket socket = new Socket(ipAddress,8001);
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());

            // determine if playing as R or B
            CommandFromServer cfs = (CommandFromServer) is.readObject();
            TTTFrame frame;

            // Create the Frame based on which player the server says this client is

            if(cfs.getCommand() == CommandFromServer.CONNECTED_AS_R) {
                frame = new TTTFrame(gameData, os, 'R');
                System.out.println("You have connected as Red");
            }
            else {
                frame = new TTTFrame(gameData, os, 'B');
            }

            // Starts a thread that listens for commands from the server
            ClientsListener cl = new ClientsListener(is,os,frame);
            Thread t = new Thread(cl);
            t.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
