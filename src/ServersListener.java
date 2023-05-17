import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ServersListener implements Runnable
{
    private ObjectInputStream is;
    private ObjectOutputStream os;

    // Stores the which player this listener is for
    private char player;

    private int nextPoint, index;

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
        gameData.reset();
    }

    @Override
    public void run() {
        try
        {
            while(true)
            {
                CommandFromClient cfc = (CommandFromClient) is.readObject();

                String data=cfc.getData();
                int c = data.charAt(0) - '0';
                int r = data.charAt(1) - '0';
                nextPoint = data.charAt(2) - '0';
                if(data.length() >4)
                    index = Integer.parseInt(data.substring(4));
                checkGameOver();

                if(cfc.getCommand()==CommandFromClient.RESTART)
                {
                    sendCommand(new CommandFromServer(CommandFromServer.RESTART,data));
                }
                if(cfc.getCommand()==CommandFromClient.OPEN)
                {
                    if(data.charAt(3) == 'R')
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
                    if(data.charAt(3) == 'R' && data.charAt(1) == '9')//Righclicked - Red
                    {
                        CommandFromServer.rReset = true;

                    }
                    else if(data.charAt(3) == 'B' && data.charAt(1) == '9') {//Rightclicked - Blue
                        CommandFromServer.bReset = true;
                    }else if(data.charAt(3) == 'B' && data.charAt(1) == '8') {
                        CommandFromServer.bReset = false;
                    }else if(data.charAt(3) == 'R' && data.charAt(1) == '8') {
                        CommandFromServer.rReset = false;
                    }
                    sendCommand(new CommandFromServer(CommandFromServer.RESTART,data));
                }

                // handle the received command
                if(cfc.getCommand()==CommandFromClient.MOVE &&
                    turn==player ) {

                    // pulls data for the move from the data field
                    // changes the server side game board

                    int index = (c * 5 - 1) + r + 1;

                    char[] val = gameData.points.get(index);
                    if (val[nextPoint] != '9') {
                        val[nextPoint] = player;
                        System.out.println("Put data into gamepoints index " + index + " nextPoint " + nextPoint + " val " + player);
                    }
                    gameData.points.put(index, val);

                    int claim = checkGameScore(index, turn, nextPoint);
                    if(claim != -1) {
                        System.out.println("Claim " + turn + Integer.toString(claim));
                        sendCommand(new CommandFromServer(CommandFromServer.CLAIM, turn + Integer.toString(claim)));
                    }


                    next = (turn == 'R') ? 'B' : 'R';

                    // sends the move out to both players
                    sendCommand(new CommandFromServer(CommandFromServer.MOVE, data));

                    int countR = gameData.scoreForPlayer('R');
                    int countB = gameData.scoreForPlayer('B');
                    System.out.println("Score " + "R: " + countR + " B:" + countB);

                    sendCommand(new CommandFromServer(CommandFromServer.SCORE, "R: " + countR + " B:" + countB));
                    if(countR + countB == 16) {
                        if (countR > countB) {
                            System.out.println("R Wins r " + countR);
                            sendCommand(new CommandFromServer(CommandFromServer.X_WINS, data));
                        }
                        else if (countB > countR) {
                            System.out.println("Count B " + countB);
                            sendCommand(new CommandFromServer(CommandFromServer.O_WINS, data));
                        }
                        else {
                            System.out.println("Tie ");
                            sendCommand(new CommandFromServer(CommandFromServer.TIE, data));
                        }
                    }
                    // changes the turn and checks to see if the game is over
                    changeTurn();
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

    public void checkGameOver()
    {
        int command = -1;

        if(!gameData.canMove()) {
//            sendCommand(new CommandFromServer(CommandFromServer.B_TURN, null));
           System.out.println("Game over");
        }
    }

    public int checkGameScore(int index, char current, int nextpoint) {
        int ind = -1;
        System.out.println("Checking claim for " + index + " current " + current);

        boolean squ = gameData.claimIfSquareFormed(index, current);
        if (squ == true)
            return index;

        if ((squ == false) && (index - 5 >= 0))
            squ = gameData.claimIfSquareFormed(index - 5, current);
        if (squ == true)
            return index - 5;

        if ((squ == false) && (index % 5 != 0) && (index - 1 >= 0))
            squ = gameData.claimIfSquareFormed(index - 1, current);
        if (squ == true)
            return index - 1;

        return -1;
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
