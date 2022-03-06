package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);


        JButton exitButton = new JButton("Ex");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //panel.setVisible(false);
                System.exit(0);
            }
        });
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
