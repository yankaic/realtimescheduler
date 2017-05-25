/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.gui.controller;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import scheduler.gui.InstanceView;
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
  private VBox tasklistview;
  @FXML
  private VBox labelBox;

  private List<Task> tasks;
  private List<InstanceView> instances;
  Scheduler scheduler;
  @FXML
  private TextField periodField;
  @FXML
  private TextField computeField;
  @FXML
  private TextField deadField;
  @FXML
  private Button addButton;

  private char lastLetter = 'A';
  @FXML
  private Circle ball;
  @FXML
  private Line line;
  @FXML
  private ScrollPane scrollpane;
  private TranslateTransition time;
  @FXML
  private Button playButton;
  @FXML
  private SVGPath pauseIcon;
  @FXML
  private SVGPath playIcon;
  @FXML
  private SVGPath winIcon;
  @FXML
  private SVGPath errorIcon;
  @FXML
  private Text statusLabel;
  @FXML
  private HBox statusPane;
  @FXML
  private Text timeLabel;
  
  

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    tasks = new LinkedList<>();
    scheduler = new RateMonotonic();
    instances = new LinkedList<>();

    scrollpane.heightProperty().addListener(e -> {

      line.setEndY(scrollpane.getHeight() - 20);

    });

    line.translateXProperty().addListener(e -> {
      double x = line.getTranslateX();
      ball.setTranslateX(x - ball.getRadius());
      for (InstanceView instance : instances) {
        instance.setTime((x - 20) / (double) InstanceView.BLOCK_WIDTH);
      }

      x -= 20;
      x /= InstanceView.BLOCK_WIDTH;
      timeLabel.setText("Tempo: " + String.format("%1$.1f", x)+" segundos");
    });

    time = new TranslateTransition(Duration.ZERO, line);
    time.setInterpolator(Interpolator.LINEAR);
    time.setFromX(20);

    time.statusProperty().addListener(e -> {
      if (time.getStatus() == Status.RUNNING) {
        playButton.setGraphic(pauseIcon);
      }
      else {
        playButton.setGraphic(playIcon);
      }
    });

//    addTask(new Task(lastLetter++ + "", 0, 2, 5, 5));
//    addTask(new Task(lastLetter++ + "", 0, 1, 3, 4));
//    addTask(new Task(lastLetter++ + "", 0, 2, 6, 6));
  }

  @FXML
  private void changeAction(MouseEvent event) {
    double x = line.getTranslateX() + event.getX();
    if (x >= 20 && x < scheduler.getMMC() * InstanceView.BLOCK_WIDTH + 20) {
      line.setTranslateX(x);
    }

    time.stop();

  }

  private void addTask(Task task) {
    clearAll();
    scheduler.add(task);
    boolean isWork = true;
    for (Task instance : scheduler.compute()) {
      addInstance(instance);
      isWork = isWork && !instance.isFail();
      System.out.println("falhou: " + instance.isFail());
    }
    for (InstanceView instance : instances) {
      instance.setTime(scheduler.getMMC());
    }

    if (isWork) {
      statusLabel.setText("Escalonável");
      statusLabel.setFill(Color.GREEN);
      statusPane.getChildren().remove(winIcon);
      statusPane.getChildren().add(1, winIcon);
      errorIcon.setVisible(false);
      winIcon.setVisible(true);
    }
    else {
      statusLabel.setText("Não Escalonável");
      statusLabel.setFill(Color.RED);
      statusPane.getChildren().remove(errorIcon);
      statusPane.getChildren().add(1, errorIcon);
      errorIcon.setVisible(true);
      winIcon.setVisible(false);
    }

    time.setFromX(20);
    time.stop();
    time.setDuration(Duration.seconds(scheduler.getMMC()));
    time.setToX(scheduler.getMMC() * InstanceView.BLOCK_WIDTH + 20);
    time.play();
  }

  @FXML
  public void addAction() {
    int computation = Integer.parseInt(computeField.getText());
    int period = Integer.parseInt(periodField.getText());
    int dead = Integer.parseInt(deadField.getText());

    Task task = new Task(lastLetter++ + "", 0, computation, period, dead);
    addTask(task);
  }

  private void clearAll() {
    tasklistview.getChildren().clear();
    labelBox.getChildren().clear();
    instances.clear();
  }

  private void addInstance(Task task) {
    InstanceView view = new InstanceView(task);
    instances.add(view);
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
        view.setGapy(gap += InstanceView.BLOCK_HEIGHT + 4);
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
      Text text = (Text) ((BorderPane) node).getChildren().get(0);
      if (text.getText().equals(name)) {
        double height = card.getBoundsInLocal().getHeight() + 10;
        System.out.println("height: " + height);
        ((BorderPane) node).setPrefHeight(height);
      }
    }
  }

  @FXML
  private void keyAction(KeyEvent event) {
  }

  @FXML
  private void playAction(ActionEvent event) {
    if (time.getStatus() == Status.RUNNING) {
      time.stop();
    }
    else {
      time.setFromX(line.getTranslateX());
      double remain = scheduler.getMMC() - line.getTranslateX() / InstanceView.BLOCK_WIDTH;
      time.setDuration(Duration.seconds(remain));
      time.play();
    }
  }

  @FXML
  private void stopAction(ActionEvent event) {
    time.stop();
    line.setTranslateX(20);
  }

}
