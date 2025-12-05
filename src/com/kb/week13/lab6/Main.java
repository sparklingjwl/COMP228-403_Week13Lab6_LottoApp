package com.kb.week13.lab6;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
       // Load the window layout from the FXML file
        String fxmlPath = "C:\\Users\\spark\\eclipse-workspace-Fall2025\\Week13_Lab6_KellyBurden\\src\\view\\lotto_gui.fxml";
        Parent root = FXMLLoader.load(new File(fxmlPath).toURI().toURL());
        
        // Set up the primary stage (main application window)
        primaryStage.setTitle("Week13 Lab6 - Quick Pick Lotto");
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }
    
    public static void main(String[] args) {
    	
    	// Launch the JavaFX application
        launch(args);
    }
}