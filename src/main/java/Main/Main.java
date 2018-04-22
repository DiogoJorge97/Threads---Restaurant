/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Entities.Chef;
import Entities.Student;
import Entities.Waiter;
import Regions.Bar;
import Regions.Kitchen;
import Regions.Table;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Restaurant Versão Concurrente
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public class Main {
    //TODO Check all authors
    public static void main(String[] args) {
        try {
            //7 Students, 1 waiter, 1 chef and 3 courses
            int MaxStudents = 7;
            int rounds = 3;

            Bar bar = new Bar(MaxStudents);
            Kitchen kitchen = new Kitchen(bar);
            Table table = new Table(MaxStudents, rounds, bar, kitchen);

            Chef chef = new Chef(kitchen, bar, rounds, MaxStudents);
            Waiter waiter = new Waiter(kitchen, bar, table);
            Student[] students = new Student[MaxStudents];
            for (int id = 0; id < MaxStudents; id++) {
                students[id] = new Student(table, bar, rounds, MaxStudents, id);
            }
            chef.start();
            //System.out.println("chef " + chef.getName());
            waiter.start();
            //System.out.println("waiter " + waiter.getName());


            for (int i = 0; i < MaxStudents; i++) {
                students[i].start();
                Thread.sleep(1000);
                //System.out.println("student[" + i + "] "  + students[i].getName());
            }
            
            chef.join();
            waiter.join();
            for (int i = 0; i < MaxStudents; i++) {
                students[i].join();
            }

        } catch (InterruptedException ex) {
        }

    }
}
