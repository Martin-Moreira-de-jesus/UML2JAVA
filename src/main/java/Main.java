import org.json.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            String jsonFile = new String(Files.readAllBytes(Paths.get("UMLTestFiles/2classes_association.mdj")));
            JSONObject project = new JSONObject(jsonFile);
            Translator translator = new Translator(project);
            System.out.println(JSONDB.DATABASE.getDatabase());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
