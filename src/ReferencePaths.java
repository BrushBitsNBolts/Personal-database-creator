import java.io.File;

//This object is required for easily and safely setting photo locations, it is also used to allign other files to coords.
public class ReferencePaths {
    private String referenceLocation;
    private double[] planeLocation = new double[2];
    //Sets location, next update will include a feature where setting this will default spawning at 0,0 on pane.
    public ReferencePaths(String location){
        referenceLocation = location;
    }
    //Sets the File path as string and javafxCoords as int respectively.
    public ReferencePaths(String location, double planeLocation1, double planeLocation2){
        referenceLocation = location;
        //System.out.println(location);
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
    //Returns referenceLocation which is being used for text in this case.
    public String returnText(){
        return referenceLocation;
    }
    public double[] returnGridLocation(){
        return planeLocation;
    }
    //Likely will not be used as inorder to actually create a referencepath you need to choose a file location to begin with.
    //Reason that stops this from being used much is that photos and text files are usually a static locational reference.
    //Might be useful for optimization.
    //todo Next update if needed, include path updating via custom user input. Might not be needed with current setup.
    public void setReferenceLocation(String reference){
        this.referenceLocation = reference;
    }
    //todo Next update if needed, include position updating from external source. Currently not needed. Otherwise delete.
    public void setPlaneLocation(double[] coords){
        planeLocation[0] = coords[0];
        planeLocation[1] = coords[1];
    }
}
