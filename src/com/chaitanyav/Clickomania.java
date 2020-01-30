package com.chaitanyav;

import com.sun.javafx.application.PlatformImpl;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Clickomania{
    
    int[] colTileCount;
    
    int ROWS=20;
    int COLS=10;
    int TILE_SIZE=32;
    
    Color[] colors = {Color.RED,Color.GREEN,Color.BLUE,Color.MAGENTA,Color.YELLOW};
    Random random = new Random();
    JFrame window = new JFrame();
    JPanel gamePanel  = new JPanel();
    JPanel scorePanel = new JPanel();
    int score=0;
    JLabel scoreLabel = new JLabel("Score - "+score);
    
    Media media=null;
    MediaPlayer player = null;
    URL url = Clickomania.class.getClassLoader().getResource("res/glass_break.mp3");
    
    
    public Clickomania(int rows,int cols,int tileSize){
        ROWS=rows;
        COLS=cols;
        colTileCount = new int[cols];
        for(int i=0;i<COLS;i++)colTileCount[i]=ROWS;
        this.TILE_SIZE=tileSize;
        window.setLocation(500,50);
        
        PlatformImpl.startup(()->{
            media = new Media(url.toString());
            player = new MediaPlayer(media);
        });
    }
    
    
    
    public void start(){
        Listener listener = new Listener();
        window.add(gamePanel,BorderLayout.CENTER);
        window.add(scorePanel,BorderLayout.NORTH);
        window.setSize(COLS*TILE_SIZE, ROWS*TILE_SIZE);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel.setLayout(new GridLayout(ROWS,COLS));
        scorePanel.add(scoreLabel);
        
        Tile game[][] = new Tile[ROWS][COLS];
        for(int i=0;i<ROWS;i++)for(int j=0;j<COLS;j++){
            Tile t = new Tile(TILE_SIZE);
            t.setOpaque(true);
            gamePanel.add(t);
            t.setBackground(colors[random.nextInt(colors.length)]);
            
            t.setPos(i, j);
            t.setGame(game);
            t.addMouseListener(listener);
            game[i][j]=t;
        }
        window.setVisible(true);
    } 

    void tryDestroy(Tile tile,Tile[][] game,TileGroup tg){
        tryDestroy(tile, game, tg,false);
    }
    
    void tryDestroy(Tile tile,Tile[][] game,TileGroup tg,boolean debugDestroy){
        System.out.println("Trying for "+tile.col() +" "+tile.row()+" "+debugDestroy);
        boolean nborDestroyed=false;
        boolean visited[][] = new boolean[ROWS][COLS];
        visited[tile.row()][tile.col()]=true;
        Color tileColor = tile.getBackground();
        if(tile.col()>0){
            nborDestroyed = chainDestroy(game[tile.row()][tile.col()-1],game,visited,tileColor,tg);
            if(nborDestroyed)tile.destroy();
        }
        if(tile.col()<COLS-1){
            nborDestroyed= chainDestroy(game[tile.row()][tile.col()+1],game,visited,tileColor,tg);
            if(nborDestroyed)tile.destroy();
        }
        if(tile.row()>0){
            nborDestroyed= chainDestroy(game[tile.row()-1][tile.col()],game,visited,tileColor,tg);
            if(nborDestroyed)tile.destroy();
        }
        if(tile.row()<ROWS-1){
            nborDestroyed = chainDestroy(game[tile.row()+1][tile.col()],game,visited,tileColor,tg);
            if(nborDestroyed)tile.destroy();
        }
        if(debugDestroy)tile.destroy();
        if(tile.isDestroyed()){
            playSound();
            colTileCount[tile.col()]--;
            tg.add(tile.row(), tile.col());
            
            Thread tileShifter = new Thread(()->{
                while (tg.hasMoreRows()) {
                    TileGroup.Row row = tg.popNextRow();
                    while (row.hasMoreTiles()) {
                        score += 100;
                        int ri = row.getRowIndex();
                        int ti = row.nextTile();
                        for (int k = ri; k > 0; k--) {
                            game[k][ti].replicate(game[k - 1][ti]);
                        }
                        game[0][ti].destroy();
                    }
                    window.repaint(100);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Clickomania.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                updateScore();

                for (int i = COLS - 1; i >= 0; i--) {
                    if (colTileCount[i] <= 0) {
                        final int ii = i;
                        //shift cols
                        for (int j = ii; j < COLS - 1; j++) {
                            colTileCount[j] = colTileCount[j + 1];
                            for (int k = 0; k < ROWS; k++) {
                                game[k][j].replicate(game[k][j + 1]);
                            }
                        }
                        for (int k = 0; k < ROWS; k++) {
                            game[k][COLS - 1].destroy();
                        }
                        colTileCount[COLS - 1] = 0;
                        window.repaint(100);
                    }
                }
            });
            tileShifter.start();
        }
    }
    
    boolean chainDestroy(Tile tile,Tile[][] game,boolean visited[][],Color tileColor,TileGroup tg){
        if(!tile.getBackground().equals(tileColor))return false;
        tile.destroy();
        colTileCount[tile.col()]--;
        tg.add(tile.row(), tile.col());
        
        
        visited[tile.row()][tile.col()]=true;
        if(tile.col()>0 && !visited[tile.row()][tile.col()-1]){
            chainDestroy(game[tile.row()][tile.col()-1],game,visited,tileColor,tg);
        }
        if(tile.col()<COLS-1 && !visited[tile.row()][tile.col()+1]){
            chainDestroy(game[tile.row()][tile.col()+1],game,visited,tileColor,tg);
        }
        if(tile.row()>0 && !visited[tile.row()-1][tile.col()]){
            chainDestroy(game[tile.row()-1][tile.col()],game,visited,tileColor,tg);
        }
        if(tile.row()<ROWS-1 && !visited[tile.row()+1][tile.col()]){
            chainDestroy(game[tile.row()+1][tile.col()],game,visited,tileColor,tg);
        }
        return true;
    }
    
    
    void updateScore(){
        scoreLabel.setText("Score - " + score);
    }
    
    
    boolean p=false;
    void playSound(){
        player.seek(Duration.ZERO);
        if(!p)player.play();
        p=true;
    }
    
    
    
    
    
    
    class Listener extends MouseAdapter{
        @Override
        public void mousePressed(MouseEvent e) {
            Tile tile = (Tile) e.getSource();
            if(!tile.isDestroyed()){
                Tile[][] game = tile.game();
                TileGroup tg = new TileGroup(); 
                if(e.getButton()==MouseEvent.BUTTON1)tryDestroy(tile, tile.game(),tg);
                //debug 
//                else if(e.getButton()==MouseEvent.BUTTON3)tryDestroy(tile, tile.game(),tg,true);
            }
        }
    }
    
    
}
