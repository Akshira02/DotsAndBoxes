public class GameData
{
    private char[][] grid = {{' ',' ', ' ',' '},{' ',' ', ' ',' '},{' ',' ', ' ',' '},{' ',' ', ' ',' '}};

    public char[][] getGrid()
    {
        return grid;
    }

    public boolean restartR = false;
    public boolean restartB = false;

    private char next = 'R';

    public void reset()
    {
        for(int r=0;r<grid.length; r++)
            for(int c=0; c<grid[0].length; c++)
                grid[r][c]=' ';
    }

    public void setNext(char next){
        this.next = next;
    }

    public char getNext(){
        return  this.next ;
    }

   
    public boolean isCat()
    {
        for (int i = 0; i < grid.length; i++) {
            if (grid[i] == null) {
                return false;
            }
        }
        return true;
    }

    public boolean isWinner(char letter, int r, int c)
    {
        for(int i=0;i<7;i++)
        {
            for(int j=0;j<6;j++)
                System.out.print("i:" +i + "j:" + j + " " + grid[i][j] + "  ");
        }
        System.out.println();
        System.out.println("Grid" + grid.length); //7
        System.out.println("Grid 0" + grid[0].length); //6

        //Find left
        int leftCount = 0;
        int rightCount = 0;
        for(int i=1;i<=3;i++)
        {
            if(c-i >= 0) {
                if (grid[r][c - i] == letter) {
                    leftCount = leftCount + 1;
                } else
                    break;
            }
        }
        //Find right
        for(int i=1;i<=3;i++)
        {
            if(c+i < grid[0].length) {
                if (grid[r][c + i] == letter) {
                    rightCount = rightCount + 1;
                } else
                    break;
            }
        }

        if(leftCount + rightCount >= 3 && grid[r][c] ==letter) {
            return true;
        }

        //find upper
        int up = 0;
        int low =0;
        for(int i=1;i<=3;i++)
        {
            if(r-i >= 0) {
                if (grid[r - i][c] == letter) {
                    up = up + 1;
                }
                else
                    break;
            }

        }
        //Find lower
        for(int i=1;i<=3;i++) {
            if (r + i < grid.length) {
                if (grid[r + i][c] == letter) {
                    low = low + 1;
                } else
                    break;
            }
        }
        if(up + low >= 3 && grid[r][c] ==letter) {
            return true;
        }


        //find diagonal \
        int upd = 0;
        int lowd =0;
        for(int i=1;i<=3;i++)
        {
            if((c-i >= 0) &&(r-i >=0) && (grid[r-i][c-i] ==letter)) {
                upd = upd + 1;
            }
            else
                break;
        }
        //Find lower
        for(int i=1;i<=3;i++)
        {
            if((c+i < grid[0].length) &&(r+i < grid.length) && (grid[r+i][c+i] ==letter)) {
                lowd = lowd + 1;
            }
            else
                break;
        }
        if(upd + lowd >= 3 && grid[r][c] ==letter) {
            return true;
        }

        //Find Diagonal (right to left) /
        upd = 0;
        lowd =0;
        for(int i=1;i<=3;i++)
        {
            if((c+i <grid[0].length)  && (r-i >=0) && (grid[r-i][c+i] ==letter)) {
                upd = upd + 1;
            }
            else
                break;
        }
        //Find lower
        for(int i=1;i<=3;i++)
        {
            if((c-i >= 0 ) &&(r+i < grid.length) && (grid[r+i][c-i] ==letter)) {
                lowd = lowd + 1;
            }
            else
                break;
        }
        if(upd + lowd >= 3 && grid[r][c] ==letter) {
            return true;
        }

        return false;
    }


}
