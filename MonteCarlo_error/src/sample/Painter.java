package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Painter extends Thread{

    private final GraphicsContext gc;
    private final DataGenerator dg;
    double radius;
    int countAll;
    private boolean isAlive;
    public Painter(GraphicsContext gc, DataGenerator dg) {
        this.gc = gc;
        radius = 5;
        this.dg = dg;
        countAll = 0;
        isAlive = true;
        System.out.println("Nová inštancia triedy Painter bola inicializovaná!");
    }

    @Override
    public void run() {
        gc.setFill(Color.BLACK);
        while (isAlive) {
            //Zaistíme, že toto vlákno vlastní monitor pomocou kľúčového slova keyword
            synchronized (this) {

                gc.fillOval(dg.getListX().get(countAll), dg.getListY().get(countAll), radius, radius);
                countAll++;
                System.out.println("Vykreslujem");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void killThread(){
        isAlive = false;
    }
}
