package com.example.demo;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.model.Config;
import org.example.model.GameMessege;
import org.example.model.PlayerData;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class HelloApplication extends Application
{
    private static final String HERO_IMAGE_LOC = "U:/Пронина Татьяна/Documents/hero_image.png";
    private static final String HERO_WEAPON_LOC = "U:/Пронина Татьяна/Documents/weapon_image.png";
    private static final String TARGET_IMAGE_LOC = "U:/Пронина Татьяна/Documents/target_image.png";
    private Image heroImage= new Image(HERO_IMAGE_LOC);
    private Image weaponImage = new Image(HERO_WEAPON_LOC,50,50,true,false);
    private Image targetImage = new Image(TARGET_IMAGE_LOC,20,20,true,false);
    public final int SIZEX = 600;
    public final int SIZEY = 600;

    private String resultName="Anonim";
    private void showAlertWithHeaderText() {
        TextInputDialog dialogName = new TextInputDialog();
        dialogName.setTitle("Connection");
        dialogName.setHeaderText("Enter your name");
        dialogName.setContentText("Name:");
        Optional<String> result = dialogName.showAndWait();
        if(!result.get().isEmpty())
            result.ifPresent(name -> {
                resultName=name;
            });
        System.out.println(resultName);
    }

    private void ShowHighScoreAlert()
    {
        Alert alert = new Alert(Alert.AlertType.NONE, "Score", ButtonType.CLOSE);
        alert.setTitle("High Scores");
        alert.setHeaderText("Results:");
        String scores = "";
        List<PlayerData> scoresData;
        try
        {
            Socket scoreSocket = new Socket(new Config().ip, new Config().PORT+10);
            ObjectInputStream objectInputStream = new ObjectInputStream(scoreSocket.getInputStream());
            scoresData = (List<PlayerData>) objectInputStream.readObject();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        if (scoresData == null)
        {
            return;
        }
        for (int i =0; i<scoresData.size(); i++)
        {
            scores+="name: "+scoresData.get(i).name;
            scores+=" shoots: "+scoresData.get(i).hits;
            scores+=" hits: "+scoresData.get(i).scoreRes;
            scores+="\n";
        }
        alert.setContentText(scores);
        alert.showAndWait();
    }
    private Group board;
    @Override
    public void start(Stage stage) throws IOException
    {
        VBox vBox = new VBox();
        Menu menu = new Menu("get");
        MenuBar menuBar = new MenuBar();
        MenuItem menuView = new MenuItem("Get rate");
        BorderPane root = new BorderPane();
        menu.getItems().add(menuView);
        menuBar.getMenus().add(menu);
        root.setTop(menuBar);
        menuView.setOnAction(e -> {
            ShowHighScoreAlert();
        });
        showAlertWithHeaderText();
        Socket socket;
        try
        {
            socket = new Socket(new Config().ip, new Config().PORT);
            new ObjectOutputStream(socket.getOutputStream()).writeObject(resultName);
        }
        catch (Exception exception)
        {
            return;
        }
        board = new Group();
        vBox.getChildren().addAll(root,board);
        vBox.setStyle("-fx-background-color: yellowgreen;");
        Scene scene = new Scene(vBox, SIZEX, SIZEY);
        stage.setTitle("JavaNet SHOOTER Client");
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode())
                {
                    case SPACE:
                        try
                        {
                            socket.getOutputStream().write(1);
                        }
                        catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                        break;
                    case H:
                        try
                        {
                            ShowHighScoreAlert();
                        }
                        catch (Exception exception)
                        {
                            throw new RuntimeException(exception);
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
                try
                {
                    board.getChildren().clear();
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    GameMessege gameMessege = (GameMessege) objectInputStream.readObject();
                    for (int i = 0; i< gameMessege.objects.size(); i++)
                    {
                        ImageView temp;
                        if (gameMessege.objects.get(i).IsTarget())
                        {
                            temp = new ImageView(targetImage);
                        }
                        else if (gameMessege.objects.get(i).IsPlayer())
                        {
                            temp = new ImageView(weaponImage);
                        }
                        else
                        {
                            continue;
                        }
                        temp.relocate(gameMessege.objects.get(i).x, gameMessege.objects.get(i).y);
                        board.getChildren().add(temp);
                        if (gameMessege.objects.get(i).IsPlayer())
                        {
                            ImageView player = new ImageView(heroImage);
                            player.relocate(-20,gameMessege.objects.get(i).y);
                            board.getChildren().add(player);
                        }
                    }
                    for (int i =0; i< gameMessege.playerData.size(); i++)
                    {
                        Label temp = new Label("Name: " + gameMessege.playerData.get(i).name + " Shoots: " + gameMessege.playerData.get(i).hits + " Score: " + gameMessege.playerData.get(i).scoreRes);
                        temp.relocate(10, i*20);
                        board.getChildren().add(temp);
                    }
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                catch (ClassNotFoundException e)
                {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.start();
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void stop() throws Exception
    {
        super.stop();
    }
    public static void main(String[] args)
    {
        launch();
    }
}