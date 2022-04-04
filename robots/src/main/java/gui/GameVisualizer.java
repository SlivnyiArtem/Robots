package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.io.Console;
import java.util.*;
import java.util.Timer;

import javax.swing.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class GameVisualizer extends JPanel {
    //volatile необходим для последующей реализации многопоточности
    public volatile double m_robotPositionX = 150;
    public volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    private final Timer m_timer = initTimer();
    private volatile double CurrentBorderRight;

    private volatile double CurrentBorderDown;

    public GameVisualizer() {
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }
    protected void setTargetPosition(Point p) { // просто обновление переменных цели
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }
    // Вызывает на следующем тике потока событий
    //this::repaint     - вызов метода this.repaint для элемента который был помещен в очередь обработки собтий???

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }
    // расчитываем расстояние между двумя точками

    private static double angleToRadians(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        //находим расстояние которое потом становится угловыми кординатами
        double polarCoordinates = Math.atan2(diffY, diffX); // преобразовали в полярные координаты
        return asNormalizedRadians(polarCoordinates);
    }
    //

    protected void onModelUpdateEvent() { // происходит на каждом обновлении состояния приложения
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5) { // мы достигли цели
            return;
        }

        double velocity = maxVelocity;
        double angleToTarget = angleToRadians(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        if (angleToTarget > m_robotDirection) {
            angularVelocity = maxAngularVelocity;
        }
        if (angleToTarget < m_robotDirection) {
            angularVelocity = -maxAngularVelocity;
        }
        //обновляем угловую скорость (векторная величина, характеризующая быстроту и
        //направление вращения материальной точки или
        // абсолютно твёрдого тела относительно центра вращения.)

        tryStartOutTheDifferentBorder();

        moveRobot(velocity, angularVelocity, 10);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        return Math.min(value, max);
    }


    // Выход с другого края доски
    private void tryStartOutTheDifferentBorder() {
        if (m_robotPositionX > CurrentBorderRight ||m_robotPositionX < 0) {
            m_robotPositionX = CurrentBorderRight - m_robotPositionX;
        }
        if (m_robotPositionY > CurrentBorderDown ||m_robotPositionY < 0) {
            m_robotPositionY = CurrentBorderDown - m_robotPositionY;
        }
    }


    private void moveRobot(double velocity, double angularVelocity, double movementDuration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection + angularVelocity * movementDuration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX)) {
            newX = m_robotPositionX + velocity * movementDuration * Math.cos(m_robotDirection);
        }

        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection + angularVelocity * movementDuration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY)) {
            newY = m_robotPositionY + velocity * movementDuration * Math.sin(m_robotDirection);
        }

        m_robotPositionX = newX;
        m_robotPositionY = newY;

        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * movementDuration);
        m_robotDirection = newDirection;
    }

    private static double asNormalizedRadians(double angle) {
        //у нас есть некий угол в радианах, нам нужно привести его в диапазон от 0 до 2 PI
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static int round(double value) {
        return (int) (value + 0.5); //привидение к int с округлением вверх
    }

    @Override
    public void paint(Graphics g) {
        // отрисовка робота и цели(вроде только этого?)
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void fillAndDrawOval(Graphics2D g, int x, int y, Color fillColor,
                                 Color drawColor, int diam1, int diam2) {
        g.setColor(fillColor);
        fillOval(g, x, y, diam1, diam2);
        g.setColor(drawColor);
        drawOval(g, x, y, diam1, diam2);
    }

    /**
     * три метода выше отвечают за отрисовку и "заливку" овалов
     */

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        var robotCenterX = round(m_robotPositionX);
        var robotCenterY = round(m_robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        //отображение плоскости на саму себя, афинное преобразование
        g.setTransform(t);
        fillAndDrawOval(g, robotCenterX, robotCenterY,
                Color.MAGENTA, Color.BLACK, 30, 10);
        fillAndDrawOval(g, robotCenterX + 10, robotCenterY,
                Color.WHITE, Color.BLACK, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        fillAndDrawOval(g, x, y, Color.GREEN, Color.black, 5, 5);
    }


    //GameWindow a = new GameWindow();
    public void setWH(double w, double h){
        this.CurrentBorderDown = h;
        this.CurrentBorderRight = w;
        }
    }

