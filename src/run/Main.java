/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run;

import java.io.IOException;
import java.util.Arrays;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scheduler.gui.controller.TaskGroup;
import scheduler.model.RateMonotonic;
import scheduler.model.Scheduler;
import scheduler.model.Task;

/**
 *
 * @author yanka
 */
public class Main extends Application {

  @Override
  public void start(Stage primaryStage) {
    AnchorPane root = new AnchorPane();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/scheduler/gui/TaskGroup.fxml"));
    try {
      root = loader.load();
      TaskGroup controler = loader.getController();
      
      Scheduler scheduler = new RateMonotonic();
      scheduler.add(new Task("A", 0, 2, 4, 5));
      scheduler.add(new Task("B", 1, 1, 3, 3));
//      Task[] tasks = scheduler.compute();
//      System.out.println(Arrays.toString(tasks));
      
     
    }
    catch (IOException iOException) {
      iOException.printStackTrace();
    }

    Scene scene = new Scene(root, 900, 400);
    primaryStage.setTitle("Escalonador de processor");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

}
