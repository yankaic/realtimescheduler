package scheduler.model;

/**
 *
 * @author yanka
 */
public class RateMonotonic extends Scheduler {

  public RateMonotonic() {
    super();
  }

  /**
   * Alocar Tarefa. Funcao abstrada implementada pela heuristica de taxa
   * Monotônica. Esta heurística tem como objetivo dar prioridades à tarefas que
   * possuam menor periodo.
   *
   * @param task tarefa pronta
   */
  @Override
  public void allocate(Task task) {
    int index = 0;
    for (Task insideTask : readyQueue) {
      boolean isPriority = task.getPeriod() < insideTask.getPeriod()
              || task.getPeriod() == insideTask.getPeriod()
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
    double use = 0.0;
    int n = originalTasks.size();
    for (Task task : originalTasks) {
      use += (double) task.getComputation() / (double) task.getPeriod();
    }
    return use <= n * (Math.pow(1, 1.0 / (double) n) - 1);
  }

}
