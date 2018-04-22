/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Regions;

import Entities_states.Chef_State;
import static Entities_states.Chef_State.WFO;
import Entities_states.Student_State;
import static Entities_states.Student_State.GTR;
import Entities_states.Waiter_State;
import static Entities_states.Waiter_State.ATS;
import Semaphore.Semaphore;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public class GeneralRepo {

    private Chef_State chefstate;
    private Waiter_State waiterstate;
    private Student_State[] studentstates;
    private int courseCounter;
    private Semaphore access;
    private String filename;
    private PrintWriter writer;

    BufferedWriter bw = null;
    FileWriter fw = null;

    public GeneralRepo(String filename) throws IOException {
        waiterstate = ATS;
        chefstate = WFO;
        studentstates = new Student_State[7];
        for (Student_State studentstate : studentstates) {
            studentstate = GTR;
        }
        this.filename = filename;
        fw = new FileWriter(filename);
        bw = new BufferedWriter(fw);
        bw.append("  CHEF         WAITER      ST1        ST2        ST3        ST4        ST5        ST6        ST7\n");
    }

    public void appendLine() throws IOException {
        bw.append("  " + chefstate + "          " + waiterstate + "         " + studentstates[0] + "        " + studentstates[1] + "        " + studentstates[2] + "        " + studentstates[3] + "        " + studentstates[4] + "        " + studentstates[5] + "        " + studentstates[6] + "\n");
    }

    public void updateChefState(Chef_State newstate) throws IOException {
        chefstate = newstate;
        appendLine();

    }

    public void updateWaiterState(Waiter_State newstate) throws IOException {
        waiterstate = newstate;
        appendLine();

    }

    public void updateStudentState(Student_State newstate, int studentID) throws IOException {
        studentstates[studentID] = newstate;
        appendLine();

    }

    public void updateCourse() throws IOException {
        courseCounter = 1;
        appendLine();
    }

    @Override
    public String toString() {
        return null;
    }

    public void closeWriter() throws IOException {
        bw.close();
    }
}
