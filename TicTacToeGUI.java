import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.GridLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public final class TicTacToeGUI { 
    
    public static void main(String[] args) {
        TicTacToeGUI t = new TicTacToeGUI();  
    }

    private State state; //reference instances of inner classes called State
    private GUI gui;//reference instances of inner classes called gui

    private void startNewGame() {
        if (gui != null) {
            gui.dispose(); // dispose if gui is not null 
        }
        state = new State(); // game starts all over again 
        gui = new GUI(); //creates the frame all over again and all the tiles have the null state with empty strings in tiles.
    }

    public TicTacToeGUI(){
        startNewGame();
    }


    //do not change state class!
    private class State {
        // store the state of a game separately from the GUI
        //write useful methods like prepareForNextMove, applyMove, someoneWon, boardFull without having to think about how the GUI is implemented.
        private char XO = 'X';

        private void prepareForNextMove() {
            XO = (XO == 'X') ? 'O' : 'X';
        }


        private final char[][] board = new char[][] {{' ',' ',' '},
                                                     {' ',' ',' '},
                                                     {' ',' ',' '}};

        private void applyMove(int row, int col) {
            board[row][col] = XO;
        }


        private boolean someoneWon() {
            if (' ' != board[0][0] && board[0][0] == board[0][1] && board[0][1] == board[0][2]) { return true; }
            if (' ' != board[1][0] && board[1][0] == board[1][1] && board[1][1] == board[1][2]) { return true; }
            if (' ' != board[2][0] && board[2][0] == board[2][1] && board[2][1] == board[2][2]) { return true; }

            if (' ' != board[0][0] && board[0][0] == board[1][0] && board[1][0] == board[2][0]) { return true; }
            if (' ' != board[0][1] && board[0][1] == board[1][1] && board[1][1] == board[2][1]) { return true; }
            if (' ' != board[0][2] && board[0][2] == board[1][2] && board[1][2] == board[2][2]) { return true; }

            if (' ' != board[0][0] && board[0][0] == board[1][1] && board[1][1] == board[2][2]) { return true; }
            if (' ' != board[0][2] && board[0][2] == board[1][1] && board[1][1] == board[2][0]) { return true; }

            return false;
        }

        private boolean boardFull() {
            return board[0][0] != ' ' && board[0][1] != ' ' && board[0][2] != ' ' &&
                   board[1][0] != ' ' && board[1][1] != ' ' && board[1][2] != ' ' &&
                   board[2][0] != ' ' && board[2][1] != ' ' && board[2][2] != ' ';
        }
    }

//Since GUI extends JFrame, (gui instanceof JFrame) is true.
    //The constructor of GUI will generate the JFrame that you see in the demonstrational video when the code starts executing.
    private class GUI extends JFrame{ 


            private GUI(){
                super("TicTacToe");
                setSize(600, 600);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //change this 
                setLayout(new GridLayout(3,3));
                
                for(int i=0; i<3 ; ++i){
                    for(int j=0; j<3; ++j){
                        add(new Tile( i, j)); 
                    }
                
                }
                setVisible(true);
            }
       
            //this is an innerclass
            private class Tile extends JButton implements ActionListener { //add ears

                int rowss;
                int colss; 

                private Tile(int i, int j){ 
                    super("");
                    this.rowss = i;
                    this.colss = j;
                    addActionListener(this); //listens to itself 
                }
        

            public void actionPerformed(ActionEvent e) { //add event
                String thetext = ((JButton)e.getSource()).getText(); 

                if(thetext == ""){ 
                    setText("" + state.XO);
                    processValidMove(rowss,colss); 
                }
                else{
                   JOptionPane.showMessageDialog(gui,"Someone has already made a move in that position.","INVALID MOVE", JOptionPane.WARNING_MESSAGE);
                }
            
            }

        }

    }


    private void processValidMove(int row, int col) {

            state.applyMove(row,col);//apply the move 
            
            if ((state.someoneWon()) == true){
                JOptionPane.showMessageDialog(gui,"That was a winning move! "+state.XO+" wins!","WINNING MOVE", JOptionPane.INFORMATION_MESSAGE);
                startNewGame();
            }
            
            else if ((state.boardFull())==true){
                JOptionPane.showMessageDialog(gui,"It's a draw.","DRAW", JOptionPane.INFORMATION_MESSAGE); 
                startNewGame();
            }
            else{
                state.prepareForNextMove();
            }


    }

}




