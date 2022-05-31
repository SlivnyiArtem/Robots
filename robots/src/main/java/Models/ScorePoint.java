package Models;

import gui.GameVisualizer;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class ScorePoint {
    public double X;
    public double Y;
    public ScorePoint(double x, double y){
        X=x;
        Y=y;
    }
    public void drawScorePoint(Graphics2D g){
        AffineTransform t = AffineTransform.getRotateInstance(0,0,0);
        g.setTransform(t);
        GameVisualizer.fillAndDrawOval(g, GameVisualizer.round(X), GameVisualizer.round(Y),
                Color.ORANGE, Color.BLACK, 15, 15);
    }
    public double getDistanceToPoint(double x, double y){
        return Math.sqrt((x-X)*(x-X) + (y-Y)*(y-Y));
    }
}
