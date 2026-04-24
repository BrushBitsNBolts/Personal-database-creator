import java.io.File;

public class Main {

    public static void main(String[] args){
        String mainDir = "Resources";
        File location = new File(mainDir);
        if(!location.exists()){
            location.mkdir();
        }
        DatabaseVisualizer.setStatics(mainDir);
        DatabaseVisualizer.launch(DatabaseVisualizer.class, args);
    }
}