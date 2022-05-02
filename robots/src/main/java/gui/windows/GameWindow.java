package gui.windows;

import gui.Exiter;
import gui.GameVisualizer;
import gui.SizeState;
import localization.Localization;

import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.border.BevelBorder;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Observable;
import java.util.Observer;

public class GameWindow extends JInternalFrame implements SizeState, GetLocalizeLabel {
    private final GameVisualizer m_visualizer;
    public double Hight;
    public double Weight;

    public GameWindow() {
        super(GetLocalizeLabel.getLocalization("localizationGameField"),
                true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                Hight = evt.getComponent().getSize().getHeight();
                Weight = evt.getComponent().getSize().getWidth();
                update(Hight, Weight);
            }
        });
    }

    @Override
    public void doDefaultCloseAction() {
        var confirmResult = Exiter.onExit();
        if (confirmResult == 0)
            super.doDefaultCloseAction();
    }


    @Override
    public void update(double w, double Height) {
        m_visualizer.setWH(Weight, Hight);
    }
}