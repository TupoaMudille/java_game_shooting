package com.example.shooting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application
{
    private static final double W = 600, H = 400;
    private static final String HERO_IMAGE_LOC = "U:/Пронина Татьяна/Documents/hero_image.png";
    private static final String HERO_WEAPON_LOC = "U:/Пронина Татьяна/Documents/weapon_image.png";
    private static final String TARGET_IMAGE_LOC = "U:/Пронина Татьяна/Documents/target_image.png";
    private static final String SUPER_TARGET_IMAGE_LOC = "U:/Пронина Татьяна/Documents/super_target_image.png";
    private Group board;
    Text scoreText, shootText;
    private int shootCount = 0, score = 0;
    private Image heroImage=new Image(HERO_IMAGE_LOC), weaponImage=new Image(HERO_WEAPON_LOC), targetImage1 = new Image(TARGET_IMAGE_LOC),targetImage2 = new Image(SUPER_TARGET_IMAGE_LOC);
    private ImageView hero = new ImageView(heroImage), target1=new ImageView(targetImage1),target2=new ImageView(targetImage2), aWeapon = new ImageView(weaponImage);
    private TargetThread targetThread1 = new TargetThread();
    private TargetThread targetThread2 = new TargetThread();
    boolean isD1=false,isD2=false;


    private void TargetInit()
    {
        targetThread1.SetTarget(target1, 500, 200, 10);
        targetThread2.SetTarget(target2, 300, 200, 20);
    }

    private void CheckHit()
    {
        if (aWeapon.getBoundsInParent().intersects(target1.getBoundsInParent())&&!isD1)
        {
            score+=2;
            isD1=true;
        }
        if (aWeapon.getBoundsInParent().intersects(target2.getBoundsInParent())&&!isD2)
        {
            score++;
            isD2=true;
        }
        scoreText.setText("Score: " + score);
    }

    @Override
    public void start(Stage stage) throws IOException {
            scoreText = new Text(110, 10, "Score: " + score);
            shootText = new Text(170, 10, "Shoots: " + shootCount);

            board = new Group();

            board.getChildren().addAll(hero, target1, target2, scoreText, shootText, aWeapon);
            Scene scene = new Scene(board, W, H, Color.YELLOWGREEN);
            hero.relocate(0, H / 2);
            aWeapon.relocate(10, H / 2);
            stage.setTitle("Shooter Game");
            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    switch (keyEvent.getCode()) {
                        case SPACE:
                            shootCount++;
                            shootText.setText("Shoots: " + shootCount);
                            PlayerThread playerThread = new PlayerThread();
                            playerThread.SetPlayer(aWeapon);
                            if (!playerThread.isAlive()) {
                                playerThread.start();
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    CheckHit();
                }
            };

            timer.start();
            stage.setScene(scene);
            TargetInit();
            targetThread1.start();
            targetThread2.start();
            stage.show();
        }
        
    @Override
    public void stop() throws Exception
    {
        targetThread2.stop();
        targetThread1.stop();
        super.stop();
    }
    public static void main(String[] args)
    {
        launch();
    }

    private class MovTarget implements Runnable
    {
        private ImageView target;
        private double newY;
        private double newX;
        public MovTarget(ImageView _target, double _newX, double _newY)
        {
            this.target = _target;
            this.newY=_newY;
            this.newX = _newX;
        }
        @Override
        public void run()
        {
            target.relocate(this.newX, this.newY);
        }
    }

    private class PlayerThread extends Thread
    {
        private ImageView weapon;
        private double weaponW;
        private double weaponH;
        public void SetPlayer(ImageView _weapon)
        {

            this.weapon=_weapon;
            this.weaponW=this.weapon.getLayoutX();
            this.weaponH=this.weapon.getLayoutY();
        }

        @Override
        public void run()
        {
            double pos = this.weaponW;
            while (true)
            {
                pos++;
                Platform.runLater(new MovTarget(this.weapon, pos, this.weaponH));
                try
                {
                    Thread.sleep(1);
                }
                catch (Exception exception)
                {
                    return;
                }
                if(pos>=W)
                {
                    Platform.runLater(new MovTarget(this.weapon, 10, H/2));
                    isD1=false;
                    isD2=false;
                    return;
                }
            }
        }
    }

    private class TargetThread extends Thread
    {
        private ImageView target;
        private int speed;
        private double startH;
        private double startW;
        public void SetTarget(ImageView _target, double _w, double _h, int _speed)
        {
            this.target=_target;
            this.target.relocate(_w, _h);
            this.speed=_speed;
            this.startH=_h;
            this.startW=_w;
        }
        @Override
        public void run()
        {
            double pos = startH;
            int vec = 1;
            while (true)
            {
                try
                {
                    Thread.sleep(speed);
                    Thread.yield();
                }
                catch (Exception exception)
                {
                    return;
                }
                pos+=vec;
                Platform.runLater(new MovTarget(this.target, startW, pos));
                if(pos>H || pos<0)
                {
                    vec*=(-1);
                }
            }
        }
    }
}
