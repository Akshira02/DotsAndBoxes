import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;

//removed all keylisteners
public class TTTFrame extends JFrame  {
    // Display message
    private String text = "";
    // the letter you are playing as
    private char player;
    // stores all the game data
    private GameData gameData = null;
    // output stream to the server
    ObjectOutputStream os;
    int xoffset = 30;
    int yoffset = 60;
    boolean status = false; //win/lose status.
    boolean bOpened; //Player B Window is open
    boolean rOpened; //Player R Window is open

    char playerTurn; //who has to turn next
    private static boolean  pressed = false; //when it's a turn for Red/Blue, it is set to false. It is set to true when a valid selection is done

    public TTTFrame(GameData gameData, ObjectOutputStream os, char player)
    {
        super("TTT Game");
        // sets the attributes
        this.gameData = gameData;
        this.os = os;
        this.player = player;
        gameData.reset();


        // makes closing the frame close the program
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Set initial frame message - update server that it is opened. Client
        if(player == 'R') {
            text = "Waiting for B to Connect";
            try {
                os.writeObject(new CommandFromClient(CommandFromClient.OPEN, "99" + player));
                os.flush();
            }catch (Exception exception){
                System.out.println(exception.getMessage());
            }
            rOpened = true;
        }else if (player == 'B'){
            try {
                os.writeObject(new CommandFromClient(CommandFromClient.OPEN, "99" + player));
                os.flush();
            }catch (Exception exception){
                System.out.println(exception.getMessage());
            }
            rOpened = true;
            bOpened = true;
        }


        setSize(450,450);
        setResizable(false);
        setAlwaysOnTop(true);
        setVisible(true);


        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //make it here so it can see which white rectangle is clicked
                int r = ((e.getX() - xoffset) / 50);
                int c = ((e.getY() - yoffset) / 50);
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (status != true) {
                        //RR = R clicked this point as the first circle
                        //
                        if (player == 'R' && pressed == false && rOpened && bOpened  && playerTurn =='R' ) {
                            if (gameData.getGrid()[r][c] == ' ') {
                                gameData.getGrid()[r][c] = 'R';
                                text = "B's turn";
                                pressed = true;
                                try {
                                    os.writeObject(new CommandFromClient(CommandFromClient.MOVE, "" + c + r + player));
                                    os.flush();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }

                            } else
                                text = "Already filled";
                        }
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (player == 'B' && pressed == false && rOpened && bOpened && playerTurn =='B') {
                                if (gameData.getGrid()[r][c] == ' ') {
                                    gameData.getGrid()[r][c] = 'B';
                                    text = "R's Turn";
                                    pressed = true;
                                    try {
                                        os.writeObject(new CommandFromClient(CommandFromClient.MOVE, "" + c + r + player));
                                        os.flush();
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                } else
                                    text = "Already filled";
                            }

                }


                    }
                }

                //Right Click
                if (e.getButton() == MouseEvent.BUTTON3 && status == true) {
                    if (player == 'R') {
                        try {
                            os.writeObject(new CommandFromClient(CommandFromClient.RESTART, "99" + player));
                            os.flush();
                        } catch (Exception exception) {
                            System.out.println(exception.getMessage());
                        }
                    } else if (player == 'B') {
                        try {
                            os.writeObject(new CommandFromClient(CommandFromClient.RESTART, "99" + player));
                            os.flush();
                        } catch (Exception exception) {
                            System.out.println(exception.getMessage());
                        }
                    }
                    gameData.reset();
                    gameData.setNext('R');
                    setRestartText(player);
                }
                repaint();
            }


            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    os.writeObject(new CommandFromClient(CommandFromClient.CLOSE, "55" + player));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
    }

    public void paint(Graphics g) {
//background
        g.setColor(Color.white);
        g.fillRect(0,0,500,500);
        int R = -1;
        int C = -1;


        // draws the display text to the screen
        g.setColor(Color.BLUE);
        g.setFont(new Font("Times New Roman", Font.BOLD, 20));
        g.drawString(text, 20, 55);

        // grid
        g.setColor(Color.white);
        int xoffset = 30;
        int yoffset = 60;
     for (int y = 0; y < 5; y++) {
         for (int x = 0; x < 5; x++) {
             g.setColor(Color.black);
               g.fillOval(xoffset + x * 70, yoffset + y * 60, 5, 5);
           }
        }


        // player moves
        for (int r = 0; r < gameData.getGrid().length; r++)
            for (int c = 0; c < gameData.getGrid()[0].length; c++) {
                if (gameData.getGrid()[r][c] == 'B') {
                    g.setColor(Color.BLACK);
                    R = r;
                    C = c;
                    g.fillOval(xoffset + r * 70, yoffset + c * 60, 50, 50);
                    ;

                } else if (gameData.getGrid()[r][c] == 'R') {
                    g.setColor(Color.RED);
                    R = r;
                    C = c;
                    g.fillOval(xoffset + r * 70, yoffset + c * 60, 50, 50);
                    ;
                }
            }
    }

    public void setText(String text) {
        this.text = text;
        repaint();
    }

    public void setWinText(char winner) {
        if(winner == player)
            this.text = "You WIN!";
        else
            this.text = "You LOSE!";
        repaint();
    }

    public void setReset(char player) {
        if(player == 'R')
            gameData.restartR = true;
        else
            gameData.restartB = true;
    }

    public void clearReset(char player) {
        gameData.restartR = false;
        gameData.restartB = false;
        if(player == 'R' && this.player == 'R' )
            this.text = "Your Turn!";
        if(player == 'B' && this.player == 'B' )
            this.text = "R's Turn!";
        repaint();
    }

    public void setRestartText(char other) {

        //Both reset
        if (gameData.restartR  || gameData.restartB) {
            if (player == other)
                text = "Waiting for " + (player == 'R' ? "Black" : "Red") + " to agree to a new game!";
            else
                text = (player == 'R' ? "Black" : "Red") + " is ready. Right click to start a new game";
        }
        if (gameData.restartR && gameData.restartB) {
            if (player == 'R') {
                text = "Your Turn";

            }
            if (player == 'B') {
                text = "R's Turn";

            }
            pressed = false;
            status = false;
            gameData.reset();

        }

        repaint();

        }



    public void setStatus(boolean status) {
        this.status = status;
    }

    public void CloseOtherWindowIn5Seconds(String player) {
        text = player + " QUIT, SHUTTING DOWN IN 5 seconds";
        repaint();
        try {
            Thread.sleep(1000);
            text = player + " QUIT, SHUTTING DOWN IN 4 seconds";
            repaint();
            Thread.sleep(1000);
            text = player + " QUIT, SHUTTING DOWN IN 3 seconds";
            repaint();
            Thread.sleep(1000);
            text = player + " QUIT, SHUTTING DOWN IN 2 seconds";
            repaint();
            Thread.sleep(1000);
            text = player + " QUIT, SHUTTING DOWN IN 1 seconds";
            repaint();
            Thread.sleep(1000);
            System.exit(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public void getPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public void setTurn(char turn) {
        if(turn==player) {
            pressed = false;
            playerTurn = player;
            text = "Your turn";
        }
        else
        {
            text = turn+"'s turn.";
        }
        repaint();
    }

    public void makeMove(int c, int r, char letter)
    {
        gameData.getGrid()[r][c] = letter;
        repaint();
    }

}
