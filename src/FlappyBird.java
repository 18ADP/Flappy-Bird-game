import java.awt.*;
import java.awt.event.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

import javax.management.StringValueExp;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardwidth = 360;
    int boardhegiht = 640;
    
    //image
    Image backgroundImg;
    Image birdImg;
    Image topppImg;
    Image btmppImg;

    //bird
    int birdx = boardwidth/8;
    int birdy = boardhegiht/2;
    int birdwidth = 34;
    int birdheight = 24;

    class Bird{
        int x = birdx;
        int y = birdy;
        int width = birdwidth;
        int height = birdheight;
        Image img;

        Bird (Image img){
            this.img = img;
        }
    }

    //pipes
    int pipeX = boardwidth;
    int pipeY = 0;
    int ppwidth = 64;
    int ppheight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = ppwidth;
        int height = ppheight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4;   //moves pipe to the left speed
    int velocityY = 0;    //moves bird up or down speed
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameloop;
    Timer placepipestimer;
    boolean gameover = false;
    double score;

    FlappyBird(){
        setPreferredSize(new Dimension(boardwidth,boardhegiht));
        //setBackground(Color.BLUE);
        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topppImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        btmppImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);

        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placepipestimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placepipe();
            }
        });
        placepipestimer.start();

        //game timer
        gameloop = new Timer(1000/60, this);
        gameloop.start();
    }  
    
    public void placepipe(){
        int randompipeY = (int)(pipeY - ppheight/4 - Math.random() * (ppheight/2));
        int opening = boardhegiht/4;

        Pipe toppipe = new Pipe(topppImg);
        toppipe.y = randompipeY;
        pipes.add(toppipe);

        Pipe btmpipe = new Pipe(btmppImg);
        btmpipe.y = toppipe.y + ppheight + opening;
        pipes.add(btmpipe);
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //background
        g.drawImage(backgroundImg, 0, 0, boardwidth, boardhegiht, null);

        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for(int i=0; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if(gameover){
            g.drawString("Game Over: " + String.valueOf((int)score), 10, 35);
        }else{
            g.drawString(String.valueOf((int)score), 10, 35);
        }
    }

    public void move(){
        //bird
        velocityY = velocityY + gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for(int i=0; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score = score + 0.5;
            }

            if(collision(bird, pipe)){
                gameover = true;
            }
        }

        if(bird.y > boardhegiht){
            gameover = true;
        }
    }

    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
        a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
        a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
        a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameover){
            placepipestimer.stop();
            gameloop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE);
        velocityY = -9;
        if(gameover){
            //restart the game by resetting all the conditions
            bird.y = bird.y;
            velocityY = 0;
            pipes.clear();
            score = 0;
            gameover = false;
            gameloop.start();
            placepipestimer.start();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
