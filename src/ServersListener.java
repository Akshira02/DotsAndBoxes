import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ServersListener implements Runnable
{
    private ObjectInputStream is;
    private ObjectOutputStream os;

    // Stores the which player this listener is for
    private char player;

    // static data that is shared between both listeners
    private static char turn = 'R';
    private static GameData gameData = new GameData();
    private static char next = 'R';
    private static ArrayList<ObjectOutputStream> outs = new ArrayList<>();


    public ServersListener(ObjectInputStream is, ObjectOutputStream os, char player) {
        this.is = is;
        this.os = os;
        this.player = player;
        outs.add(os);
    }

    @Override
    public void run() {
        try
        {
            while(true)
            {
                CommandFromClient cfc = (CommandFromClient) is.readObject();

                System.out.println("Running thread " + cfc.getData());
                String data=cfc.getData();
                int c = data.charAt(0) - '0';
                int r = data.charAt(1) - '0';
                if(r != 9)
                    checkGameOver( r, c);
                if(cfc.getCommand()==CommandFromClient.RESTART)
                {
                    sendCommand(new CommandFromServer(CommandFromServer.RESTART,data));
                }
                if(cfc.getCommand()==CommandFromClient.OPEN)
                {
                    if(data.charAt(2) == 'R')
                    {
                        CommandFromServer.rConnected = true;

                    }
                    else {
                        CommandFromServer.bConnected = true;
                    }
                    sendCommand(new CommandFromServer(CommandFromServer.OPEN,data));
                }
                if(cfc.getCommand()==CommandFromClient.RESTART)
                {
                    gameData.reset();

                    if(data.charAt(2) == 'R' && data.charAt(1) == '9')
                    {
                        CommandFromServer.rReset = true;

                    }
                    else if(data.charAt(2) == 'B' && data.charAt(1) == '9') {
                        CommandFromServer.bReset = true;
                    }else if(data.charAt(2) == 'B' && data.charAt(1) == '8') {
                        CommandFromServer.bReset = false;
                    }else if(data.charAt(2) == 'R' && data.charAt(1) == '8') {
                        CommandFromServer.rReset = false;
                    }
                    sendCommand(new CommandFromServer(CommandFromServer.RESTART,data));
                }

                // handle the received command
                if(cfc.getCommand()==CommandFromClient.MOVE &&
                    turn==player && !gameData.isWinner('R',r, c)
                        && !gameData.isWinner('B', r, c))
                {
                    // pulls data for the move from the data field
                    // changes the server side game board
                    gameData.getGrid()[r][c] = player;
                    next = (turn=='R')?'B':'R';

                    // sends the move out to both players
                    sendCommand(new CommandFromServer(CommandFromServer.MOVE,data));

                    // changes the turn and checks to see if the game is over
                    changeTurn();
                    checkGameOver( r, c);
                }
                if(cfc.getCommand()==CommandFromClient.CLOSE)
                {
                    gameData.reset();
                    sendCommand(new CommandFromServer(CommandFromServer.CLOSE,data));
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void changeTurn()
    {
        // changes the turn
        if(turn=='R')
            turn = 'B';
        else
            turn ='R';

        // informs both client of the new player turn
        if (turn == 'R') {
            sendCommand(new CommandFromServer(CommandFromServer.R_TURN, null));
        }
        else {
            sendCommand(new CommandFromServer(CommandFromServer.B_TURN, null));
        }
    }

    public void checkGameOver(int r, int c)
    {
        int command = -1;
//        if(gameData.isCat())
//            command = CommandFromServer.TIE;
        if(gameData.isWinner('R',r, c)) {
            command = CommandFromServer.X_WINS;
        }
        else if(gameData.isWinner('B',r, c)) {
            command = CommandFromServer.O_WINS;
        }

        // if the game ended, informs both clients of the game's end state
        if(command!=-1) {
            sendCommand(new CommandFromServer(command, null));
        }
        if(command==1) {
            sendCommand(new CommandFromServer(command, null));
        }
    }
    public void sendCommand(CommandFromServer cfs)
    {
        // Sends command to both players
        for (ObjectOutputStream o : outs) {
            try {
                o.writeObject(cfs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
