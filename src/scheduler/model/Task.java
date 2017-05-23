package scheduler.model;

import scheduler.tool.Timestamp;
import scheduler.tool.CpuUsage;
import scheduler.gui.InstanceView;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author yanka
 */
public class Task {

  private final String name;
  private int init;
  private int deadline;
  private int period;
  private int computation;
  private InstanceView view;
  private final List<CpuUsage> usages;
  private Timestamp time;

  /**
   * Criando uma nova Tarefa de Tempo Real.
   *
   * @param name Nome da Tarefa
   * @param init Tempo que a tarefa é colocada na fila de prontos
   * @param computation Tempo que a tarefa gasta para concluir sua execução
   * @param period Tempo que a tarefa é chamada de novo
   * @param deadline (deadline relativo) Tempo final para execução da tarefa
   */
  public Task(String name, int init, int computation, int period, int deadline) {
    this.name = name;
    this.init = init;
    this.computation = computation;
    this.period = period;
    this.deadline = deadline;
    this.usages = new LinkedList<>();
  }

  /**
   * Funcao que calcula a quantidade de tempo que a tarefa usou do processador,
   * com base no timestamp atual.
   *
   * @return tempo gasto na cpu
   */
  public int getTotalCPU() {
    int total = 0;
    for (CpuUsage usage : usages) {
      if (time.now <= usage.getInit()) {
        break;
      }
      total += Math.min(usage.getDuration(), time.now - usage.getInit());
    }
    return total;
  }

  /**
   * Verifica se a tarefa já realizou toda a sua atividade de acordo com o o
   * timestamp atual.
   *
   * @return
   */
  public boolean isDone() {
    return getTotalCPU() == computation;
  }

  /**
   * Função que calcula o deadline absoluto com base no tempo inicial e no
   * deadline relativo.
   *
   * @return deadline absoluto
   */
  public int getAbsoluteDeadline() {
    return init + deadline;
  }

  /**
   * Funcao que indica se a tarefa ultrapassou o deadline sem ter terminado com
   * sua execução. Essa operação toma com base o timestamp atual
   *
   * @return true - se falhou <br> false - não falhou
   */
  public boolean isFail() {
    return time.now > getAbsoluteDeadline() && !isDone();
  }

  /**
   * Funcao para indicar que a tarefa esta em execução no timestamp atual. A
   * tarefa fica em consumo até que a chamada de liberação seja acionada.
   */
  public void consume() {
    CpuUsage lastUse;
    if (usages.isEmpty()) {
      lastUse = new CpuUsage(time);
      lastUse.consume();
      usages.add(lastUse);
      return;
    }
    lastUse = usages.get(usages.size() - 1);
    if (lastUse != null && lastUse.isAlive()) {
      lastUse.consume();
    }
    else {
      lastUse = new CpuUsage(time);
      lastUse.consume();
      usages.add(lastUse);
    }
  }

  /**
   * Funcao que libera o uso do processador de acordo com o timestamp e liga o
   * registro de uso da cpu com o mais antigo se ainda houver a marcacao de
   * ativo.
   */
  public void release() {
    CpuUsage lastUse = usages.get(usages.size() - 1);
    if (lastUse.isAlive()) {
      lastUse.release();
    }
  }

  /**
   * Limpa todo o historico de uso da cpu.
   */
  public void releaseAll() {
    usages.clear();
  }

  public int getInitTime() {
    return init;
  }

  public void setInitTime(int time) {
    this.init = time;
  }

  public int getDeadline() {
    return deadline;
  }

  public void setDeadline(int deadline) {
    this.deadline = deadline;
  }

  public int getPeriod() {
    return period;
  }

  public void setPeriod(int period) {
    this.period = period;
  }

  public int getComputation() {
    return computation;
  }

  public void setComputation(int computation) {
    this.computation = computation;
  }

  public String getName() {
    return name;
  }

  public void setGlobalTime(Timestamp time) {
    this.time = time;
  }

  @Override
  public String toString() {
    return '[' + name + ", " + init + ", " + computation + ", " + period + ", " + deadline + "]" + " = " + Arrays.toString(usages.toArray()) + "\n";
  }

  public List<CpuUsage> getConsume() {
    return usages;
  }
  
  public Timestamp getGlobalTime(){
    return time;
  }

}
