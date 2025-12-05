package controller;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// Controller class for lottery application
public class LottoController {

	// FXML UI components
    @FXML private TextField minField;
    @FXML private TextField maxField;
    @FXML private TextField ballsField;
    @FXML private Label resultLabel;
    @FXML private TextArea threadOutput;
    
    // Hashtable to store lottery results
    private Hashtable<Integer, String> lottoTable = new Hashtable<>();
    private int drawCounter = 1;
    private SecureRandom secureRandom = new SecureRandom();
    
    
    // Handle the generation of lottery numbers
    @FXML
    private void handleGenerate() {
        try {
        	// Parse input values from user - min and max numerical values and number of balls
            int min = Integer.parseInt(minField.getText());
            int max = Integer.parseInt(maxField.getText());
            int balls = Integer.parseInt(ballsField.getText());
            
            // Validate input values
            if (min < 0 || max > 100 || min >= max || balls <= 0 || (max - min + 1) < balls) {
                resultLabel.setText("Error: Invalid input! Min:0-99, Max:1-100, Min<Max, Balls >0");
                return;
            }
            
            // Generate unique random lottery numbers
            Set<Integer> uniqueNumbers = new HashSet<>();
            while (uniqueNumbers.size() < balls) {
                int randomNum = secureRandom.nextInt(max - min + 1) + min;
                uniqueNumbers.add(randomNum);
            }
            
            // Sort and format the generated numbers
            List<Integer> sortedNumbers = new ArrayList<>(uniqueNumbers);
            Collections.sort(sortedNumbers);
            
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < sortedNumbers.size(); i++) {
                formatted.append(sortedNumbers.get(i));
                if (i < sortedNumbers.size() - 1) formatted.append(", ");
            }
            String result = formatted.toString();
            
            
            // Display result
            resultLabel.setText("Your lotto numbers are: " + result);
            lottoTable.put(drawCounter, result);
            
            // Save result in Hashtable and save to database
            System.out.println("Draw " + drawCounter + ": " + result);
            database.DatabaseManager.saveResult(drawCounter, result);
            System.out.println("Hashtable size: " + lottoTable.size());
            
            drawCounter++; // Increment draw counter for next draw
            
        } catch (NumberFormatException e) {
            resultLabel.setText("Error: Please enter valid numbers!");
        } catch (Exception e) {
            resultLabel.setText("Error: " + e.getMessage());
        }
    }
    
    // Handle concurrent lottery draws using threads "Generate 5 Lotto Draws with Threads" button
    @FXML
    private void handleThreads() {
        threadOutput.clear();
        threadOutput.appendText("Starting 5 concurrent lotto draws...\n\n");
        
        // Parse input values from user for draws with threads
        final int min = Integer.parseInt(minField.getText());
        final int max = Integer.parseInt(maxField.getText());
        final int balls = Integer.parseInt(ballsField.getText());
        
        if (min < 0 || max > 100 || min >= max || balls <= 0 || (max - min + 1) < balls) {
            threadOutput.appendText("Error: Invalid input values for threads!\n");
            return;
        }
        
        // Create and start 5 threads for concurrent lottery draws
        for (int i = 1; i <= 5; i++) {
            final int threadNum = i;
            final int drawNum = drawCounter + i - 1;
            
            Thread thread = new Thread(() -> {
                try {
                	// Generate unique random lottery numbers in each thread
                    Set<Integer> uniqueNumbers = new HashSet<>();
                    while (uniqueNumbers.size() < balls) {
                        int randomNum = secureRandom.nextInt(max - min + 1) + min;
                        uniqueNumbers.add(randomNum);
                    }
                    
                    List<Integer> sortedNumbers = new ArrayList<>(uniqueNumbers);
                    Collections.sort(sortedNumbers);
                    
                    StringBuilder formatted = new StringBuilder();
                    for (int j = 0; j < sortedNumbers.size(); j++) {
                        formatted.append(sortedNumbers.get(j));
                        if (j < sortedNumbers.size() - 1) formatted.append(", ");
                    }
                    String result = formatted.toString();
                    
                    // Store result in Hashtable
                    synchronized(lottoTable) {
                        lottoTable.put(drawNum, result);
                    }
                    
                    // Save result to database
                    database.DatabaseManager.saveResult(drawNum, result);
                    
                    Platform.runLater(() -> {
                        threadOutput.appendText("Thread " + threadNum + 
                                              " -> Draw #" + drawNum + ": " + result + "\n");
                    });
                    
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        threadOutput.appendText("Thread " + threadNum + 
                                              " Error: " + e.getMessage() + "\n");
                    });
                }
            });
            
            thread.setName("Lotto-Thread-" + i);
            thread.start(); // Start the thread
        }
        
        drawCounter += 5; // Update draw counter after launching threads
        
        // Show final summary after a short delay to ensure all threads have started
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                
                Platform.runLater(() -> {
                    threadOutput.appendText("\nAll 5 threads launched successfully.\n");
                    threadOutput.appendText("Total draws in Hashtable: " + lottoTable.size() + "\n");
                    threadOutput.appendText("Check Console for database save attempts.\n");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}