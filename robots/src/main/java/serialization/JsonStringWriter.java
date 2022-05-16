package serialization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class JsonStringWriter {
    public static void WriteJsonString(String jsonString, String path){
        try (FileWriter writer = new FileWriter(path);
             BufferedWriter bw = new BufferedWriter(writer)) {

            bw.write(jsonString);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
