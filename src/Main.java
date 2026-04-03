import javafx.animation.Animation;
import org.json.CDL;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        String mainDir = "Resources";
        File location = new File(mainDir);
        if(!location.exists()){
            location.mkdir();

        }
        //This was a test so that I know how to pull files from json, this is a way to pull data from json.
        /*
        String json = new String(Files.readAllBytes(Paths.get(new File(new File(location.getAbsolutePath(), "cat"), "catStats.json").getAbsolutePath())));
        JSONObject test = new JSONObject(json);
        System.out.println(test.get("HairType"));
         */
        //FileCreatorAndManipulator a = new FileCreatorAndManipulator(mainDir);
        //a.createNewFile("cat");
        //a.createNewTextFile("person","paragraph1");
        databaseVisualizer.launch(databaseVisualizer.class, args);
    }
}