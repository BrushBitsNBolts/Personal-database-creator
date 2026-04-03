import java.io.File;

//This object is required for easily and safely setting photo locations, it is also used to allign other files to coords.
public class ReferencePaths {
    String referenceLocation;
    int[] planeLocation = new int[2];
    //Gets photo location
    public ReferencePaths(String location){
        referenceLocation = location;
    }
    //Sets the File path as string and javafxCoords as int respectively.
    public ReferencePaths(String location, int planeLocation1, int planeLocation2){
        referenceLocation = location;
        this.planeLocation[0]=planeLocation1;
        this.planeLocation[1]=planeLocation2;
    }
    //Returns the location of said file, this is needed to verify and make a copy if saved.
    public String returnFileLocation(){
        if(new File(referenceLocation).exists()){
            return referenceLocation;
        }
        System.out.println("File location has likely been moved thus photo could not be found");
        return null;
    }
    public int[] returnGridLocation(){
        return planeLocation;
    }
    //Likely will not be used as inorder to actually create a referencepath you need to choose a file location to begin with.
    //Reason that stops this from being used much is that photos and text files are usually a static locational reference.
    //Might be useful for optimization.
    public void setReferenceLocation(String reference){
        this.referenceLocation = reference;
    }
    public void setPlaneLocation(int[] coords){
        planeLocation[0] = coords[0];
        planeLocation[1] = coords[1];
    }
}
