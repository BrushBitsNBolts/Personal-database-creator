import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.imageio.ImageIO;


//Class is designed purely to create and modify files.
//First half of class is creation, second half is modification and deletion.
public class FileCreatorAndManipulator {
    //mainDir is the static (non programming def) location that allows for easy traversing, its the root folder of this project.
    String mainDir;
    //These are here so I am not using magic variables
    public static final String COORDS = "Coords";
    public static final String FILE_LOCATION = "PhotoLocation";
    public static final String AGE = "Age";
    public static final String SKIN_COLOR = "SkinColor";
    public static final String HAIR_TYPE = "HairType";
    public static final String HAIR_COLOR = "HairColor";
    public static final String EYE_COLOR = "EyeColor";
    public static final String NAME = "Name";
    public static final String RACE = "Race";
    public static final String BIRTH_DAY = "BirthDay";
    public static final String DEATH_DAY = "DeathDay";
    public static final String LOCATION = "Position";
    public static final String WEIGHT = "Weight";
    public static final String HEIGHT = "Height";
    public static final String PARAGRAPH = "Paragraph";
    //This is so that everything can be saved easily.
    private ArrayList<ReferencePaths> photos = new ArrayList<>();
    private ArrayList<ReferencePaths> paragraphs = new ArrayList<>();

    //Might not be needed, no planned usage yet.
    public FileCreatorAndManipulator(String name){
        mainDir = name;
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
            newObject.append(LOCATION, new JSONArray().put(0).put(0));
            newObject.append(NAME, "N/A").append(BIRTH_DAY, -1).append(DEATH_DAY, -1)
                    .append(AGE, -1).append(WEIGHT, -1).append(HEIGHT, -1).append(RACE, "N/A")
                    .append(SKIN_COLOR, "N/A").append(EYE_COLOR, "n/a")
                    .append(HAIR_TYPE, "N/A").append(HAIR_COLOR, "N/a");
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
        try(FileWriter setJSON = new FileWriter(fileLocation)) {
            setJSON.write("{}");
        }
    }
    //Ahead of time message to tell myself that this may work with any file with a simple change to check file is editable.
    //todo Next time looking at code look at the alternative solution found, if it is significantly better delete these 2 methods.
    public void addImageToPaneArray(ReferencePaths photoPath){
        //Simply creates a new file import gui then applies it to an integer to varify. Then gets file path

        File testFile = new File(photoPath.returnFileLocation());

            try {
                BufferedImage imageCheck = ImageIO.read(testFile);
                if(imageCheck !=null){
                    photos.add(photoPath);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//todo I might do what this says below in a later update, I am still trying to understand how to make a new tab? Which then can hold multiple instances of objects.
        //From here it will push the file location to the MemoryNode class, it will store it in the preSavedArray ArrayList.
        //The preSavedArray ArrayList holds file locations of items that aren't saved to the array yet.
        //Plan may change to create a temp folder instead to save the image location there incase file gets moved.
    }
    //Simply adds a new file for text array. The reason its like this is because it makes it easier to read the files directly instead of via code.
    public void addTextToArray(ReferencePaths textToBeAdded){
        paragraphs.add(textToBeAdded);
    }
    /*
    Saves the file by converting metadata into a savable format which then can be stored
    in a json file. This includes positions, text, and photos primarily.
     */
        //todo If there is a ton of magic variables inside of methods I was just too lazy at the time to fix. Should work properly otherwise.
    public void saveFile(String fileLocation, ArrayList<ReferencePaths> photos, ArrayList<ReferencePaths> paragraphs){
        JSONArray savePhoto = new JSONArray();
        for(ReferencePaths object:photos){
            JSONObject newObject = new JSONObject();
            newObject.put(COORDS, object.returnGridLocation());
            newObject.put(FILE_LOCATION, object.returnFileLocation());
            savePhoto.put(newObject);
        }
        //Right now this is psudocode because the file location will be a directory which means you need to use the file
        //name to find the location of said corrosponding file through using the pattern given.
        File photoLocation = new File(fileLocation, "Photos");
        try(FileWriter savePoint = new FileWriter(new File(photoLocation, "photo.json"))){
            savePoint.write(savePhoto.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int i = 1;
        File textFolder = new File(fileLocation, "TextFiles");
        File[] text = textFolder.listFiles();
        if(text != null){
            for(File deletable : text){
                try {
                    Files.delete(deletable.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(textFolder.length());
            }
        }
        for(ReferencePaths current : paragraphs){
            JSONObject currentText = new JSONObject();
            currentText.put(COORDS, current.returnGridLocation());
            //In this case returnFileLocation is being used as the text since the file location is arbitrary.
            //This is because fileLocation is itself due to the fact I am using a JSON file as a location and text file.
            //This will also likely be changed in the future for readability, but I am lazy right now.
            //System.out.println(current.returnText());
            currentText.put(PARAGRAPH, current.returnText());
            File textFile = new File(textFolder.getAbsolutePath(), "text"+i+".json");
            if(!textFile.exists()){
                try {
                    textFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try(FileWriter savePoint = new FileWriter(textFile)){
                savePoint.write(currentText.toString(4));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
    }
    /*
    Loads data and pushes it to javafx and other useful class objects.
     */
    public boolean loadData(String location) throws FileNotFoundException {
        File subject = new File(mainDir, location);
        if(!subject.exists()){
            System.out.println("File doesn't exist");
            return false;
        }
        photos.clear();
        paragraphs.clear();

        //mainDir -> def reminder that main dir is the root directory which will be holding all of the info.
        File subjectPhotos = new File(new File(subject, "Photos"), "photo.json");
        JSONArray photoJSON = new JSONArray(new org.json.JSONTokener(new java.io.FileInputStream(subjectPhotos)));
        for(Object object : photoJSON){
            JSONObject photo = (JSONObject) object;
            JSONArray photoCoords = (JSONArray) photo.get(COORDS);
            //System.out.println((String) photo.get(FILE_LOCATION));
            //Literally turning data into an object, thus the definition.
            ReferencePaths photoObjectification = new ReferencePaths((String) photo.get(FILE_LOCATION), photoCoords.getDouble(0), photoCoords.getDouble(1));
            photos.add(photoObjectification);
        }
        File subjectTexts = new File(subject, "TextFiles");
        File[] textFiles = subjectTexts.listFiles();
        if(textFiles != null) {
            for (File textDoc : textFiles) {
                    JSONObject text = new JSONObject(new org.json.JSONTokener(new java.io.FileInputStream(textDoc)));
                    JSONArray textCoords = (JSONArray) text.get(COORDS);
                    //Literally turning data into an object, thus the definition.
                    ReferencePaths textObjectification = new ReferencePaths((String) text.get(PARAGRAPH), textCoords.getDouble(0), textCoords.getDouble(1));
                    paragraphs.add(textObjectification);
            }
        }
        return true;
    }
    //todo This will be added in a later updated version when I have it pull static characteristics like age.
    public void returnStats(String location) throws FileNotFoundException {
        File statsLocation = new File(new File(mainDir, location), location+"Stats.json");
        JSONObject characteristics = new JSONObject(new org.json.JSONTokener(new java.io.FileInputStream(statsLocation)));

    }
    public ArrayList<ReferencePaths> returnText(){
        return paragraphs;
    }
    public ArrayList<ReferencePaths> returnPhotos(){
        return photos;
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
