package com.example.shooting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.io.IOException;

import javafx.animation.Timeline;

public class HelloApplication extends Application {

    private static final double W = 600, H = 400;
    private static final String HERO_IMAGE_LOC = "U:/Пронина Татьяна/Documents/hero_image.png";
    private static final String HERO_WEAPON_LOC = "U:/Пронина Татьяна/Documents/weapon_image.png";
    private static final String TARGET_IMAGE_LOC = "U:/Пронина Татьяна/Documents/target_image.png";
    private static final String SUPER_TARGET_IMAGE_LOC = "U:/Пронина Татьяна/Documents/super_target_image.png";
    Text gameStop = new Text(W/2-50,H/2,"Game paused");
    private Timeline timeline = new Timeline();
    private Image heroImage, weaponImage, targetImage;
    private Node hero;
    private ArrayList<Node> weapons = new ArrayList<Node>();
    private ArrayList<Node> targets = new ArrayList<Node>();
    private Group board;
    Text scoreText, shootText;
    private int dTarget, dWeapon = 10, shootCount = 0, score = 0;
    boolean goNorth, goSouth, throwing;

    private boolean running = true, isUp=false;

    @Override
    public void start(Stage stage) throws IOException {
        heroImage = new Image(HERO_IMAGE_LOC);
        hero = new ImageView(heroImage);

        weaponImage = new Image(HERO_WEAPON_LOC);

        board = new Group();
        scoreText = new Text(110, 10, "Score: " + score);
        shootText = new Text(170, 10, "Shoots: " + shootCount);
        board.getChildren().addAll(hero, scoreText, shootText);
        moveHeroTo(0, H / 2);
        Scene scene = new Scene(board, W, H, Color.GREENYELLOW);
        createTarget();
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case W:
                        goNorth = true;
                        break;
                    case S:
                        goSouth = true;
                        break;
                    case SPACE:
                        if (!throwing) {
                            shootCount++;
                            shootText.setText("Shoots: "+shootCount);
                            ImageView aWeapon = new ImageView(weaponImage);
                            Node newWeapon = aWeapon;
                            newWeapon.relocate(hero.getLayoutX() + hero.getBoundsInLocal().getWidth(), hero.getLayoutY());
                            weapons.add(newWeapon);
                            board.getChildren().add(newWeapon);
                            throwing = true;
                        }
                        break;
                    case ESCAPE:
                        stopGame();
                        break;
                    case TAB:
                        startGame();
                        break;

                }
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case W:
                        goNorth = false;
                        break;
                    case S:
                        goSouth = false;
                        break;
                    case SPACE:
                        throwing = false;
                        break;
                }
            }
        });
        stage.setTitle("Shooter Game");
        stage.setScene(scene);
        stage.show();
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long now) {
                if (!running)
                    return;
                int dy = 0;
                if (goNorth) dy -= 3;
                if (goSouth) dy += 3;
                moveHeroTo(hero.getLayoutX(),hero.getLayoutY()+dy);
                throwWeapon(dWeapon);
                moveTarget();
                checkHit();
            }
        };
        timer.start();
    }

    private void moveHeroTo(double x,double y){
        if(y>=0 && y<=H-hero.getBoundsInLocal().getHeight()) hero.relocate(x,y);
    }

    private void moveTarget() {
        for (int i = 0; i < targets.size(); i++) {
            if (targets.get(i).getLayoutY() == 0) {
            isUp = false;
        } else if (targets.get(i).getLayoutY() == H) isUp = true;
            targets.get(i).relocate(targets.get(i).getLayoutX(), targets.get(i).getLayoutY() + (isUp ?-dTarget : dTarget));
        }
    }

    void createTarget() {
        String loc;
        if (Math.random() < 0.5) {
            loc = TARGET_IMAGE_LOC;
            dTarget = 8;
        } else {
            loc = SUPER_TARGET_IMAGE_LOC;
            dTarget = 4;}
            targetImage = new Image(loc);
            Node newTarget = new ImageView(targetImage);
            newTarget.relocate(W / 2, H / 2);
            targets.add(newTarget);
            board.getChildren().add(newTarget);

    }

    private void throwWeapon(int del){
        for(int i=0;i<weapons.size();i++)
            if(weapons.get(i).getLayoutX()<W)
                weapons.get(i).relocate(weapons.get(i).getLayoutX()+del,weapons.get(i).getLayoutY());
            else weapons.remove(i);
    }

    private void checkHit(){
        for(int i=0;i<weapons.size();i++)
            for(int j=0;j<targets.size();j++)
                if(weapons.get(i).getBoundsInParent().intersects(targets.get(j).getBoundsInParent())){
                        board.getChildren().remove(targets.get(j));
                        targets.remove(j);
                        score++;
                        board.getChildren().remove(weapons.get(i));
                        weapons.remove(i);
                        createTarget();
                        scoreText.setText("Score: "+score);
                }
    }

    void startGame(){
        board.getChildren().remove(gameStop);
        timeline.play();
        running=true;
    }

    void stopGame(){
        running=false;
        gameStop.setFill(Color.GREEN);
        gameStop.setFont(Font.font("Verdana",20));
        board.getChildren().add(gameStop);
        timeline.stop();
    }
    public static void main(String[] args) {
        launch();
    }
}