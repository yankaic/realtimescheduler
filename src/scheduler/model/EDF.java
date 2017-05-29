package scheduler.model;

/**
 *
 * @author yanka
 */
public class EDF extends Scheduler {

  @Override
  public void allocate(Task task) {
    int index = 0;
    for (Task insideTask : readyQueue) {
      boolean isPriority = task.getAbsoluteDeadline()< insideTask.getAbsoluteDeadline()
              || task.getAbsoluteDeadline()== insideTask.getAbsoluteDeadline()
              && task.getInitTime() < insideTask.getInitTime();
      if (isPriority) {
        break;
      }
      index++;
    }
    readyQueue.add(index, task);
  }

  @Override
  public boolean isWorking() {
//    double use = 0.0;
//    for (Task task : original) {
//      
//    }
    return false;
  }

}
