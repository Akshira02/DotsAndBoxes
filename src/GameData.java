import java.util.HashMap;

public class GameData
{
//    public static HashMap<Integer, char[]> lines = new HashMap<Integer, char[]>();
    public  HashMap<Integer, char[]> points = new HashMap<Integer, char[]>();

    public static boolean restartR = false;
    public static boolean restartB = false;

    private char next = 'R';

    public void reset()
    {
        char[] empty;
        for(int r=0;r<25; r++) {
            if(r==24)
                empty = new char[]{'9', '9'};
            else if((r+1)%5 == 0)
               empty = new char[]{'9', ' '}; //No next point on the right end
            else if(r>=20)
                empty = new char[]{' ', '9'}; //No next point for the last column
            else
                empty = new char[]{' ', ' '};
            this.points.put(r, empty.clone());
        }
    }

    public void setNext(char next){
        this.next = next;
    }

    public char getNext(){
        return  this.next ;
    }

   
    public boolean isCat()
    {
//        for (int i = 0; i < grid.length; i++) {
//            if (grid[i] == null) {
//                return false;
//            }
//        }
        return true;
    }

    public int isWinner(char current) {
        int countPlayer = scoreForPlayer(current);
        char opponent = (current == 'R') ? 'B' : 'R';
        int countOpponent = scoreForPlayer(opponent);

        if (countOpponent == countPlayer)
            return 0;
        else if (countOpponent > countPlayer)
            return -1;
        else
            return 1;
    }

    public int scoreForPlayer(char player) {
        int count = 0;
        for (int i = 0; i < 25; i++) {
            boolean flag = false;
            //check right & down
            if ((points.get(i)[0] == player) && (points.get(i)[1] == player)) {
                //check down right
                if (points.get(i + 1)[1] == player) {
                    //check right down
                    if (points.get(i + 5)[0] == player) {
                        count++;
                    }
                }

            }
        }
        return count;
    }

    public boolean canMove() {
        System.out.println("Check move");
        for (int i = 0; i < 25; i++) {
            char[] nextLines = points.get(i);
            System.out.println("Chars i" + i+ "#" + nextLines[0] + "#" + nextLines[1] + "#");
            if (nextLines[0] == ' ' || nextLines[1] == ' ')
                return true;
        }
        System.out.println("Cant move");
        return false;

    }


}
