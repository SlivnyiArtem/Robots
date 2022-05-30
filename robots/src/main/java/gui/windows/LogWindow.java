package gui.windows;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.io.*;
import java.util.LinkedList;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gui.Dialoger;
import log.*;
import org.json.JSONObject;
import lombok.SneakyThrows;

public class LogWindow extends JInternalFrame implements LogChangeListener, GetLocalizeLabel {
    private LogWindowSource notFinalLogSource;
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;


    public LogWindow(LogWindowSource logSource) throws IOException {
        super(GetLocalizeLabel.getLocalization("protocolLabel"),
                true, true, true, true);
        BufferedReader reader;
        //String json;
        reader = new BufferedReader(new FileReader(
                ".\\src\\main\\java\\serialization\\LogWindowSerialization"));
        var json = reader.readLine();
        var jsonSize = reader.readLine();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        if (json != null && json.length()>0){
            var recoveryDialogResult = Dialoger.confirmRecovery();
            if (recoveryDialogResult == 0){
                var jsonArr = gson.fromJson(json, LogWindowSource.class).m_messages;
                var logEntryList = new LinkedList<LogEntry>();
                for(int i = 0; i< jsonArr.size(); i++){
                    var jObj = jsonArr.get(i);
                    var log = new LogEntry(LogLevel.valueOf(jObj.getLevel().toString()),
                            jObj.getMessage());
                    logEntryList.add(log);
                    notFinalLogSource = Logger.getDefaultLogSource();
                    for (LogEntry logEntry:logEntryList) {
                        notFinalLogSource.append(logEntry.getLevel(), logEntry.getMessage());
                    }
                }
            }
        }
        if (notFinalLogSource == null) {
            m_logSource = logSource;
        }
        else{
            m_logSource = notFinalLogSource;
        }
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        int width = 300;
        int height = 800;
        int locationX = 800;
        int locationY = 10;
        if (jsonSize != null && jsonSize.length() > 0) {
            var recoveryDialogResult = Dialoger.confirmRecovery();
            if (recoveryDialogResult == 0) {
                var jsonObject = new JSONObject(jsonSize);
                height = jsonObject.getInt("WindowHeight");
                width = jsonObject.getInt("WindowWidth");
                locationX = jsonObject.getInt("LocationX");
                locationY = jsonObject.getInt("LocationY");
            }
        }
        this.setLocation(locationX , locationY);
        m_logContent.setSize(width, height);



        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @SneakyThrows
    @Override
    public void doDefaultCloseAction(){
        m_logSource.unregisterListener(this);
        var confirmResult = Dialoger.onExit();
        if (confirmResult == 0) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            FileWriter writer = new FileWriter(".\\src\\main\\java\\serialization\\LogWindowSerialization");

            var jsonObj = new JSONObject();
            jsonObj.put("WindowHeight", m_logContent.getHeight());
            jsonObj.put("WindowWidth", m_logContent.getWidth());
            jsonObj.put("LocationX", this.getLocation().x);
            jsonObj.put("LocationY", this.getLocation().y);
            writer.write(gson.toJson(m_logSource) + '\n' + jsonObj);
            //System.out.println(gson.toJson(m_logSource));
            //writer.write(gson.toJson(m_logSource));
            writer.close();
            super.doDefaultCloseAction();
        }
    }
    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }
}