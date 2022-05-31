package gui;

import Models.ScorePoint;
import log.Logger;
import lombok.Synchronized;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.swing.*;

public class GameVisualizer extends JPanel {
    //volatile необходим для последующей реализации многопоточности
    public volatile double m_robotPositionX = 150;
    public volatile double m_robotPositionY = 100;
    public volatile double m_robotDirection = 0;


    public volatile double m_automatonX = 150;
    public volatile double m_automatonY = 100;
    public volatile double m_automatonDirection = 0;


    public volatile int m_targetPositionX = 150;
    public volatile int m_targetPositionY = 100;

    public static final double maxVelocity = 0.1;
    public static final double maxAngularVelocity = 0.001;

    public final Timer m_timer = initTimer();
    public volatile double CurrentBorderRight;


    private final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    private final ArrayList<ScorePoint> scorePointsList = new ArrayList<>();

    private static volatile int robotScore = 0;
    private static volatile  int automatonScore = 0;


    public volatile double CurrentBorderDown;

    public GameVisualizer() {
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                var rand = new Random();
                scorePointsList.add(new ScorePoint(rand.nextDouble() * CurrentBorderDown,
                        rand.nextDouble() * CurrentBorderRight));
            }
        }, 0, 5000);
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

    public GameVisualizer(double currentBorderRight, double currentBorderDown, double robotPositionX, double robotPositionY,
                          double robotDirection, double targetPositionX, double targetPositionY) {
        CurrentBorderDown = currentBorderRight;
        CurrentBorderDown = currentBorderDown;
        m_robotPositionX = robotPositionX;
        m_robotPositionY = robotPositionY;
        m_robotDirection = robotDirection;
        m_targetPositionX = round(targetPositionX);
        m_targetPositionY = round(targetPositionY);


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

    public static Timer initTimer() {
        return new Timer("events generator", true);
    }

    public void setTargetPosition(Point p) { // просто обновление переменных цели
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
    }

    public void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }
    // Вызывает на следующем тике потока событий

    public static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }
    // расчитываем расстояние между двумя точками

    public static double angleToRadians(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        //находим расстояние которое потом становится угловыми кординатами
        double polarCoordinates = Math.atan2(diffY, diffX); // преобразовали в полярные координаты
        return asNormalizedRadians(polarCoordinates);
    }

    private void updateRobot() {

        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5) { // мы достигли цели
            return;
        }

        double angleToTarget = angleToRadians(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        if (angleToTarget > m_robotDirection) {
            angularVelocity = maxAngularVelocity;
        }
        if (angleToTarget < m_robotDirection) {
            angularVelocity = -maxAngularVelocity;
        }

        tryStartOutTheDifferentBorder();

        moveRobot(maxVelocity, angularVelocity, 10);
    }

    private void updateScorePoints() {
        for (var scorePoint : scorePointsList) {
            if (scorePoint.getDistanceToPoint(m_robotPositionX, m_robotPositionY) < 5) {
                synchronized (GameVisualizer.class) {
                    robotScore += 1;
                }
                Logger.debug("Player score: " + robotScore);
                scorePointsList.remove(scorePoint);
            }
            if (scorePoint.getDistanceToPoint(m_automatonX, m_automatonY) < 5) {
                synchronized (GameVisualizer.class) {
                    automatonScore += 1;
                }
                Logger.debug("Computer score: " + automatonScore);
                scorePointsList.remove(scorePoint);
            }
        }
    }

    private void updateAutomaton() {
        var velocity = maxVelocity;
        var movementDuration = 10;
        var nearestScorePoint = getNearestScorePoint(m_automatonX, m_automatonY, scorePointsList);
        double angleToTarget = angleToRadians(m_automatonX, m_automatonY, nearestScorePoint.X, nearestScorePoint.Y);
        double angularVelocity = 0;
        if (angleToTarget > m_automatonDirection)
            angularVelocity = maxAngularVelocity;
        if (angleToTarget < m_automatonDirection)
            angularVelocity = -maxAngularVelocity;
        tryStartOutTheDifferentBorderForAutomaton();
        velocity = applyLimits(velocity, 0, maxVelocity);

        angularVelocity = applyLimits(angularVelocity, -maxVelocity, maxAngularVelocity);
        double newX = m_automatonX + velocity / angularVelocity * (Math.sin(m_automatonDirection + angularVelocity * movementDuration) - Math.sin(m_automatonDirection));
        if (!Double.isFinite(newX))
            newX = m_automatonX + velocity * movementDuration * Math.cos(m_automatonDirection);
        double newY = m_automatonY - velocity / angularVelocity * (Math.cos(m_automatonDirection + angularVelocity * movementDuration) - Math.cos(m_automatonDirection));
        if (!Double.isFinite(newY))
            newY = m_automatonY + velocity * movementDuration * Math.sin(m_automatonDirection);
        m_automatonX = newX;
        m_automatonY = newY;
        synchronized (GameVisualizer.class) {
            m_automatonDirection = asNormalizedRadians(m_automatonDirection + angularVelocity * movementDuration);
        }
    }

    private ScorePoint getNearestScorePoint(double m_automatonX, double m_automatonY, ArrayList<ScorePoint> scorePointsList) {
        ScorePoint nearestScorePoint = new ScorePoint(m_automatonX, m_automatonY);
        var minimalDistance = Double.MAX_VALUE;
        for (var currentScorePoint : scorePointsList) {
            var currentDistance = currentScorePoint.getDistanceToPoint(m_automatonX, m_automatonY);
            if (currentDistance < minimalDistance) {
                nearestScorePoint = currentScorePoint;
                minimalDistance = currentDistance;
            }
        }
        return nearestScorePoint;
    }


    public void onModelUpdateEvent() { // происходит на каждом обновлении состояния приложения
        threadPoolExecutor.submit(this::updateRobot);
        threadPoolExecutor.submit(this::updateScorePoints);
        threadPoolExecutor.submit(this::updateAutomaton);
    }

    public static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        return Math.min(value, max);
    }


    // Выход с другого края доски
    public void tryStartOutTheDifferentBorder() {
        synchronized (this) {
            if (m_robotPositionX > CurrentBorderRight || m_robotPositionX < 0) {
                m_robotPositionX = CurrentBorderRight - m_robotPositionX;
            }
            if (m_robotPositionY > CurrentBorderDown || m_robotPositionY < 0) {
                m_robotPositionY = CurrentBorderDown - m_robotPositionY;
            }
        }
    }

    public void tryStartOutTheDifferentBorderForAutomaton() {
        synchronized (this) {
            if (m_automatonX > CurrentBorderRight || m_automatonX < 0) {
                m_automatonX = CurrentBorderRight - m_automatonX;
            }
            if (m_automatonY > CurrentBorderDown || m_automatonY < 0) {
                m_automatonY = CurrentBorderDown - m_automatonY;
            }
        }
    }


    public void moveRobot(double velocity, double angularVelocity, double movementDuration) {
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

        synchronized (GameVisualizer.class) {
            m_robotDirection = asNormalizedRadians(m_robotDirection + angularVelocity * movementDuration);
        }
    }

    public static double asNormalizedRadians(double angle) {
        //у нас есть некий угол в радианах, нам нужно привести его в диапазон от 0 до 2 PI
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    public static int round(double value) {
        return (int) (value + 0.5); //привидение к int с округлением вверх
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
        for (var scorePoint : scorePointsList) {
            scorePoint.drawScorePoint(g2d);
        }
        drawAutomaton(g2d, m_automatonX, m_automatonY, m_automatonDirection);
    }

    public static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    public static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    public static void fillAndDrawOval(Graphics2D g, int x, int y, Color fillColor,
                                       Color drawColor, int diam1, int diam2) {
        g.setColor(fillColor);
        fillOval(g, x, y, diam1, diam2);
        g.setColor(drawColor);
        drawOval(g, x, y, diam1, diam2);
    }

    /**
     * три метода выше отвечают за отрисовку и "заливку" овалов
     */

    public void drawAutomaton(Graphics2D g, double automatonX, double automatonY, double automatonDirection) {
        var robotCenterX = round(automatonX);
        var robotCenterY = round(automatonY);
        AffineTransform t = AffineTransform.getRotateInstance(automatonDirection, robotCenterX, robotCenterY);
        //отображение плоскости на саму себя, афинное преобразование
        g.setTransform(t);
        fillAndDrawOval(g, robotCenterX, robotCenterY,
                Color.GREEN, Color.BLACK, 30, 10);
        fillAndDrawOval(g, robotCenterX + 10, robotCenterY,
                Color.WHITE, Color.BLACK, 5, 5);
    }

    public void drawRobot(Graphics2D g, double robotPositionX, double robotPositionY, double robotDirection) {
        var robotCenterX = round(robotPositionX);
        var robotCenterY = round(robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(robotDirection, robotCenterX, robotCenterY);
        //отображение плоскости на саму себя, афинное преобразование
        g.setTransform(t);
        fillAndDrawOval(g, robotCenterX, robotCenterY,
                Color.MAGENTA, Color.BLACK, 30, 10);
        fillAndDrawOval(g, robotCenterX + 10, robotCenterY,
                Color.WHITE, Color.BLACK, 5, 5);
    }

    public void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        fillAndDrawOval(g, x, y, Color.GREEN, Color.black, 5, 5);
    }

    public void setWH(double w, double h) {
        this.CurrentBorderDown = h;
        this.CurrentBorderRight = w;
    }
}