package Entities;

import Entities_states.Student_State;
import Regions.Bar;
import Regions.Table;
import Regions.GeneralRepo;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public class Student extends Thread {

    //Student stats
    private final Table table;
    private final Bar bar;
    private final int MCourses;
    private final int MaxStudents;
    private final int studentID;


    
    public Student(Table table, Bar bar, int MCourses, int MaxStudents, int id) {
        this.table = table;
        this.bar = bar;
        this.MCourses = MCourses;
        this.MaxStudents = MaxStudents;
        this.studentID = id;
    }

    @Override
    public void run() {
        try {
            WalkABit();
            int arrivalOrder;
            arrivalOrder = table.enter(studentID);
            table.readTheMenu(studentID); 
            if (arrivalOrder == 1) {
                while (!table.hasEverybodyChosen()) {
                    table.prepareTheOrder(studentID);
                }
                bar.callTheWaiter();
                table.describeTheOrder();
                table.joinTheTalk();
            } else {
                table.informCompanion(studentID);
            }

            for (int i = 0; i < MCourses; i++) {
                table.startEating(studentID);  
                table.endEating(studentID);
                if (table.hasEverybodyFinished(i)) {
                    if (i != (MCourses - 1)) {
                        bar.SignalTheWaiter();
                    }
                }
            }
            if (arrivalOrder == (MaxStudents)) {
                table.shouldHaveArrivedEarlier(studentID); 
                table.honorTheBill();
            }
            table.exit(studentID);
        } catch (IOException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void WalkABit() {
        try {
            Thread.sleep((long) ((Math.random()*1500)+500));    
        } catch (InterruptedException ex) {
        }

    }
}
