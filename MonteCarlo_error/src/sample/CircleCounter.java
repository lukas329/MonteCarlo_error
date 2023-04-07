package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.Random;

public class CircleCounter extends Application {
    private static final int CANVAS_WIDTH = 1400;
    private static final int CANVAS_HEIGHT = 700;
    DataGenerator dataGenerator;
    Painter painter;
    Checker checker;
    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Label lbl = new Label("POCET");
        lbl.setVisible(true);
        Group root = new Group(canvas);
        Button btnStop = new Button();
        Button btnResume = new Button();
        Button btnRestart = new Button();

        //Vykreslíme kruh a štvorec
        drawBoundaries(gc);

        //vlákno, ktoré generuje dáta
        dataGenerator = getDataGenerator();
        dataGenerator.start();

        //vlákno na kontrolu, či sa guličky nachádzajú v kruhu alebo štvorci
        checker = getChecker();
        checker.start();

        //vlákno, ktoré kreslí, guličky
        painter = getPainter(gc);
        painter.setDaemon(true);
        painter.start();

        primaryStage.setOnCloseRequest(windowEvent -> {
            dataGenerator.killThread();
            checker.killThread();
            painter.killThread();
        });

        //vypísanie pomeru guliečiek každú sekundu
        Timeline tm = new Timeline(new KeyFrame(Duration.millis(1000), e ->{lbl.setText("Počet guličiek v kruhu / počet guličiek vo štvorci: "
                + (float)checker.getOvalCount()/checker.getRectCount() + "  ----  Celkový počet guličiek: " + checker.getIterator());}));
        tm.setCycleCount(Animation.INDEFINITE);
        tm.play();

        //tlačidlo na zastavenie generovania dáts
        btnStop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //preruší vlákno
                dataGenerator.interrupt();
                System.out.println("dg: " + dataGenerator.getState() + "\n Painter: " + painter.getState() + "\n cr: " + checker.getState());
            }
        });

        //tlačidlo na pustenie generovania dát
        btnResume.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //Spúšťa generovanie dát
                dataGenerator.startAgain();
                System.out.println("dg: " + dataGenerator.getState() + "\n Painter: " + painter.getState() + "\n cr: " + checker.getState());
            }
        });

        btnRestart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                dataGenerator.startAgain();
                painter = getPainter(gc);
                painter.setDaemon(true);
                painter.start();
                checker = getChecker();
                checker.start();
            }
        });

        setButtons(btnResume, btnStop,btnRestart, root);
        root.getChildren().add(lbl);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void drawBoundaries(GraphicsContext gc){
        gc.setStroke(Color.RED);
        gc.strokeRect(220, 200, 300, 300);
        gc.strokeOval(630, 50, 600, 600);
    }

    private void setButtons(Button btnResume, Button btnStop, Button btnRestart, Group root){
        btnResume.setLayoutY(20);
        btnResume.setLayoutX(100);
        btnResume.setText("R E S U M E");
        btnResume.setStyle("-fx-background-color: #06d57b; ");

        btnStop.setLayoutY(20);
        btnStop.setLayoutX(20);
        btnStop.setText("  S T O P  ");
        btnStop.setStyle("-fx-background-color: #990000; ");

        btnRestart.setLayoutY(20);
        btnRestart.setLayoutX(190);
        btnRestart.setText("R E S T A R T");
        btnRestart.setStyle("-fx-background-color: #009994; ");

        root.getChildren().addAll(btnStop, btnResume, btnRestart);
    }

    private DataGenerator getDataGenerator(){
        return new DataGenerator();
    }
    private Checker getChecker(){
        return new Checker(dataGenerator);
    }
    private Painter getPainter(GraphicsContext gc){
        return new Painter(gc, dataGenerator);
    }
}
