import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain
{
    public static void main(String[] args)
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(8001);

            Socket xCon = serverSocket.accept();
            ObjectOutputStream xos = new ObjectOutputStream(xCon.getOutputStream());
            ObjectInputStream xis = new ObjectInputStream(xCon.getInputStream());

            xos.writeObject(new CommandFromServer(CommandFromServer.CONNECTED_AS_R,null));
            System.out.println("Red has Connected.");

            ServersListener sl = new ServersListener(xis,xos,'R');
            Thread t = new Thread(sl);
            t.start();

            Socket oCon = serverSocket.accept();
            ObjectOutputStream oos = new ObjectOutputStream(oCon.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(oCon.getInputStream());

            oos.writeObject(new CommandFromServer(CommandFromServer.CONNECTED_AS_B,null));
            System.out.println("Black has Connected.");

            sl = new ServersListener(ois,oos,'B');
            t = new Thread(sl);
            t.start();


            xos.writeObject(new CommandFromServer(CommandFromServer.R_TURN,null));
            oos.writeObject(new CommandFromServer(CommandFromServer.R_TURN,null));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}