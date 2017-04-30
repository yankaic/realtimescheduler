/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.gui.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import scheduler.gui.Taskview;
import scheduler.model.RateMonotonic;
import scheduler.model.Scheduler;
import scheduler.model.Task;

/**
 * FXML Controller class
 *
 * @author yanka
 */
public class TaskGroup implements Initializable {

  @FXML
  private Slider slider;
  @FXML
  private Pane area;
  Taskview view;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {

  }

  private void passar() {
    Scheduler scheduler = new RateMonotonic();
    scheduler.add(new Task("A", 0, 2, 5, 5));
    scheduler.add(new Task("B", 1, 1, 3, 3));
    Task[] tasks = scheduler.compute();
    System.out.println(Arrays.toString(tasks));
    view = new Taskview(tasks[1]);
    area.getChildren().add(view);
  }

  @FXML
  private void changeAction(MouseEvent event) {
    double value = slider.getValue();
    for (Node node : area.getChildren()) {
      ((Taskview) node).setTime(value);
    }
  }

  public void addTask(Task task) {
    Taskview view = new Taskview(task);
    while (areColision(view)) {
      view.setLayoutY(view.getLayoutY() + 14);
      System.out.println("não consigo sair do loop");
      System.out.println("tamanho: "+view.getLayoutBounds());
    }
    area.getChildren().add(view);

  }

  public boolean areColision(Taskview view) {
    for (Node node : area.getChildren()) {
      Taskview child = (Taskview) node;
      Bounds area1 = child.getBoundsInLocal();
      Bounds area2 = view.getBoundsInLocal();
      boolean contains = area1.intersects(area2);
      if (contains) {
        return true;
      }
    }
    return false;
  }

}
