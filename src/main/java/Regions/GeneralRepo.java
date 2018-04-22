package Regions;

import Entities_states.Chef_State;
import static Entities_states.Chef_State.WFO;
import Entities_states.Student_State;
import static Entities_states.Student_State.GTR;
import Entities_states.Waiter_State;
import static Entities_states.Waiter_State.ATS;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public final class GeneralRepo {

    private Chef_State chefstate;
    private Waiter_State waiterstate;
    private int courseCounter;
    private final String filename;
    private PrintWriter writer;
    Student_State[] studentstates = {GTR,GTR,GTR,GTR,GTR,GTR,GTR};

    BufferedWriter bw = null;
    FileWriter fw = null;

    public GeneralRepo(String filename) throws IOException {
        //inicial states of entities
        waiterstate = ATS;
        chefstate = WFO;

        this.courseCounter = 0;
        this.filename = filename;
        
        initFile(filename);
    }
    
    /**
     * Initiates fileWriter 
     * @param filename
     * @throws IOException 
     */
    public void initFile(String filename) throws IOException {
        fw = new FileWriter(filename);
        bw = new BufferedWriter(fw);
        bw.append("  Course   CHEF         WAITER       ST1        ST2        ST3        ST4        ST5        ST6        ST7\n");

    }
    /**
     * Adds line with new states to the logger file
     * @throws IOException 
     */
    public void appendLine() throws IOException {
        bw.append("     " + courseCounter + "      " + chefstate + "          " + waiterstate + "         " + studentstates[0] + "        " + studentstates[1] + "        " + studentstates[2] + "        " + studentstates[3] + "        " + studentstates[4] + "        " + studentstates[5] + "        " + studentstates[6] + "\n");
    }

    /**
     * Updates chef sate and prints in logger
     *
     * @param newstate
     * @throws IOException
     */
    public void updateChefState(Chef_State newstate) throws IOException {
        chefstate = newstate;
        appendLine();

    }

    /**
     * Updates waiter sate and prints in logger
     *
     * @param newstate
     * @throws IOException
     */
    public void updateWaiterState(Waiter_State newstate) throws IOException {
        waiterstate = newstate;
        appendLine();

    }

    /**
     * Updates student x sate and prints in logger
     *
     * @param newstate
     * @param studentID
     * @throws IOException
     */
    public void updateStudentState(Student_State newstate, int studentID) throws IOException {
        studentstates[studentID] = newstate;
        appendLine();
    }

    /**
     * Updates course and prints in logger
     *
     * @throws IOException
     */
    public void updateCourse(int newcourseCounter) throws IOException {
        courseCounter = newcourseCounter;
        appendLine();
    }

    /**
     * closes BufferWriter and FileWriter
     *
     * @throws IOException
     */
    public void closeWriter() throws IOException {
        bw.close();
        fw.close();
    }
}
