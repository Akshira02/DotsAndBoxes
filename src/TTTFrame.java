import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

//removed all keylisteners
public class TTTFrame extends JFrame  {
    // Display message
    private String text = "";
    // the letter you are playing as
    private char player;
    // stores all the game data
    private static GameData gameData = null;
    // output stream to the server
    ObjectOutputStream os;
    int xOffset = 100;
    int yOffset = 100;
    int squareSide =50;
    boolean status = false; //win/lose status.
    boolean bOpened; //Player B Window is open
    boolean rOpened; //Player R Window is open

    char playerTurn; //who has to turn next
    private static boolean  pressed = false; //when it's a turn for Red/Blue, it is set to false. It is set to true when a valid selection is done


    public TTTFrame(GameData gameData, ObjectOutputStream os, char player)
    {
        super("Dots and Boxes Game");
        // sets the attributes
        this.gameData = gameData;
        this.os = os;
        this.player = player;

        // makes closing the frame close the program
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Set initial frame message - update server that it is opened. Client
        if(player == 'R') {
            text = "Waiting for B to Connect";
            try {
                os.writeObject(new CommandFromClient(CommandFromClient.OPEN, "999" + player));
                os.flush();
            }catch (Exception exception){
                System.out.println(exception.getMessage());
            }
            rOpened = true;
        }else if (player == 'B'){
            try {
                os.writeObject(new CommandFromClient(CommandFromClient.OPEN, "999" + player));
                os.flush();
            }catch (Exception exception){
                System.out.println(exception.getMessage());
            }
            rOpened = true;
            bOpened = true;
        }

        System.out.println("2222");
        setSize(450,450);
        setResizable(false);
        setAlwaysOnTop(true);
        setVisible(true);

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean nextTrue =false;
                int x = ((e.getX() - xOffset) / squareSide);
                int y = ((e.getY() - yOffset) / squareSide);
                int rowStart=-1, colStart=-1, rowEnd =-1, colEnd=-1;
                //Vertical
                if((e.getX() - xOffset >= x * squareSide - 10) && (e.getX() -xOffset <= x * squareSide + 10) ) {
                    rowStart = x;
                    rowEnd = x;
                    if((e.getY() - yOffset >= y * squareSide ) && (e.getY()-yOffset <= (y + 1) * squareSide)) {
                        nextTrue = false;
                        colStart = y;
                        colEnd = y+1;
                    }
                }
                //Horizontal
                else if((e.getY() -yOffset >= y * squareSide - 10) && (e.getY()-yOffset <= y * squareSide + 10)) {
                    nextTrue = true;
                    colStart = y;
                    colEnd = y;
                    if((e.getX() -xOffset >= x * squareSide - 10) && (e.getX() -xOffset <= (x + 1) * squareSide) ) {
                        rowStart = x;
                        rowEnd = x + 1;
                    }
                }
                int index = ( colStart * 5) + rowStart;

                if (e.getButton() == MouseEvent.BUTTON1 && index >=0) {
                    if (status != true) {
                        if (player == 'R' && pressed == false && rOpened && bOpened) { //Red
                            if (nextTrue == true && (gameData.points.get(index)[0] == ' ')) { //Right

                                text = "B's turn";
                                pressed = true;
                                try {
                                    os.writeObject(new CommandFromClient(CommandFromClient.MOVE, "" + colStart + rowStart  + 0 + player + index));
                                    os.flush();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            } else
                                text = "Already filled";

                            if (nextTrue == false && gameData.points.get(index)[1] == ' ') { //Down

                                char[] val = gameData.points.get(index);
                                val[1] = player;
                                gameData.points.put(index, val);

                                text = "B's turn";
                                pressed = true;
                                try {
                                    os.writeObject(new CommandFromClient(CommandFromClient.MOVE, "" + colStart + rowStart  +1 + player + index));
                                    os.flush();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            } else
                                text = "Already filled";

                        }
                        if (player == 'B' && pressed == false && rOpened && bOpened) { //Black
                            if (nextTrue == true && (gameData.points.get(index)[0] == ' ') ) { //Right
                                char[] val = gameData.points.get(index);
                                val[0] = player;
                                gameData.points.put(index, val);

                                text = "R's Turn";
                                pressed = true;
                                try {
                                    os.writeObject(new CommandFromClient(CommandFromClient.MOVE, "" + colStart + rowStart  + 0 + player + index));
                                    os.flush();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            } else
                                text = "Already filled";
                            if (nextTrue == false && (gameData.points.get(index)[1] == ' ')) { //Down
                                char[] val = gameData.points.get(index);
                                val[1] = player;
                                gameData.points.put(index, val);
                                text = "R's Turn";
                                pressed = true;
                                System.out.println("Gamedata index " + index + " - 1 - Playerturn" + playerTurn);
                                try {
                                    os.writeObject(new CommandFromClient(CommandFromClient.MOVE, "" + colStart + rowStart  + 1 + player + index));
                                    os.flush();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            } else
                                text = "Already filled";
                        }
                    }
                }

                //Right Click
                System.out.println("e.getButton() " + e.getButton());
                System.out.println("status " + status);
                if (e.getButton() == MouseEvent.BUTTON3 && status == true) {
                    System.out.println("rightclick " + status);
                    if (player == 'R') {
                        try {
                            System.out.println("Sneding rightclick " + player);
                            os.writeObject(new CommandFromClient(CommandFromClient.RESTART, "999" + player));
                            os.flush();
                        } catch (Exception exception) {
                            System.out.println(exception.getMessage());
                        }
                    } else if (player == 'B') {
                        try {
                            System.out.println("Sneding rightclick " + player);
                            os.writeObject(new CommandFromClient(CommandFromClient.RESTART, "999" + player));
                            os.flush();
                        } catch (Exception exception) {
                            System.out.println(exception.getMessage());
                        }
                    }
                    gameData.reset();
                    gameData.setNext('R');
                    setRestartText(player);
                }
                System.out.println("4444");
                repaint();
                System.out.println("55555");
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
                    os.writeObject(new CommandFromClient(CommandFromClient.CLOSE, "555" + player));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
    }

    public void paint(Graphics graph) {
        Graphics2D g = (Graphics2D) graph;
        g.setColor(Color.white);
        g.fillRect(0, 0, 500, 500);
        int R = -1;
        int C = -1;


        // draws the display text to the screen
        g.setColor(Color.BLUE);
        g.setFont(new Font("Times New Roman", Font.BOLD, 20));
        g.drawString(text, 20, 55);

        // grid
        g.setColor(Color.white);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                g.setColor(Color.black);
                g.fillOval(xOffset + x * squareSide, yOffset + y * squareSide, 5, 5);
            }
        }

        g.setStroke(new BasicStroke(3));
        for (int i = 0; i < 25; i++) {
            char[] lines = gameData.points.get(i);
            int col = i / 5;
            int row = i % 5;


            for(int k=0;k<2;k++) {
                if (lines[k] == 'R')
                    g.setColor(Color.RED);
                else if (lines[k] == 'B')
                    g.setColor(Color.BLACK);
                System.out.println("i:" + i + "k:" + k + " lines:" + lines[k]);
                if (lines[k] == 'R' || lines[k] == 'B') {
                    if (k == 0) {
                        System.out.println("Row" + row + " Col" + col + "x1:" + xOffset + (row) * squareSide + "y1:"+ yOffset + (col) * squareSide + " x2:" + xOffset + (row + 1) * squareSide + "y2:"+yOffset + (col) * squareSide);
                        g.drawLine(xOffset + (row) * squareSide, yOffset + (col) * squareSide, xOffset + (row + 1) * squareSide, yOffset + (col) * squareSide);
                    }else if(k== 1) {
                        System.out.println("Row" + row + " Col" + col + "x1:" + xOffset + (row) * squareSide + "y1:"+ yOffset + (col) * squareSide + " x2:" + xOffset + (row) * squareSide + "y2:"+yOffset + (col + 1) * squareSide);
                        g.drawLine(xOffset + (row) * squareSide, yOffset + (col) * squareSide, xOffset + (row) * squareSide, yOffset + (col + 1) * squareSide);
                    }
                }
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
        System.out.println("Turn" + turn);
        System.out.println("playerTurn" + playerTurn);
        System.out.println("pressed" + pressed);
        if(turn==player) {
            System.out.println("if");
            pressed = false;
            playerTurn = player;
            text = "Your turn";
        }
        else
        {
            //akshi
            playerTurn = player;
            System.out.println("else");
            text = turn+"'s turn.";
        }
        repaint();
    }

    public void makeMove(int index, int rightOrDown, char player)
    {

        char[] val = gameData.points.get(index);
        if(val[rightOrDown] != '9')
            val[rightOrDown] = player;
        gameData.points.put(index, val);

        //akshi
        repaint();
    }

}
