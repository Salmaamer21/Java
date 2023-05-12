import java.util.Scanner;
import java.util.Random;


public final class TicTacToe extends TwoPlayerBoardGame {
    private char XO = 'X'; 
    private int row; 
    private int col; 


    public TicTacToe(Player p1, Player p2) { //constructor that holds player objects 

        super(new char[3][3], 9, p1, p2);

        for(int i=0; i<3; ++i){
            for(int j=0; j<3; ++j){
                super.board[i][j] = ' '; 
            }
        }

    }

  
    public String toString() {


       System.out.println();
        String boardgame = super.board[0][0] +  "|" + super.board[0][1]  + "|" + super.board[0][2]  + '\n' + "-----" + '\n' + super.board[1][0]  +  "|" + super.board[1][1] + "|" + super.board[1][2]  +'\n' + "-----" + '\n' + super.board[2][0]   + "|" + super.board[2][1]  + "|" + super.board[2][2] + '\n' ;
        return boardgame;
    }


    protected void askForMove() {
        //eeds to only print some stuff onto the console. Look up demo.txt for what it needs to print
        // Note that super.current gives you access to the current player and player class has a method called getName which may prove useful.
        String a_name = super.current.getName();  //removing super doesn't change much ...  
        System.out.print(a_name + ", it's your move and you're " + XO + "s.");
        System.out.println();
        System.out.print("Please choose your move by typing row col where row is 0, 1, or 2 and col is 0, 1, or 2.");
        System.out.println(); 

    }


    

    protected void receiveMove() {
        // if whatever you take in is not 0,1,2 then you should prompt to enter again ...
        Scanner reader = new Scanner(System.in);
        row = reader.nextInt();
        col = reader.nextInt();
    }

    

    protected void generateMove() {
        Random r = new Random();
        row = Math.abs(r.nextInt()) % 3;
        col = Math.abs(r.nextInt()) % 3;

        //generated a move... by adding col and row 
    }



    protected boolean validMove() {

        
       
       if ( ((row<0) || (row>2)) || ((col<0) || (col>2)) ) {
        return false; 
       }

       if (super.board[row][col] != ' ') {
        return false; 
       }


        return true;
    }

    protected void applyMove() {
        
        super.board[row][col] = XO; 

       
    }

    protected boolean someoneWon() {


        String checker = ""; 
        for(int i = 0; i<3 ; i++){
            for(int j=0; j<3; j++){
                checker += super.board[i][j];  // checker = checker + super.board[0][0] + super.board[0][1]... + super[1][1]  
                // System.out.println("iM HERE");
            }
            if( (checker.equals("XXX")) || (checker.equals("OOO")) ) {
                return true; 
            }
            else{
                // System.out.println("also HERE");
                // System.out.println(checker);
                checker = ""; 
            }
        }

        for(int i = 0; i<3 ; i++){
            for(int j=0; j<3; j++){
                checker += super.board[j][i];  // checker = checker + super.board[0][0] + super.board[1][0]... + super[2][0]
            }

            if( (checker.equals("XXX")) || (checker.equals("OOO")) ){
                return true; 
            }
            else{
                // System.out.println(checker);
                checker = ""; 
            }
        }

        String check_diag1 = "" + super.board[0][0] + super.board[1][1] + super.board[2][2]; 
        String check_diag2 = "" + super.board[0][2] + super.board[1][1] + super.board[2][0]; 

        if( (check_diag1.equals("XXX")) || (check_diag1.equals("OOO")) ){
                return true; 
        }

        if( (check_diag2.equals("XXX")) || (check_diag2.equals("OOO")) ){
                return true; 
        }


        
        return false;
    }


    protected void celebrateMove() { 
        System.out.println("That was a winning move!"); 
        System.out.println( super.current.getName() + " (" + XO +") wins!"); 
    }


    protected void prepareForNextMove() {

        super.prepareForNextMove();

            if ( XO == 'X'){
                XO = 'O'; 
            }
            else{
                XO = 'X'; 
            }
        
    }

}
