package gui.windows;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gui.Dialoger;
import gui.GameVisualizer;
import gui.SizeState;
import lombok.SneakyThrows;

import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;

public class GameWindow extends JInternalFrame implements SizeState, GetLocalizeLabel {
    public final GameVisualizer m_visualizer;
    private GameVisualizer notFinalVisualizer;
    public double Hight;
    public double Width;
    public JPanel panel;

    public GameWindow() throws IOException {
        super(GetLocalizeLabel.getLocalization("localizationGameField"),
                true, true, true, true);
        BufferedReader reader;
        String stingJson;
        reader = new BufferedReader(new FileReader(
                ".\\src\\main\\java\\serialization\\GameWindowSerialization"));
        int locationX = 10;
        int locationY = 10;
        //int windowWeight = 400;
        //int windowHeight = 400;
        stingJson = reader.readLine();

        if (stingJson != null && stingJson.length() > 0){
            //this.setDefaultCloseOperation(JInternalFrame.EXIT_ON_CLOSE);
            //setMaximumSize(this.getMinimumSize());
            var recoveryDialogResult = Dialoger.confirmRecovery();
            if (recoveryDialogResult == 0){
                var jsonObject = new JSONObject(stingJson);

                var currentBorderRight = jsonObject.getDouble("CurrentBorderRight");
                var CurrentBorderDown = jsonObject.getDouble("CurrentBorderDown");
                var robotPositionX = jsonObject.getDouble("m_robotPositionX");
                var robotPositionY = jsonObject.getDouble("m_robotPositionY");
                var robotDirection = jsonObject.getDouble("m_robotDirection");
                var targetPositionX = jsonObject.getDouble("m_targetPositionX");
                var targetPositionY = jsonObject.getDouble("m_targetPositionY");
                locationX = jsonObject.getInt("LocationX");
                locationY = jsonObject.getInt("LocationY");
                Width = jsonObject.getInt("CurrentBorderRight");
                Hight = jsonObject.getInt("CurrentBorderDown");

                notFinalVisualizer = new GameVisualizer(currentBorderRight, CurrentBorderDown, robotPositionX, robotPositionY, robotDirection,
                        targetPositionX,targetPositionY);

            }
        }
        if (notFinalVisualizer == null){
            m_visualizer = new GameVisualizer();
        }
        else{
            m_visualizer = notFinalVisualizer;
        }
        this.setLocation(locationX,locationY);
        //this.setSize((int)Width, Hight);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                Hight = evt.getComponent().getSize().getHeight();
                Width = evt.getComponent().getSize().getWidth();
                update(Hight, Width);
            }
        });

    }

    @SneakyThrows
    @Override
    public void doDefaultCloseAction() {
        var confirmResult = Dialoger.confirmRecovery();
        if (confirmResult == 0) {
            //var confir
            // mResult = Dialoger.onExit();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            FileWriter writer = new FileWriter(".\\src\\main\\java\\serialization\\GameWindowSerialization");
        /*
        if(this.isClosed){
            writer.write("isClosed");
            writer.close();
        }
        else if(this.isMaximum){
            writer.write("isMaximum");
            writer.close();
        }
        else if (this.isIcon()) {
            writer.write("isIcon");
            writer.close();
        }
        else{

        }

         */
            var jsonObj = new JSONObject();
            putObjectsInJSONObject(jsonObj,m_visualizer);
            writer.write(jsonObj.toString());
            writer.close();

        }
        super.doDefaultCloseAction();



    }

    @Override
    public void update(double w, double Height){
        m_visualizer.setWH(Width, Hight);
    }
    public void putObjectsInJSONObject(JSONObject jsonObj, GameVisualizer m_visualizer){
        jsonObj.put("m_robotPositionX", m_visualizer.m_robotPositionX);
        jsonObj.put("m_robotPositionY", m_visualizer.m_robotPositionY);
        jsonObj.put("m_robotDirection", m_visualizer.m_robotDirection);
        jsonObj.put("m_targetPositionX",m_visualizer.m_targetPositionX);
        jsonObj.put("m_targetPositionY",m_visualizer.m_targetPositionY);
        jsonObj.put("CurrentBorderRight", Width);
        jsonObj.put("CurrentBorderDown", Hight);
        jsonObj.put("LocationX", this.getLocation().x);
        jsonObj.put("LocationY", this.getLocation().y);
    }
}