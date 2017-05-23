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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import scheduler.gui.InstanceView;
import scheduler.model.Task;

/**
 * FXML Controller class
 *
 * @author yanka
 */
public class TaskGroup implements Initializable {

  private Slider slider;
  @FXML
  private Line markline;
  @FXML
  private Circle sliderSelector;
  @FXML
  private VBox tasklistview;
  @FXML
  private VBox labelBox;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
  }

  private void changeAction(MouseEvent event) {
//    double value = slider.getValue();
//    System.out.println();
//    line.setStartX(value * Taskview.BLOCK_WIDTH);
//    line.setEndX(value * Taskview.BLOCK_WIDTH);
//    for (Node node : area.getChildren()) {
//      if (node instanceof Taskview) {
//        ((Taskview) node).setTime(value);
//      }
//    }
  }

  public void addTask(Task task) {
    InstanceView view = new InstanceView(task);
    double gap = 0;
//    while (areColision(view)) {
//      view.setGapy(gap += Taskview.BLOCK_HEIGHT + 1);
//    }
//    System.out.println("gap: " + gap);
//    area.getChildren().add(view);
    addTaskBlock(view);
  }

  private void addTaskBlock(InstanceView view) {
    int padding = 10;

    AnchorPane card = null;
    for (Node node : tasklistview.getChildren()) {
      if (node instanceof AnchorPane) {
        AnchorPane cardInside = (AnchorPane) node;
        AnchorPane core = (AnchorPane) cardInside.getChildren().get(0);
        InstanceView first = (InstanceView) core.getChildren().get(0);
        if (first.getTask().getName().equals(view.getTask().getName())) {
          card = cardInside;
          break;
        }
      }
    }

    if (card == null) {
      card = new AnchorPane();
      card.setStyle("-fx-background-color:white;"
              + "-fx-background-radius: 2");
      card.setEffect(new DropShadow(5, new Color(0, 0, 0, 0.5)));
      card.setPadding(new Insets(padding));
      AnchorPane corecard = new AnchorPane();
      corecard.getChildren().add(view);
      card.getChildren().add(corecard);
      corecard.setLayoutX(padding);
      corecard.setLayoutY(padding + 10);
      tasklistview.getChildren().add(card);
      addLabel(card);
    }
    else {
      double gap = 0;
      AnchorPane core = (AnchorPane) card.getChildren().get(0);
      while (areColision(core, view)) {
        view.setGapy(gap += InstanceView.BLOCK_HEIGHT + 1);
      }
      core.getChildren().add(view);
      resizeLabel(card);
    }

  }

  public boolean areColision(AnchorPane area, InstanceView view) {
    for (Node node : area.getChildren()) {
      InstanceView child = (InstanceView) node;
      Bounds area1 = child.getBounds();
      Bounds area2 = view.getBounds();
      boolean contains = area1.intersects(area2);
      if (contains) {
        return true;
      }
    }
    return false;
  }

  @FXML
  private void dragmouse(MouseEvent event) {
//    double x = sliderSelector.getTranslateX() + event.getX();
//    if (x < 20 || x > area.getWidth() + 20) {
//      return;
//    }
//    sliderSelector.setTranslateX(x);
//    markline.setTranslateX(x + 5);
//
//    for (Node node : area.getChildren()) {
//      if (node instanceof Taskview) {
//        ((Taskview) node).setTime((x - 20) / (double) Taskview.BLOCK_WIDTH);
//      }
//    }

  }

  private void addLabel(AnchorPane card) {
    InstanceView first = (InstanceView) ((AnchorPane) card.getChildren().get(0)).getChildren().get(0);
    String name = first.getTask().getName();

    BorderPane pane = new BorderPane();
//    pane.setStyle("-fx-background-color:white");
    Text text = new Text(name);
    text.setFont(new Font(24));
    pane.setCenter(text);
    pane.setPrefHeight(card.getHeight());
    labelBox.getChildren().add(pane);
  }

  private void resizeLabel(AnchorPane card) {
    InstanceView first = (InstanceView) ((AnchorPane) card.getChildren().get(0)).getChildren().get(0);
    String name = first.getTask().getName();
    
    for (Node node : labelBox.getChildren()) {
      Text text = (Text) ((BorderPane)node).getChildren().get(0);
      if(text.getText().equals(name)){
        double height = card.getBoundsInLocal().getHeight()+10;
        System.out.println("height: "+height);
        ((BorderPane)node).setPrefHeight(height);
      }
    }
  }

}
