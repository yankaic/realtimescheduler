/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import scheduler.model.RateMonotonic;
import scheduler.model.Scheduler;
import scheduler.model.Task;
import java.util.Arrays;
import scheduler.gui.InstanceView;

/**
 *
 * @author yanka
 */
public class SchedulerTest {

  public static void main(String[] args) {
    Scheduler scheduler = new RateMonotonic();
    scheduler.add(new Task("A", 0, 2, 5, 5));
    scheduler.add(new Task("B", 0, 2, 3, 3));
    scheduler.add(new Task("C", 0, 1, 4, 4));
//    Task[] tasks = scheduler.compute();
//    System.out.println(Arrays.toString(tasks));
//    InstanceView view = new InstanceView(tasks[2]);
//    System.out.println("tamanho: "+view.getBoundsInLocal());
  }

}
