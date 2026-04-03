/*
Memory node is a class to hold represented datapoints and no that can easily be deleted using java's trash collector.
Essentially this holds the data and allows you to edit it for the fileManipulation function.
It also makes it easy to fully load and hold the actual data into the visualizer (javafx text image and other stuffs)
 */

import java.io.File;
import java.util.ArrayList;

public class MemoryNode {
    //Holds the root node for general location. This will be based off someones name.
    File rootLocation;

    //Holds the photo File location and coordinates on the JavaFX pane. (x,y coords)
    private ArrayList<ReferencePaths> photoData = new ArrayList<>();
    //References the location of the json file that holds generic data on person.
    private ReferencePaths jsonData = new ReferencePaths(null);
    //Holds the location and coords for text files and JavaFX pane respectively.
    private ArrayList<ReferencePaths> metadataReferences = new ArrayList<>();

    //Required to lazily set rootLocation and jsonData sheet location. This makes it easier to access actual locations
    //of file since root file will always contain the same folders just with differing data and images.
    MemoryNode(String location){
        rootLocation = new File(location);
        if(!rootLocation.exists()){
            //If rootlocation does not exist something serious has gone wrong, program should probably be restart.
        }
        jsonData.setReferenceLocation(new File(location ,"personStats.json").getAbsolutePath());
    }
    //Takes a json file that has an object that lists coords then the paragraph
    public void addToParagraphs(File textFile){
        ReferencePaths newReference = new ReferencePaths(textFile.getAbsolutePath());
    }
}
