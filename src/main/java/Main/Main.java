package Main;

import Entities.Chef;
import Entities.Student;
import Entities.Waiter;
import Regions.Bar;
import Regions.Kitchen;
import Regions.Table;
import Regions.GeneralRepo;
import java.io.IOException;
/**
 * Restaurant Problem
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public class Main {

    //TODO Colocar todos os prints em comentário
    public static void main(String[] args) throws IOException {
        try {
            //7 Students, 1 waiter, 1 chef and 3 courses
            int MaxStudents = 7;
            int rounds = 3;
            String filename = "src/main/java/Main/logger.txt";
            
            //Create regions
            GeneralRepo gr = new GeneralRepo(filename);
            Bar bar = new Bar(MaxStudents, gr);
            Kitchen kitchen = new Kitchen(bar, gr);
            Table table = new Table(MaxStudents, rounds, bar, kitchen, gr);
            
            //Create entities
            Chef chef = new Chef(kitchen, rounds, MaxStudents);
            Waiter waiter = new Waiter(kitchen, bar, table);
            Student[] students = new Student[MaxStudents];
            for (int id = 0; id < MaxStudents; id++) {
                students[id] = new Student(table, bar, gr, rounds, MaxStudents, id);
            }
            
            //Starts threads
            chef.start();
            waiter.start();
            for (int i = 0; i < MaxStudents; i++) {
                students[i].start();
            }
            
            //Waits for threads to finish
            chef.join();
            for (int i = 0; i < MaxStudents; i++) {
                students[i].join();
            }
            waiter.join();
            
            //Close FileWriter
            gr.closeWriter();

        } catch (InterruptedException ex) {
        }

    }
}
