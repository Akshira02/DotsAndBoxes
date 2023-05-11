import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientsListener implements Runnable
{
    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;
    private TTTFrame frame = null;

    public ClientsListener(ObjectInputStream is,
                           ObjectOutputStream os,
                           TTTFrame frame) {
        this.is = is;
        this.os = os;
        this.frame = frame;

    }

    @Override
    public void run() {
        try
        {

            while(true)
            {
                CommandFromServer cfs = (CommandFromServer)is.readObject();

                // processes the received command
                if(cfs.getCommand() == CommandFromServer.R_TURN) {
                    frame.setTurn('R');
                }
                else if(cfs.getCommand() == CommandFromServer.B_TURN) {
                    frame.setTurn('B');
                }
                else if(cfs.getCommand() == cfs.MOVE)
                {
                    String data = cfs.getData();
                    // pulls data for the move from the data field
                    int c = data.charAt(0) - '0';
                    int r = data.charAt(1) - '0';

                    // changes the board and redraw the screen
                    frame.makeMove(c,r,data.charAt(2));
                }
                else if(cfs.getCommand() == cfs.RESTART)
                {
                    String data = cfs.getData();
                    if(data.charAt(1) == '8') {
                        frame.clearReset(data.charAt(2));
                    }
                    else if(data.charAt(2) == 'R') {
                        frame.setReset('R');
                        frame.setRestartText(data.charAt(2));
                    }
                    else {
                        frame.setReset('B');
                        frame.setRestartText(data.charAt(2));
                    }

                }
                // handles the various end game states
                else if(cfs.getCommand() == CommandFromServer.TIE)
                {
                    frame.setText("Tie game.");
                }
                else if(cfs.getCommand() == CommandFromServer.X_WINS)
                {
                    frame.setWinText('R');
                    frame.setStatus(true);
                }
                else if(cfs.getCommand() == CommandFromServer.CLOSE)
                {
                    String player = (cfs.getData().charAt(2)=='B')?"BLACK":"RED";
                    frame.setText(player + "QUIT, SHUTTING DOWN IN 5 seconds");
                    frame.CloseOtherWindowIn5Seconds(player);

                }
                else if(cfs.getCommand() == CommandFromServer.OPEN)
                {
//                    frame.setWinText('R');
                    String data = cfs.getData();
                    if(data.charAt(2) == 'R') {
                        frame.rOpened = true;
                    }
                    else
                        frame.bOpened  = true;
//                    frame.setStatus(true);
                }


                else if(cfs.getCommand() == CommandFromServer.O_WINS)
                {
                    frame.setWinText('B');
                    frame.setStatus(true);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


}