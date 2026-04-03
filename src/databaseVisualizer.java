import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.jfr.Event;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.beans.EventHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class databaseVisualizer extends Application {
    public void databaseVisualizer(){

    }
    final double[] mouseCoords = new double[2];
    Node target;
    //Essentially main class for javafx thread, everything after setup gets ran through here.
    @Override
    public void start(Stage stage) throws Exception {
        Pane userInfo = new Pane();
        Scene workSpace = new Scene(userInfo);
        stage.setScene(workSpace);
        stage.setHeight(1000);
        stage.setWidth(1000);
        stage.show();
        Button newFileButton = new Button();
        newFileButton.setLayoutX(100);
        newFileButton.setLayoutY(100);
        newFileButton.setText("Upload Photo");
        //WIP Dragging logic
        Text textTest = new Text("ThisIsATestString");
        Text textTest2 = new Text("ThisIsATestString2");
        userInfo.getChildren().add(textTest);
        userInfo.getChildren().add(textTest2);
        workSpace.setOnMousePressed(e -> {
            Object rawTarget = e.getTarget();
            if(rawTarget instanceof Scene){
                target = ((Scene) rawTarget).getRoot();
            }else{
                target = ((Node) rawTarget);
                target.toFront();
            }
            System.out.println(target);
            if(target instanceof Text textNode) {
                mouseCoords[0] = e.getSceneX() - textNode.getX();
                mouseCoords[1] = e.getSceneY() - textNode.getY();
            }else{

                mouseCoords[0] = e.getSceneX() - target.getLayoutX();
                mouseCoords[1] = e.getSceneY() - target.getLayoutY();
            }
        });
        workSpace.setOnMouseDragged(e -> {
            if(target instanceof Text textNode) {
                textNode.setX((e.getSceneX() - mouseCoords[0]));
                textNode.setY((e.getSceneY() - mouseCoords[1]));

            }else{
                target.setLayoutX(e.getSceneX() - mouseCoords[0]);
                target.setLayoutY(e.getSceneY() - mouseCoords[1]);
            }
        });
//        FileCreatorAndManipulator tst = new FileCreatorAndManipulator("person");
//        String location = tst.addImageToPaneArray();

        newFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if(file != null){
                System.out.println(file.getAbsolutePath());

                try {
                    BufferedImage imageCheck = ImageIO.read(file);
                    if(imageCheck != null){
                        ImageView newImage = new ImageView(new Image(file.toURI().toString()));
                        userInfo.getChildren().add(newImage);
                        pullImageInfo(newImage, file.getAbsolutePath());
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }else{
                System.out.println("File not valid or window closed");
            }
        });

        userInfo.getChildren().add(newFileButton);
    }

    public void pullImageInfo(ImageView image, String location){
        System.out.println(image.getLayoutX());
        System.out.println(image.getLayoutY());
        System.out.println(location);
        //From here it pushes to fileCreatorAndManipulator
    }
    public void pullTextInfo(Text text){

    }
}
