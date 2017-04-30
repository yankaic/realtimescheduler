package scheduler.tool;

import java.util.LinkedList;
import java.util.List;
import scheduler.model.Task;

/**
 * Fila Cronológica. Fila encadeada de Tarefas que são ordenadas de acordo com o
 * tempo de inicio. Permite a remoção de itens quando o timestamp estiver
 * configurado maior ou igual ao inicio da tarefa.
 *
 * @author yanka
 */
public class ChronologicalQueue {

  public final List<Task> queue;
  private final Timestamp time;

  /**
   * Construtor. Inicia uma fila encadeada e recebe um marcador de tempo externo
   *
   * @param time marcador de tempo externo
   */
  public ChronologicalQueue(Timestamp time) {
    this.queue = new LinkedList<>();
    this.time = time;
  }

  /**
   * Enfilar tarefa. ordena de acordo com seu tempo de inicio.
   *
   * @param task tarefa a ser ordenada
   */
  public void enqueue(Task task) {
    int index = 0;
    for (Task insideTask : queue) {
      boolean insertHere = task.getInitTime() < insideTask.getInitTime();
      if (insertHere) {
        break;
      }
      index++;
    }
    queue.add(index, task);
  }

  /**
   * Remover uma tarefa. Remove um item que tenha o tempo de inicio menor ou
   * igual ao timestamp atual.
   *
   * @return Retorna o item removido que esteja no timestamp correto.
   */
  public Task dequeue() {
    if (isEmpty()) {
      return null;
    }
    return queue.remove(0);
  }

  /**
   * Infoma se existe algum elemento cujo tempo de computação seja menor que o
   * timestamp atual.
   *
   * @return true - lista vazia no timestamp <br> false - não está vazia
   */
  public boolean isEmpty() {
    if (queue.isEmpty()) {
      return true;
    }
    return queue.get(0).getInitTime() > time.now;
  }

  public void clear() {
    queue.clear();
  }

}
