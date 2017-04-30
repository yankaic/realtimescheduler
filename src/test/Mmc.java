/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import scheduler.model.RateMonotonic;
import scheduler.model.Scheduler;
import scheduler.model.Task;

/**
 *
 * @author yanka
 */
public class Mmc {

  public static void main(String[] args) {
    Scheduler scheduler = new RateMonotonic();
    scheduler.add(new Task("A", 0, 0, 7, 0));
//    scheduler.add(new Task("A", 0, 0, 5, 0));
    scheduler.add(new Task("A", 0, 0, 4, 0));
    scheduler.add(new Task("A", 0, 0, 5, 0));
    System.out.println(scheduler.getMMC());
  }

}
