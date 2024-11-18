//Abstract Window Toolkit is an API used for developing GUI(Graphic User Interfaces) 
//or Window-Based Applications in Java (original and outtdated, modern: JavaFX/ OpenFX)
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import jdk.jfr.MemoryAddress;
//adding KeyListener Interface requires: keyPressed() KeyReleased(), KeyTyped() methods to be invoked
public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 360;
    int boardHeight = 640;

    //Change background images of the game...stores image objects below:
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird Variables. Initial X,Y positons of birds (1/8th of width, halfway down)
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;


    //Create class to hold values  of bird characteristics
    class Bird {
        int x= birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        //constructor of the bird class and pass in image
        Bird(Image img) {
            this.img = img;
        }
    }

    //Pipe variables 
    class Pipe {
    int x = pipeX;
    int y = pipeY;
    int width = pipeWidth;
    int height = pipeHeight;
    Image img;
    boolean passed = false;

    Pipe(Image img) {
        this.img = img;
    }


    }
    //logic.. add field for brd so that it is publically retrievable and can access its characteristics
    Bird bird;
    Pipe pipe;

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    double score = 0; //double because passing each pipe adds to the score (one pass = 2 points) --> add .5 each pass

    //Logic of the game.. implement velocity feature
    int velocityY = 0; //upward direction is negative
    int velocityX = -4; //rightward direction is negative
    int gravity = +1;
    
    //Create Constructor (essentially the canvas)
    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);
        //Draw Out Background

        //ensures flappbird() JPANEL class takes in key events ** THESE TWO IMPORTANT FOR MOVEMENT -->.requestFocus() in App.java
        setFocusable(true);
        addKeyListener(this);

        //Load the images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //Create bird object in the constructor of our game
        bird = new Bird(birdImg);
        pipe = new Pipe(topPipeImg);
        pipes = new ArrayList<>();

        //game timer instantiated... refers to FlappyBird class... "this" refers to actionPerformed() method
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

//Place pipes timer.. create an actionPerfrormed by creating actionListener
//Timer that calls placePipes() every 1.5 sec
placePipesTimer = new Timer(1500, new ActionListener() {
    @Override 
    public void actionPerformed(ActionEvent e) {
        placePipes();
    } 
}
);
placePipesTimer.start();



    }

public void placePipes() {
    // 0 - 128 - (0-1) * pipeHeight/2 --> (0-256 interval)
    int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random() * (pipeHeight/2));
    int openingSpace = (int) (boardHeight/4 - Math.random()*boardHeight/8 + birdHeight);
    Pipe topPipe = new Pipe(topPipeImg);
    topPipe.y = randomPipeY;
    pipes.add(topPipe);

    Pipe bottomPipe = new Pipe(bottomPipeImg);
    bottomPipe.y = topPipe.y + pipeHeight+ openingSpace;
    pipes.add(bottomPipe);
}
//Goal: create game loop that coontinuously calls this function for moving effect
   //invoke function from Jpanel using super().   Paint Component calls draw()
public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
        
}
//add game loop
//draw out graphics... drawing follows the order: top left --> bottom right dims:
    //top left corner position: 0,0
    //width, height params (bottom right postion: (360px, 640px))
public void draw(Graphics g) {
    //draws background
    g.drawImage(backgroundImg, 0,0, boardWidth, boardHeight, null);
    //draws bird
    g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
    //draw pipe(practice)
    for (int i = 0; i < pipes.size(); i++ ) {
        Pipe pipe = pipes.get(i);
        g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
    }

    //Debug to show the number of calls to draw per second... 60 prints per sec
    //System.out.println("draw");

    //score
    g.setColor(Color.red);
    g.setFont(new  Font("Arial", Font.PLAIN, 40));
    if(gameOver) {
        g.drawString("Game Over: " + String.valueOf((int) score), boardWidth/10, boardHeight/2);
    }
    else {
        g.setColor(Color.blue);
        g.drawString(String.valueOf((int) score), boardWidth/2, 35);
    }
}

public void move() {
    velocityY += gravity;
    bird.y += velocityY;
   if (bird.y < boardHeight/2) {
        bird.y = Math.max(bird.y, 0);
    }
    else if (bird.y > boardHeight) {
        gameOver = true;
        //bird.y = Math.min(bird.y, boardHeight - bird.height);
    }
    /// else { bird.y = Math.max(bird.y, boardHeight);
   // } //Caps the bird within the frame of the game (cannot go beyond 0:top)

   for (int i = 0; i < pipes.size(); i++ ) {
    Pipe pipe = pipes.get(i);
    pipe.x += velocityX;

    if (!pipe.passed && bird.x > pipe.x +pipe.width) {
        pipe.passed = true;
        score+= .5;
    }

    if (collision(bird, pipe)) {
        gameOver = true;
    }
}
}

public boolean collision(Bird a, Pipe b) {
    return a.x < b.x + b.width &&  //a's top left corer does not reach b's top right corner
    a.x + a.width > b.x && //a's top right corner passes b's top left corner 
    a.y < b.y + b.height && //a's top left corner doesn't reach b's bottom left corner 
    a.y + a.height > b.y; //a's bottom left corner passes b's top left corner
}
@Override
public void actionPerformed(ActionEvent e) {
    // Auto-generated method stub...generated when we "implemented ActionListener class"..generated every second 60 times
    //action performed every 16 milliseconds 1000mi/60frames 
    move(); //before the repaint in the next frame, update position is moved
    repaint(); 
    if (gameOver) {
        //Stops the actions that were commanded (i.e adding pipes to array, moving postion of bird, ...)
        //stops repainting and updating frames of game
        placePipesTimer.stop();
        gameLoop.stop();
        MemoryCheck.printMemoryUsage();
    }
//I.e every second we are cycling through this method 60 times
    if (false) {
        throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
}



            }
            //Working mainly with space key
@Override
public void keyPressed(KeyEvent e) { 
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -12;
        //reseting game by resetting variables to default
        if(gameOver) {
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
        } }
    }   



@Override //**UNUSED METHOD 
public void keyTyped(KeyEvent e) {}

@Override //**UNUSED METHOD
public void keyReleased(KeyEvent e) {}


public class MemoryCheck {

    public static void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();

        System.out.println("Used Memory: " + memoryUsed + " bytes");
        System.out.println("Total Memory: " + totalMemory + " bytes");
        System.out.println("Max Memory: " + maxMemory + " bytes");
        System.out.println("------------------------");
    }
}
} 