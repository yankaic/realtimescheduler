package scheduler.gui;

import java.util.LinkedList;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import scheduler.model.Task;
import scheduler.tool.CpuUsage;

/**
 *
 * @author yanka
 */
public class Taskview extends Pane {

  public static final int BLOCK_SIZE = 50;

  private Task task;

  private List<Rectangle> consumed;

  private List<Rectangle> unconsumed;

  private Rectangle active;
  private Rectangle half;
  private Rectangle remain;
  private double time;
  private Line line;
  private Line initline;
  private Line deadline;

  /**
   *
   * @param task
   */
  public Taskview(Task task) {
    super();
    consumed = new LinkedList<>();
    unconsumed = new LinkedList<>();
    this.task = task;

    line = new Line(0, 7, 0, 7);
    initline = new Line(0, 0, 0, 14);
    deadline = new Line(0, 0, 0, 14);    
    drawLines();
    getChildren().add(line);
    getChildren().add(initline);
    getChildren().add(deadline);

    for (CpuUsage use : task.getConsume()) {
      unconsumed.add(use.getView());
      getChildren().add(use.getView());
      use.getView().setY(2);
    }

    half = new Rectangle(0, 10, Color.GREEN);
    half.setY(2);
    remain = new Rectangle(getRemain(), 10, Color.GRAY);
    remain.setY(2);
    getChildren().add(half);
    getChildren().add(remain);

  }

  /**
   *
   * @param seconds
   */
  public void setTime(double seconds) {
    this.time = seconds;
    task.getGlobalTime().now = (int) seconds;
    if (active != null) {
      unconsumed.add(active);
      active = null;
    }
    for (Object obj : consumed.toArray()) {
      Rectangle rect = (Rectangle) obj;
      if (rect.getX() + rect.getWidth() <= seconds * BLOCK_SIZE) {
        continue;
      }
      else if (rect.getX() >= seconds * BLOCK_SIZE) {
        unconsumed.add(rect);
      }
      else {
        active = rect;
      }
      consumed.remove(rect);
    }

    for (Object obj : unconsumed.toArray()) {
      Rectangle rect = (Rectangle) obj;
      if (rect.getX() >= seconds * BLOCK_SIZE) {
        continue;
      }
      else if (rect.getX() + rect.getWidth() <= seconds * BLOCK_SIZE) {
        consumed.add(rect);
      }
      else {
        active = rect;
      }
      unconsumed.remove(rect);
    }
    paint();
  }

  /**
   *
   */
  private void paint() {
    if (active != null) {
      half.setX(active.getX());
      half.setWidth((time - active.getX() / BLOCK_SIZE) * BLOCK_SIZE);
    }
    else {
      half.setWidth(0);
    }
    for (Rectangle rectangle : consumed) {
      rectangle.setVisible(true);
    }
    for (Rectangle rectangle : unconsumed) {
      rectangle.setVisible(false);
    }
    remain.setX(time * BLOCK_SIZE);
    remain.setWidth(getRemain() * BLOCK_SIZE - half.getWidth());
  }

  private int getRemain() {
    return task.getComputation() - task.getTotalCPU();
  }

  private void drawLines() {
    int end = task.getAbsoluteDeadline() * BLOCK_SIZE;
    line.setStartX(task.getInitTime() * BLOCK_SIZE);
    line.setEndX(end);
    deadline.setEndX(end);
    deadline.setStartX(end);
  }

}
