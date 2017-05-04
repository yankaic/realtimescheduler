package scheduler.tool;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import scheduler.gui.Taskview;

/**
 * Classe que faz a marcacao de tempo com inicio e duração. Isso alocado com uma
 * lista pode indicar o uso total do uso da cpu.
 *
 * @author yankaic
 */
public class CpuUsage {

  private final Timestamp time;
  private final int init;
  private int duration;
  private boolean isAlive;
  private int lastime;
  private final Rectangle rectangle;

  /**
   * Construtor da Classe. Inicia a marcação, recebendo com parametro o
   * timestamp geral Todas as operacoes sao realizadas de acordo o timestamp de
   * consumo e liberacao. Quando cpuUsage é criado, a duração já é iniciada com
   * 1, que significa que o timestamp que o objeto criado será consumido.
   *
   * @param time timestamp geral do sistema
   */
  public CpuUsage(Timestamp time) {
    this.time = time;
    this.init = time.now;
    this.isAlive = true;
    this.duration = 1;
    this.lastime = time.now;
    this.rectangle = new Rectangle(Taskview.BLOCK_WIDTH, 10, Color.GREEN);
    this.rectangle.setX(init * Taskview.BLOCK_WIDTH);
  }

  /**
   * Obtem o comprimento do cpu da tarefa.
   *
   * @return tempo em valor discreto.
   */
  public int getDuration() {
    return duration;
  }

  /**
   * Mostra a situação da cpu usage. Uma cpu usage pode estar fechada ou aberta.
   * Uma cpu usage só pode consumir quando seu estado ainda estiver aberta
   *
   * @return true - aberta<br>false - fechada
   */
  public boolean isAlive() {
    return isAlive;
  }

  /**
   * Funcao que consome o tempo na timestamp atual. Quando um timestamp já for
   * consumido, não poderá ser consumido de novo
   */
  public void consume() {
    if (lastime == time.now || !isAlive) {
      return;
    }
    duration++;
    lastime = time.now;
    rectangle.setWidth(duration * Taskview.BLOCK_WIDTH);
  }

  /**
   * Funcao que libera o uso do processador. este marcador finaliza a
   * continualização do uso do processador. Quando uma nova chamada de uso de
   * processador for necessaria, precisa criar um novo cpuUsage
   */
  public void release() {
    isAlive = false;
  }

  /**
   * Captura o inicio do uso da cpu.
   *
   * @return tempo absoluto discreto do inicio do processamento.
   */
  public int getInit() {
    return init;
  }

  @Override
  public String toString() {
    return init + " - " + (init + duration);
  }

  public Rectangle getView() {
    return rectangle;
  }

}
