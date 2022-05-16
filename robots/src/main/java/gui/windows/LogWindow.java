package gui.windows;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.util.JSONPObject;
import gui.Exiter;
import log.LogChangeListener;
import log.LogEntry;
import log.LogLevel;
import log.LogWindowSource;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import serialization.JsonStringWriter;

public class LogWindow extends JInternalFrame implements LogChangeListener, GetLocalizeLabel {
    private LogWindowSource notFinalLogSource;
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;


    public LogWindow(LogWindowSource logSource) throws IOException {
        super(GetLocalizeLabel.getLocalization("protocolLabel"),
                true, true, true, true);


        ObjectMapper objectMapper = new ObjectMapper();
        BufferedReader reader;
        String json;
        reader = new BufferedReader(new FileReader(
                ".\\src\\main\\java\\serialization\\LogWindowSerialization"));
        json = reader.readLine();


        if (json != null && json.length()>0){
            var exitDialogResult = Exiter.onExit();
            if (exitDialogResult == 0){
                var jsonArray = new JSONObject(json).getJSONArray("m_messages");
                var logEntryList = new LinkedList<LogEntry>();
                if (jsonArray != null) {
                    for (int i=0;i<jsonArray.length();i++){
                        var jsonObject = jsonArray.getJSONObject(i);
                        var log = new LogEntry(LogLevel.valueOf(jsonObject.getString("level")),
                                jsonObject.getString("message"));
                        logEntryList.add(log);
                        notFinalLogSource = new LogWindowSource(logEntryList, new ArrayList<>());
                    }
                }
            }
        }
        if (notFinalLogSource == null)
            notFinalLogSource = logSource;
        m_logSource = notFinalLogSource;








        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
        /*
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BufferedReader reader;
            String json;
            reader = new BufferedReader(new FileReader(
                    ".\\src\\main\\java\\serialization\\LogWindowSerialization"));
            json = reader.readLine();
            if (json.length()>0) {
                var exitDialogResult = Exiter.onExit();
                if (exitDialogResult == 0){
                    m_logSource = objectMapper.readValue(json, LogWindowSource.class);
                }
                else
                    m_logSource = logSource;
            }
            else
                m_logSource = logSource;
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

         */

        /*
        if (1!=1)
            m_logSource = logSource;
        else {
            ObjectMapper objectMapper = new ObjectMapper();
            BufferedReader reader;
            String json;
            try {
                reader = new BufferedReader(new FileReader(
                        ".\\src\\main\\java\\serialization\\LogWindowSerialization"));
                json = reader.readLine();

                while (line != null) {
                    System.out.println(line);
                    // read next line
                    line = reader.readLine();
                }

                m_logSource = objectMapper.readValue(json, LogWindowSource.class);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        */

    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void doDefaultCloseAction(){
        m_logSource.unregisterListener(this);
        var confirmResult = Exiter.onExit();
        if (confirmResult == 0) {
            //за джейсонить ресурсы
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String json =objectMapper.writeValueAsString(m_logSource);
                JsonStringWriter.WriteJsonString(json, ".\\src\\main\\java\\serialization\\LogWindowSerialization");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            super.doDefaultCloseAction();
        }
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }
}