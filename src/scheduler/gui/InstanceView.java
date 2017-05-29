package scheduler.gui;

import java.util.LinkedList;
import java.util.List;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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
public class InstanceView extends Pane {

  public static final int BLOCK_HEIGHT = 14;
  public static final int BLOCK_WIDTH = 40;

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
  private double gapY;
  private Rectangle area;

  /**
   *
   * @param task
   */
  public InstanceView(Task task) {
    super();
    consumed = new LinkedList<>();
    unconsumed = new LinkedList<>();
    this.task = task;

    line = new Line(0, BLOCK_HEIGHT / 2, 0, BLOCK_HEIGHT / 2);
    initline = new Line(0, 0, 0, BLOCK_HEIGHT);
    deadline = new Line(0, 0, 0, BLOCK_HEIGHT);
    drawLines();
    getChildren().add(line);
    getChildren().add(initline);
    getChildren().add(deadline);

    for (CpuUsage use : task.getConsume()) {
      unconsumed.add(use.getView());
      getChildren().add(use.getView());
      use.getView().setY(2);

      if (use.getInit() >= task.getAbsoluteDeadline()) {
        use.getView().setFill(Color.RED);
      }
    }

    half = new Rectangle(0, 10, Color.GREEN);
    half.setY(2);
    remain = new Rectangle(getRemain(), 10, Color.GRAY);
    remain.setY(2);
    getChildren().add(half);
    getChildren().add(remain);
//    setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
    area = new Rectangle(0, 0);
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
      if (rect.getX() + rect.getWidth() <= seconds * BLOCK_WIDTH) {
        continue;
      }
      else if (rect.getX() >= seconds * BLOCK_WIDTH) {
        unconsumed.add(rect);
      }
      else {
        active = rect;

      }
      consumed.remove(rect);
    }

    for (Object obj : unconsumed.toArray()) {
      Rectangle rect = (Rectangle) obj;
      if (rect.getX() >= seconds * BLOCK_WIDTH) {
        continue;
      }
      else if (rect.getX() + rect.getWidth() <= seconds * BLOCK_WIDTH) {
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
      half.setWidth((time - active.getX() / BLOCK_WIDTH) * BLOCK_WIDTH);
      half.setY(gapY + 2);

      if (task.isFail()) {
        half.setFill(Color.RED);
      }
      else {
        half.setFill(Color.GREEN);
      }
    }
    else {
      half.setWidth(0);
    }
    for (Rectangle rectangle : consumed) {
      rectangle.setVisible(true);
      rectangle.setY(gapY + 2);
    }
    for (Rectangle rectangle : unconsumed) {
      rectangle.setVisible(false);
    }
    
   
    remain.setVisible(time > task.getInitTime());
    remain.setX(time * BLOCK_WIDTH);
    remain.setY(gapY + 2);
    remain.setWidth(getRemain() * BLOCK_WIDTH - half.getWidth() % BLOCK_WIDTH);
  }

  private int getRemain() {
    return task.getComputation() - task.getTotalCPU();
  }

  private void drawLines() {
    int end = task.getAbsoluteDeadline() * BLOCK_WIDTH;
    int start = task.getInitTime() * BLOCK_WIDTH;

    line.setStartX(start);
    line.setEndX(end);
    line.setStartY(gapY + BLOCK_HEIGHT / 2);
    line.setEndY(gapY + BLOCK_HEIGHT / 2);

    deadline.setStartX(end);
    deadline.setEndX(end);
    deadline.setStartY(gapY);
    deadline.setEndY(gapY + BLOCK_HEIGHT);

    initline.setStartX(start);
    initline.setEndX(start);
    initline.setStartY(gapY);
    initline.setEndY(gapY + BLOCK_HEIGHT);
  }

  public void setGapy(double y) {
    this.gapY = y;
    drawLines();
    paint();
  }

  public Task getTask() {
    return task;
  }

  public Bounds getBounds() {
    double width = deadline.getEndX();

    for (Node node : getChildren()) {
      if (node instanceof Rectangle) {
        Rectangle rec = (Rectangle) node;
        if (rec.getX() + rec.getWidth() > width) {
          width = rec.getX() + rec.getWidth();
        }
      }
    }

    Bounds bounds = new BoundingBox(line.getStartX() + 1, initline.getStartY(),
            width - line.getStartX() - 1, line.getEndY() - line.getStartY());
    area.setX(bounds.getMinX());
    area.setY(bounds.getMinY());
    area.setWidth(width);
    area.setHeight(line.getEndY());

    return bounds;
  }

}
