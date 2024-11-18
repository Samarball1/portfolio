/**
 * package in Java that provides a set of lightweight 
 *  components for creating graphical user interfaces (GUIs). These components are 
 * written entirely in Java and are platform-independent, 
 * meaning they can run on any operating system that supports Java
*/
import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        //set the variables of the pixels:board dimesions in pixels***
        //also have the same dimmensions as our background image
        
        int boardWidth = 360;
        int boardHeight = 640;

        //Create JFrame and pass in title of application when opened
        JFrame frame = new JFrame("Flappy Bird :)");


        //Place Window at the center of the screen
        frame.setSize(boardWidth, boardHeight);

        frame.setLocationRelativeTo(null);
        
        //User cannot resize window
        frame.setResizable(false);
        
        //Terminate program on close button
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Instantiate the FlappyBird() Class
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        
        //Dimmensions accounts for title bar as well, thus introduce .pack() to exclude title bar dims
        frame.pack();
        
        frame.requestFocus();
        //Set visibility true ONLY after the JPanel added to the frame// BLue Panel shown when run is the JPanel which is class of FlappyBird()
        frame.setVisible(true);
    }
}
