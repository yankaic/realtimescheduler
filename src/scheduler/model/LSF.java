package scheduler.model;

/**
 *
 * @author yanka
 */
public class LSF extends Scheduler {

  @Override
  public void allocate(Task task) {
    int index = 0;
    for (Task insideTask : readyQueue) {
      int laxite = task.getAbsoluteDeadline() - time.now - (task.getComputation() - task.getTotalCPU());
      int laxiteInside = insideTask.getAbsoluteDeadline() - time.now - (insideTask.getComputation() - insideTask.getTotalCPU());

      boolean isPriority = laxite < laxiteInside
              || laxite == laxiteInside
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
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
