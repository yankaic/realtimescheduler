package scheduler.model;

import scheduler.tool.ChronologicalQueue;
import scheduler.tool.Timestamp;
import scheduler.gui.SchedulerView;
import java.util.LinkedList;
import java.util.List;

/**
 * Escalonador de processos de um sistema de tempo real.
 *
 * @author yankaic
 */
public abstract class Scheduler {

  protected final List<Task> readyQueue;
  private Task active;
  protected final List<Task> originalTasks;
  private final ChronologicalQueue futureTasks;
  private final List<Task> doneList;
  private SchedulerView schedulerView;
  protected final Timestamp time;

  /**
   * Construtor. Inicia o tempo do sistema. Todas as atividades do sistema devem
   * seguir este tempo
   */
  public Scheduler() {
    this.time = new Timestamp();
    this.originalTasks = new LinkedList<>();
    this.doneList = new LinkedList<>();
    this.futureTasks = new ChronologicalQueue(time);
    this.readyQueue = new LinkedList<>();
  }

  /**
   * Adiciona uma tarefa no escalonador. Essa tarefa deve ser a instancia
   * inicial da tarefa, as demais instancia serão alocadas automaticamente.
   *
   * @param task - Tarefa base
   */
  public void add(Task task) {
    task.setGlobalTime(time);
    originalTasks.add(task);
  }

  /**
   * Gerar ciclos de tarefa. Funcao que gera todas as instancias de uma tarefa e
   * insere na fila de tarefas futuras
   */
  private void generateCycles(Task task) {
    Task instance;
    for (int i = task.getInitTime(); i < getMMC(); i += task.getPeriod()) {
      instance = new Task(
              task.getName(),
              i,
              task.getComputation(),
              task.getPeriod(),
              task.getDeadline());
      instance.setGlobalTime(time);
      futureTasks.enqueue(instance);
    }
//    System.out.println(Arrays.toString(futureTasks.queue.toArray()));
  }

  /**
   * Chamar Kernel. Funcao que faz a ativação do kernel e aloca as tarefas na
   * ordem correta
   */
  private void callKernel() {
    preempt();
    loadtask();
  }

  /**
   * Preemptar tarefa ativa. Funcao que remove uma tarefa da ativação e verifica
   * qual é a mais adequada para usar o processador *
   */
  private void preempt() {
    if (active != null) {
      active.release();
      if (active.isDone()) {
        doneList.add(active);
      }
      else {
        allocate(active);
      }
    }
    active = null;
  }

  /**
   * Carrega Tarefa. Funcao que ativa uma tarefa ao cpu.
   */
  private void loadtask() {
    if (!readyQueue.isEmpty()) {
      active = readyQueue.remove(0);
      active.consume();
    }
  }

  /**
   * Alocar Tarefa. Funcao abstrada que ordena as tarefas na fila de prontos.
   * Esta funcao é abstrada pois pode ser implementada por várias heurísticas
   *
   * @param task
   */
  public abstract void allocate(Task task);

  public abstract boolean isWorking();

  /**
   * Calcular o MMC. Funcao que calcula o mmc dos periodos das tarefas base. O
   * tempo de período de cada tarefa será analisado para gerar um valor em que
   * as tarefas começarão a repetir suas interações, ou seja, à partir de um
   * momento, as atividades serão semelhantes à origem dos tempos.
   *
   * @return resultado
   */
  public int getMMC() {
    int greater = 0;
    boolean searching = true;
    for (Task task : originalTasks) {
      if (task.getPeriod() > greater) {
        greater = task.getPeriod();
      }
    }
    int mmc = 0;
    while (searching) {
      mmc += greater;
      searching = false;
      for (Task task : originalTasks) {
        if (mmc % task.getPeriod() != 0) {
          searching = true;
          break;
        }
      }
    }
    return mmc;
  }

  /**
   * Computar. Funcao que todo o processo de alocação, repete as instancias e
   * mostra como ficou o escalonamento.
   *
   * @return Lista contendo todas as tarefas escalonadas, inclusive instancias.
   */
  public List<Task> compute() {
    //limpando todas as listas
    futureTasks.clear();
    readyQueue.clear();
    doneList.clear();
    active = null;

    //criando todas as instancias
    for (Task task : originalTasks) {
      generateCycles(task);
    }

    for (time.now = 0; time.now < getMMC(); time.now++) {
      boolean hasnew = !futureTasks.isEmpty();
      boolean taskCompleted = active != null && active.isDone();

      while (!futureTasks.isEmpty()) {
        allocate(futureTasks.dequeue());
      }

      //chama o kernel quando adicionar tarefas ou quando finalizar alguma
      if (hasnew || taskCompleted) {
        callKernel();
      }

      if (active != null && !active.isDone()) {
        active.consume();
      }
    }

    if (active != null) {
      doneList.add(active);
    }
    while (!readyQueue.isEmpty()) {
      doneList.add(readyQueue.remove(0));
    }

    Task list[] = new Task[doneList.size()];

    for (int i = 0; i < list.length; i++) {
      list[i] = doneList.get(i);
    }

    return new LinkedList(doneList);
  }

  /**
   * Obter Visualização. Mostra em elementos visuais como ficou o escalonamento.
   *
   * @return Elemento Visual
   */
  public SchedulerView getView() {
    if (schedulerView == null) {
      schedulerView = new SchedulerView(doneList);
    }
    return schedulerView;
  }

  /**
   * Obter tempo. Tempo global para todas as tarefas e serviços do sistema. Todo
   * o escalonamento será realizado de acordo com este tempo global.
   *
   * @return Tempo Global
   */
  public Timestamp getTime() {
    return time;
  }

  public void remove(Task task) {
    originalTasks.remove(task);
  }

}
