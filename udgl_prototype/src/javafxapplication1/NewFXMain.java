/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 *
 * @author Narayan G.M
 */
public class NewFXMain extends Application {

    
    public static void main(String[] args) {
        Application.launch(NewFXMain.class, args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Detective Glass");
        Group root = new Group();
        Scene scene = new Scene(root, 500, 431,Color.BLACK);

        //ImageView 
        ImageView maskView = new ImageView();    
        maskView.setCursor(Cursor.NONE);
        //Image is loaded 
        Image image = new Image(NewFXMain.class.getResource("pic.jpg").toExternalForm());
        maskView.setImage(image);
        //Mask Shape 
        final Circle glass = new Circle(100,100,100);
        maskView.setClip(glass);
        
        //adding to the root's children
        root.getChildren().add(maskView);
        
        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                glass.setCenterX(t.getX());
                glass.setCenterY(t.getY());
            }
        });

        primaryStage.setScene(scene);  
        primaryStage.show();
    }
}

