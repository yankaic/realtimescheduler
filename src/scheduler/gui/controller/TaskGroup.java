/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import scheduler.gui.Taskview;
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
  private AnchorPane area;
  private Line line;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    line = new Line(0, 0, 0, 300);
    Timeline timeline = new Timeline(new KeyFrame(Duration.ONE, e -> {
      area.getChildren().add(line);
    }));
    timeline.play();
  }

  @FXML
  private void changeAction(MouseEvent event) {
    double value = slider.getValue();
    System.out.println();
    line.setStartX(value * Taskview.BLOCK_WIDTH);
    line.setEndX(value * Taskview.BLOCK_WIDTH);
    for (Node node : area.getChildren()) {
      if (node instanceof Taskview) {
        ((Taskview) node).setTime(value);
      }
    }
  }

  public void addTask(Task task) {
    Taskview view = new Taskview(task);
    double gap = 0;
    while (areColision(view)) {
      view.setGapy(gap += Taskview.BLOCK_HEIGHT);
    }
    area.getChildren().add(view);
  }

  public boolean areColision(Taskview taskline) {
    for (Node node : area.getChildren()) {
      Taskview child = (Taskview) node;
      Bounds area1 = child.getBounds();
      Bounds area2 = taskline.getBounds();
      boolean contains = area1.intersects(area2);
      if (contains) {
        return true;
      }
    }
    return false;
  }

}
