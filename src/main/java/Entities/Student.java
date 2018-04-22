/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import Entities_states.Student_State;
import Regions.Bar;
import Regions.Table;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public class Student extends Thread {

    /*
        Student actions' regions
        Going to the restaurant - None
        Taking a seat at the table - table
        Selecting the courses - table
        Organizing the order - table
        Chatting with companions - table
        Enjoying the meal - table
        Paying the bill - table
        Going home - table
     */
    //Student stats
    private Student_State state;
    private final Table table;
    private final Bar bar;
    private final int MCourses;
    private final int MaxStudents;
    private int studentID;

    public Student(Table table, Bar bar, int MCourses, int MaxStudents, int id) {
        this.table = table;
        this.bar = bar;
        this.MCourses = MCourses;
        this.MaxStudents = MaxStudents;
        this.studentID = id;

    }

    //Teacher Method
    @Override
    public void run() {
        WalkABit();
        int arrivalOrder;
        arrivalOrder = table.enter();
        table.readTheMenu(studentID); //TODO delete entry variable, only used for print
        if (arrivalOrder == 1) {
            while (!table.hasEverybodyChosen()) {
                table.prepareTheOrder();
            }
            bar.callTheWaiter();
            table.describeTheOrder();
            table.joinTheTalk();
        } else {
            table.informCompanion();
        }

        for (int i = 0; i < MCourses; i++) {
            table.startEating(studentID);   //TODO delete entry variable, only used for print
            table.endEating(studentID); //TODO delete entry variable, only used for print
            if (table.hasEverybodyFinished(i)) {
//            if (table.hasEverybodyFinished()) {    
                if (i != (MCourses - 1)) {
                    bar.SignalTheWaiter();
                }
            }
        }
        if (arrivalOrder == (MaxStudents)) {
            table.shouldHaveArrivedEarlier(studentID);  //TODO delete entry variable, only used for print
            table.honorTheBill();
        }
        table.exit(studentID);

    }

    private void WalkABit() {
        try {
            Thread.sleep(1000);    //TODO make random
        } catch (InterruptedException ex) {
        }

    }
//TODO check if needed
//    public void setStudentID(int studentID) {
//        this.studentID = studentID;
//    }
//
//    public void setStudentState(Student_State newstate) {
//        state = newstate;
//    }
//
}
