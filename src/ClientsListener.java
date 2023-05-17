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
                    int nextPoint = Integer.parseInt(String.valueOf(data.charAt(2)));

                    int index = Integer.parseInt(data.substring(4));

                    // changes the board and redraw the screen
                    frame.makeMove(index,nextPoint,data.charAt(3));

                }
                else if(cfs.getCommand() == cfs.CLAIM)
                {
                    frame.drawSquare(cfs.getData().charAt(0), Integer.parseInt(cfs.getData().substring(1)));
                }
                else if(cfs.getCommand() == cfs.SCORE)
                {
                    frame.setScore(cfs.getData());
                }
                else if(cfs.getCommand() == cfs.RESTART)
                {

                    String data = cfs.getData();
                    System.out.println("Received in client rightclick " + data.charAt(1) + "#" + data.charAt(3));
                    if(data.charAt(1) == '8') {
                        frame.clearReset(data.charAt(3));
                    }
                    else if(data.charAt(3) == 'R') {
                        frame.setReset('R');
                        frame.setRestartText(data.charAt(3));
                    }
                    else {
                        frame.setReset('B');
                        frame.setRestartText(data.charAt(3));
                    }

                }
                // handles the various end game states
                else if(cfs.getCommand() == CommandFromServer.TIE)
                {
                    System.out.println("Tie");
                    frame.setText("Tie game.");
                    frame.setStatus(true);
                }
                else if(cfs.getCommand() == CommandFromServer.X_WINS)
                {
                    System.out.println("X wins client");
                    frame.setWinText('R');
                    frame.setStatus(true);
                }
                else if(cfs.getCommand() == CommandFromServer.CLOSE)
                {
                    String player = (cfs.getData().charAt(3)=='B')?"BLACK":"RED";
                    frame.setStatus(false);
                    frame.setText(player + "QUIT, SHUTTING DOWN IN 5 seconds");

                    frame.CloseOtherWindowIn5Seconds(player);

                }
                else if(cfs.getCommand() == CommandFromServer.OPEN)
                {
//                    frame.setWinText('R');
                    String data = cfs.getData();
                    if(data.charAt(3) == 'R') {
                        frame.rOpened = true;
                    }
                    else
                        frame.bOpened  = true;
//                    frame.setStatus(true);
                }


                else if(cfs.getCommand() == CommandFromServer.O_WINS)
                {
                    System.out.println("O Wins");
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