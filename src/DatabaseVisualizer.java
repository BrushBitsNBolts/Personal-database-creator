import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

public class DatabaseVisualizer extends Application {
    private static String mainDir;
    private String currentSubject;

    public static void setStatics(String mainDir){
        DatabaseVisualizer.mainDir = mainDir;
    }
    /*
    public void databaseVisualizer(){

    }
     */
    FileCreatorAndManipulator fileSystem = new FileCreatorAndManipulator(mainDir);
    final double[] mouseCoords = new double[2];
    Node target;
    //I prefer to make pane objects global because its what is being modified 9/10 of the time.
    Pane rootPane = new Pane();
    Pane userInfo = new Pane();
    Pane gui = new Pane();

    //Area below here is incase anything needs to be held as a reference.
    double dropDownXPos;
    double dropDownYPos;
    //Essentially main class for javafx thread, everything after setup gets ran through here.

    @Override
    public void start(Stage stage){
        rootPane.getChildren().add(userInfo);
        rootPane.getChildren().add(gui);
        Scene workSpace = new Scene(rootPane);
        stage.setScene(workSpace);
        stage.setHeight(1000);
        stage.setWidth(1000);
        stage.show();

        TextArea textBox = new TextArea("Type Paragraph here");
        textBox.setVisible(false);


        //fake todo Everything above here is just setup.

        //WIP Dragging logic


        workSpace.setOnMousePressed(e -> {
            if(!gui.isMouseTransparent()){
                return;
            }
            Object rawTarget = e.getTarget();

                if (rawTarget instanceof Pane) {
                    target = userInfo;
                } else {
                    target = ((Node) rawTarget);
                    target.toFront();
                }
                //System.out.println(target);
                if (target instanceof Text textNode) {
                    mouseCoords[0] = e.getSceneX() - textNode.getLayoutX();
                    mouseCoords[1] = e.getSceneY() - textNode.getLayoutY();
                } else {
                    mouseCoords[0] = e.getSceneX() - target.getLayoutX();
                    mouseCoords[1] = e.getSceneY() - target.getLayoutY();
                }
        });
        workSpace.setOnMouseDragged(e -> {
            if(!gui.isMouseTransparent()){
                return;
            }
            if(target instanceof Text textNode) {
                textNode.setLayoutX((e.getSceneX() - mouseCoords[0]));
                textNode.setLayoutY((e.getSceneY() - mouseCoords[1]));

            }else{
                target.setLayoutX(e.getSceneX() - mouseCoords[0]);
                target.setLayoutY(e.getSceneY() - mouseCoords[1]);
            }
        });
        //

        //These are the variables reffered for the next section. This is the right click popup menu.
        ContextMenu menu = new ContextMenu();
        MenuItem editText = new MenuItem("Edit Text");
        MenuItem editMode = new MenuItem("Enter Edit Mode");
        MenuItem createNewText = new MenuItem("New Text");
        MenuItem createNewPhoto = new MenuItem("New Photo");
        MenuItem deleteObject = new MenuItem("Delete");
        menu.getItems().add(editText);
        menu.getItems().add(editMode);
        menu.getItems().add(createNewText);
        menu.getItems().add(createNewPhoto);
        menu.getItems().add(deleteObject);

        //
        workSpace.setOnMouseClicked(e -> {
            //Possible clickable options inside of the contextmenu. (dropdown)
            editText.setVisible(false);
            createNewText.setVisible(false);
            createNewPhoto.setVisible(false);
            deleteObject.setVisible(false);

            //Edit mode is special as it allows for editing inside of the userInfo pane.
            editMode.setVisible(false);

            dropDownXPos = e.getScreenX();
            dropDownYPos = e.getScreenY();

            Object clicked = e.getTarget();
            if(clicked instanceof Text text && e.getButton().equals(MouseButton.SECONDARY)){
                editText.setVisible(true);
                deleteObject.setVisible(true);

                target = text;
            }else if(clicked instanceof ImageView image && e.getButton().equals(MouseButton.SECONDARY)){
                deleteObject.setVisible(true);
                target = image;
            }else if((clicked instanceof Pane || clicked instanceof Scene) && e.getButton().equals(MouseButton.SECONDARY)){
                if(gui.isMouseTransparent()) {
                    createNewText.setVisible(true);
                    createNewPhoto.setVisible(true);
                }
                editMode.setVisible(true);
            }
            menu.show(userInfo, dropDownXPos, dropDownYPos);
        });

        HBox buttons = new HBox(10);

        /*todo Currently this is lazily written because of how javafx sets its initial variables.
            Essentially it seems javafx reads everything in here then sets things which means if you don't set widths etc
            it will start at base value (0). For now this will stay unoptimized because right now I am creating these documents
            to be systems I can use in my personal life.

         */
        //This is for creating a new file.
        TextField textField = new TextField();
        textField.setVisible(false);
        gui.getChildren().add(textField);
        Button createNewSubject = new Button("New Document");
        createNewSubject.setOnAction(e->{
            textField.setLayoutX(createNewSubject.getLayoutX());
            textField.setLayoutY(createNewSubject.getLayoutY()+createNewSubject.getHeight());
            textField.setText("");
            createNewSubject.setVisible(false);
            textField.setVisible(true);
        });
        textField.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER)){
                if(textField.getText().trim().length() > 5){
                    File testFile = new File(mainDir, textField.getText());
                    if(!testFile.exists()){
                        currentSubject = textField.getText();
                        fileSystem.createNewFile(currentSubject);
                        //System.out.println("Test");

                    }else{
                        System.out.println("File already exists");
                    }
                }else{
                    //Indicator goes here in next update. For instance if length is less than 5 then indicator pops up on screen or something.
                }
                createNewSubject.setVisible(true);
                textField.setVisible(false);
            }
        });

        Button save = new Button("Save");
        save.setOnAction(e-> save());

        Button load = new Button("load");
        TextField textLoader = new TextField();
        gui.getChildren().add(buttons);
        load.setOnAction(e->{
            textLoader.setLayoutX(load.getLayoutX());
            textLoader.setLayoutY(load.getLayoutY());
            gui.getChildren().add(textLoader);
        });
        textLoader.setOnKeyPressed(e ->{
            if(e.getCode().equals(KeyCode.ENTER)){
                try {
                    load(textLoader.getText());
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                gui.getChildren().remove(textLoader);
            }
        });

        buttons.getChildren().addAll(save, load, createNewSubject);

        deleteObject.setOnAction(e-> userInfo.getChildren().remove(target));


        editText.setOnAction(e->{
            if(!textBox.isVisible()){
                Text text = (Text) target;
                //System.out.println(target);
                textBox.setText((text).getText());
                textBox.setLayoutX(text.getLayoutX());
                textBox.setLayoutY(text.getLayoutY());
                userInfo.getChildren().remove(target);
                textBox.setVisible(true);
                //System.out.println(textBox.getLayoutX());
                //System.out.println(textBox.getLayoutY());
            }
        });
        editMode.setOnAction(e -> {
            if(gui.isMouseTransparent()){
                gui.setMouseTransparent(false);
                userInfo.setMouseTransparent(true);
            }else{
                gui.setMouseTransparent(true);
                userInfo.setMouseTransparent(false);
            }
        });

        createNewText.setOnAction(e->{
            Text newText = new Text("Right click, click edit!");
            newText.setLayoutX(dropDownXPos);
            newText.setLayoutY(dropDownYPos);
            userInfo.getChildren().add(newText);
        });
        createNewPhoto.setOnAction(e->{
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if(file != null){
                //System.out.println(file.getAbsolutePath());

                try {
                    BufferedImage imageCheck = ImageIO.read(file);
                    if(imageCheck != null){
                        ImageView newImage = new ImageView(new Image(file.toURI().toString()));
                        newImage.setLayoutX(dropDownXPos);
                        newImage.setLayoutY(dropDownYPos);
                        userInfo.getChildren().add(newImage);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }else{
                System.out.println("File not valid or window closed");
            }
        });
        //

        //
        //This logic controls the ability to edit and save text boxes.
        textBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F1 && e.isControlDown() && textBox.isEditable()) {
                textBox.setEditable(false);
                textBox.setMouseTransparent(true);
            }else if(!textBox.isEditable() && e.getCode()==KeyCode.F1){
                textBox.setEditable(true);
                textBox.setMouseTransparent(false);
            }
            if(userInfo.getChildren().contains(textBox) && e.getCode() == KeyCode.F2){
                Text text;
                if(textBox.getText().trim().length() <= 1){
                    text = new Text("Right click for text options.");
                }else{
                    text = new Text(textBox.getText());
                }
                text.setX(textBox.getLayoutX());
                text.setY(textBox.getLayoutY());
                textBox.setVisible(false);
                userInfo.getChildren().add(text);
            }


            // Do your "button action" here
        });
        userInfo.getChildren().add(textBox);
    }

    public void save(){
        ArrayList<ReferencePaths> images = new ArrayList<>();
        ArrayList<ReferencePaths> texts = new ArrayList<>();
        //Converts current images and text into ReferencePath objects.
        for(Node child : userInfo.getChildren()){
            if(child instanceof ImageView image){
                String url = image.getImage().getUrl();
                File file = new File(URI.create(url));
                ReferencePaths newImage = new ReferencePaths(file.getAbsolutePath(), image.getLayoutX(), image.getLayoutY());
                images.add(newImage);
            }else if(child instanceof Text text){
                ReferencePaths newText = new ReferencePaths(text.getText(), text.getLayoutX(), text.getLayoutY());
                texts.add(newText);
            }
        }
        fileSystem.saveFile(new File(mainDir, currentSubject).getAbsolutePath(), images, texts);
    }
    //Clears pane then loads a new one.
    public void load(String fileName) throws FileNotFoundException {
        if(fileName.isEmpty() || !fileSystem.loadData(fileName)){
            return;
        }
        currentSubject = fileName;
        userInfo.getChildren().clear();
        for(ReferencePaths photo : fileSystem.returnPhotos()){
            ImageView image = new ImageView(new Image(new File(photo.returnFileLocation()).toURI().toString()));
            image.setLayoutX(photo.returnGridLocation()[0]);
            image.setLayoutY(photo.returnGridLocation()[1]);
            userInfo.getChildren().add(image);
        }
        for(ReferencePaths text : fileSystem.returnText()){
            //System.out.println(text.returnText());
            Text paragraph = new Text(text.returnText());
            //System.out.println(paragraph.getText());
            paragraph.setLayoutX(text.returnGridLocation()[0]);
            paragraph.setLayoutY(text.returnGridLocation()[1]);
            userInfo.getChildren().add(paragraph);
        }
        fileSystem.returnStats(fileName);
    }
}
