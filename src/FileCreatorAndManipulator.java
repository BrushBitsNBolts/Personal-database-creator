import javafx.stage.FileChooser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;


//Class is designed purely to create and modify files.
//First half of class is creation, second half is modification and deletion.
public class FileCreatorAndManipulator {
    //mainDir is the static (non programming def) location that allows for easy traversing, its the root folder of this project.
    String mainDir;
    String currentPath;
    String preSavedArray;
    //This is so that everything can be saved easily.
    private ArrayList<ReferencePaths> temperaryphotos = new ArrayList<>();

    //Might not be needed, no planned usage yet.
    public FileCreatorAndManipulator(String name){
        mainDir = name;
        //addImageToPaneArray();
    }
    /*
    Creates a blank new folder uses the first letter of the folder name and sorts it
    alphebetically into storage system.
        todo Not sure if I will implement it like I said above, instead I might just make a file and inside the file
         have all the basic info in it. Keep it simple.
    */
    public void createNewFile(String fileName){
        //Check what folder FileName goes into
        try {
            //char fileFirst = fileName.charAt(0);
            File location = new File(mainDir);
            //todo Create a unique error to check if a file exists. If it doesn't it should save and exit immediately
            //Guarentees that user knows where the file resources file goes incase of accidentily deleted.
            // Has a flaw if deleted prior to launching. In that case they will need to check the bin/recycle folder on their os.
            if(!location.exists()){
                System.out.println("File not found, likely deleted mid process check trash bin.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        File location = new File(mainDir, fileName);
        location.mkdir();

        File photoFolder = new File(location.getAbsolutePath(), "Photos");
        File paragraphFolder = new File(location.getAbsolutePath(), "TextFiles");
        File subjectInfoJSON = new File(location.getAbsolutePath(), fileName+"Stats.json");
        //The following statements are just file creation to create a standard, the first if statement forces it to skip this line if it is not needed.
        //For instance if you already have a folder with the designated paths then it should just skip everything.
        if(!(photoFolder.exists() && paragraphFolder.exists() && subjectInfoJSON.exists())) {
            if (!photoFolder.exists()) {
                //Creates a folder for photos which will have 2 types of files. A single json file that holds an array.
                //This array holds JSONObjects that have 2 variables, photo file location and coordinates for JavaFX.
                //Base array is empty as the folder has no images.
                photoFolder.mkdir();
                File createPhotoSetup = new File(photoFolder.getAbsolutePath(), "photo.json");
                try {
                    createPhotoSetup.createNewFile();
                    JSONArray photoLogic = new JSONArray();
                    try (FileWriter writer = new FileWriter(createPhotoSetup.getAbsolutePath())) {
                        writer.write(photoLogic.toString(4));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!paragraphFolder.exists()) {
                paragraphFolder.mkdir();
            }

            if (!subjectInfoJSON.exists()) {
                try {
                    subjectInfoJSON.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //Since everything has succeeded up till now the next step is to edit the json file to create the basic stats.
        if(subjectInfoJSON.length() < 10){
            createEmptyJSONSetup(subjectInfoJSON.getAbsolutePath());
        }
    }
    //Entire point of this helper method is to create the json list, the list holds the position.
    //The list's perpose is also to hold the base info about the person thus can be created early.
    //Outside of making the list of info there is no purpose for it.
    public void createEmptyJSONSetup(String fileLocation){
        try {
            jsonQuickSet(fileLocation);
            JSONTokener token = new JSONTokener(new FileReader(fileLocation));
            JSONObject newObject = new JSONObject(token);
            //"Physical aspects of a person"
            newObject.append("Position", new JSONArray().put(0).put(0));
            newObject.append("Name:", "N/A").append("BirthDay", -1).append("DeathDay", -1)
                    .append("Age", -1).append("Weight", -1).append("Height", -1).append("Race", "N/A")
                    .append("SkinColor", "N/A").append("EyeColor", "n/a")
                    .append("HairType", "N/A").append("HairColor", "N/a");
            try(FileWriter write = new FileWriter(fileLocation)){
                write.write(newObject.toString(4));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //This was made because I have issues with JSON that I need to learn still. I think it's the difference between
    //FileWriter and whatever JSON uses to file write. JSON's version of writing to a file will freak out if {} doesn't
    //exist. FileWriter just writes to a file.
    public void jsonQuickSet(String fileLocation) throws IOException {
        FileWriter setJSON = new FileWriter(fileLocation);
            setJSON.write("{}");
            setJSON.flush();
            setJSON.close();
    }
    //Ahead of time message to tell myself that this may work with any file with a simple change to check file is editable.
    public String addImageToPaneArray(String photoPath){
        //Simply creates a new file import gui then applies it to an integer to varify. Then gets file path

        File testFile = new File(photoPath);
        if(!testFile.exists()){
            return null;
        }
            try {
                BufferedImage imageCheck = ImageIO.read(testFile);
                if(imageCheck !=null){
                    return photoPath;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        //From here it will push the file location to the MemoryNode class, it will store it in the preSavedArray ArrayList.
        //The preSavedArray ArrayList holds file locations of items that aren't saved to the array yet.
        //Plan may change to create a temp folder instead to save the image location there incase file gets moved.
        return null;
    }
    //Takes the path of said person and creates a new empty text file. This text file is a json file that holds grid
    //location and the actual paragraph.
    public void createNewTextFile(String personFile, String name) throws IOException {
        File textPath = new File(new File(mainDir, personFile),"TextFiles");
        File newTextFile = new File(textPath.getAbsolutePath(), name+".json");
        if(!newTextFile.exists()){
            newTextFile.createNewFile();
            //jsonQuickSet(newTextFile.getAbsolutePath());
            JSONObject paragraphContext = new JSONObject();
            paragraphContext.put("Coords", new JSONArray().put(0).put(0));
            paragraphContext.put("Paragraph", "tempParagraph");
            try(FileWriter write = new FileWriter(newTextFile.getAbsolutePath(), true)) {
                write.write(paragraphContext.toString(4));
            }
        }
    }
    /*
    Saves the file by converting metadata into a savable format which then can be stored
    in a json file. This includes positions, text, and photos primarily.
     */
    public void saveFile(ArrayList<Object> paneObjects){

    }
    /*
    Loads data and pushes it to javafx and other useful class objects.
     */
    public void loadData(){

    }
    /*
    Checks a few obvious things about folders and deletes or modifies folders.
    Checks for things like duplicates, empty folders (if clicked), or parsable data.
     */
    public void optimizeFiles(){

    }
    /*Places file into seperate folder that is considered to be the junk folder
    Anything inside the junk folder can be pulled back into the application so that
    if something is accidently deleted it is easily recoverable.
     */
    public void deleteFile(File location){

    }
}
