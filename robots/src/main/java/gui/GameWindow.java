package gui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class GameWindow extends JInternalFrame implements SizeState{
    private final GameVisualizer m_visualizer;
    public double Hight;
    public double Weight;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        /*
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        panel.setLayout(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);

         */
        pack();
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                Hight = evt.getComponent().getSize().getHeight();
                Weight = evt.getComponent().getSize().getWidth();
                update(Hight, Weight);
            }
        });
    }

    @Override public void doDefaultCloseAction() {
        var confirmResult = Exiter.onExit();
        if (confirmResult == 0)
            super.doDefaultCloseAction();
    }


    @Override
    public void update(double w, double h) {
        m_visualizer.setWH(Weight, Hight);
    }
}
