package Regions;

import Entities_states.Student_State;
import Semaphore.Semaphore;
import Entities_states.Waiter_State;
import java.io.IOException;

/**
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public class Table {

    private Bar bar;
    private final Semaphore access;
    private final Semaphore studentsWaitingForSalute;
    private final Semaphore waiterPresentTheMenu;
    private final Semaphore studentChatting;
    private final Semaphore studentswaitingforfriends;
    private final Semaphore waiterBringThePad;
    private final Semaphore studentOrderConfirmation;
    private final Semaphore studentWaitingForBill;
    private final Semaphore waiterWaitingForPayment;
    private final Semaphore waitingForFriendsToBeServed;
    private final Semaphore backToChating;
    private final Semaphore studentsWaitingForWaiter;
    private final Semaphore waitingForFriends;
    private final Semaphore getReadyToPay;
    private final Semaphore preparingTheOrder;

    private int exitCount;

    private final Kitchen kitchen;

    private final Semaphore getPadReady;

    private int studentCount, readyCounter, deliveredCounter, MaxStudents, MCourses;

    private GeneralRepo gr;

    public Table(int MaxStudents, int MCourses, Bar bar, Kitchen kitchen, GeneralRepo gr) {
        this.bar = bar;
        this.kitchen = kitchen;
        this.MaxStudents = MaxStudents;
        this.MCourses = MCourses;
        this.waitingForFriends = new Semaphore();
        this.studentCount = 0;
        this.readyCounter = 0;
        this.waitingForFriendsToBeServed = new Semaphore();
        this.deliveredCounter = 0;
        this.exitCount = 0;
        this.access = new Semaphore();
        this.studentsWaitingForWaiter = new Semaphore();
        this.backToChating = new Semaphore();
        this.getReadyToPay = new Semaphore();
        access.up();
        this.waiterPresentTheMenu = new Semaphore();
        this.studentChatting = new Semaphore();
        this.studentOrderConfirmation = new Semaphore();
        this.waiterBringThePad = new Semaphore();
        this.studentWaitingForBill = new Semaphore();
        this.waiterWaitingForPayment = new Semaphore();
        this.getPadReady = new Semaphore();
        this.preparingTheOrder = new Semaphore();
        studentsWaitingForSalute = new Semaphore();
        studentswaitingforfriends = new Semaphore();
        this.gr = gr;

    }

    /**
     * Student enters the restaurant
     *
     * @param StudentId
     * @return
     * @throws IOException
     */
    public int enter(int StudentId) throws IOException {
        access.down();
//        System.out.println("Table       Student     " + studentCount + " Student enters");
        bar.alertWaiterEntering();
        gr.updateStudentState(Student_State.TST, StudentId);
        access.up();
        studentsWaitingForSalute.down();
        return ++studentCount;
    }

    /**
     * Waiter salutes the student after he enters
     *
     * @throws IOException
     */
    public void saluteTheClient() throws IOException {
        access.down();
//        System.out.println("Table       Waiter      Salute The Client");
        gr.updateWaiterState(Waiter_State.PTM);
        studentsWaitingForSalute.up();
        access.up();
        waiterPresentTheMenu.down();
    }

    /**
     * Student reads the menu
     *
     * @param StudentId
     * @throws IOException
     */
    public void readTheMenu(int StudentId) throws IOException {
        access.down();
        gr.updateStudentState(Student_State.STC, StudentId);
//        System.out.println("Table       Student starts reading");
        waiterPresentTheMenu.up();
        access.up();

    }

    /**
     * First student to arrive checks if every student has chosen his order
     *
     * @return true or false
     */
    public boolean hasEverybodyChosen() {
        return (readyCounter == MaxStudents - 1);
    }

    /**
     * First student to arrive waits for other students to read the menu
     *
     * @param StudentId
     * @throws IOException
     */
    public void prepareTheOrder(int StudentId) throws IOException {
        preparingTheOrder.down();
        access.down();
        gr.updateStudentState(Student_State.OTO, StudentId);
        access.up();
    }

    /**
     * Student x informs first student to arrive that he has chosen the menu
     *
     * @param StudentId
     * @throws IOException
     */
    public void informCompanion(int StudentId) throws IOException {
        access.down();
        readyCounter++;
        preparingTheOrder.up();
        gr.updateStudentState(Student_State.CWC, StudentId);
        access.up();
//        System.out.println("Table       Student     " + readyCounter + " Students ready");
    }

    /**
     * Waiter gets the pad to take order
     *
     * @throws IOException
     */
    public void getThePad() throws IOException {
        access.down();
//        System.out.println("Table       Waiter      Get The Pad");
        gr.updateWaiterState(Waiter_State.TTO);
        access.up();
        getPadReady.up();
        studentOrderConfirmation.down();

    }

    /**
     * First student to arrive describes the order to the waiter
     */
    public void describeTheOrder() {
        getPadReady.down();
//        System.out.println("Table       Student     Student Describes The Order");
        studentOrderConfirmation.up();

    }

    /**
     * First student to arrive joins the talk
     * @param StudentId
     * @throws IOException 
     */
    public void joinTheTalk(int StudentId) throws IOException {
        access.down();
        access.up();
//        System.out.println("Table       Student     Student joins the talk");
        resetReadyCounter();
        gr.updateStudentState(Student_State.CWC, StudentId);

    }

    /**
     * Waiter delivers one portion to table
     */
    public void deliverPortion() {
        access.down();
//        System.out.println("Table       Waiter      Deliver Portion " + deliveredCounter);
        studentChatting.up();
        deliveredCounter++;
        kitchen.chefWaitingForDeliveryUp();
        access.up();
    }

    /**
     * Student x starts eating
     *
     * @param StudentId
     * @throws IOException
     */
    public void startEating(int StudentId) throws IOException, InterruptedException {
        waitingForFriendsToBeServed.down();
        studentChatting.down();
        access.down();
//        System.out.println("Table       Student     Starts eating " + StudentId);
        gr.updateStudentState(Student_State.ETM, StudentId);
        access.up();
        Thread.sleep(500);
    }

    /**
     * Student x ends eating
     *
     * @param StudentId
     * @throws IOException
     */
    public void endEating(int StudentId) throws IOException {
        access.down();
        gr.updateStudentState(Student_State.CWC, StudentId);
//        System.out.println("Table       Student     End eating " + StudentId);
        readyCounter++;
        access.up();
    }

    /**
     * Students x check if everybody finished eating
     *
     * @param currentCourse
     * @return true or false
     */
    public boolean hasEverybodyFinished(int currentCourse) {
        if ((readyCounter == MaxStudents)) {
//            System.out.println("Table       Student     Everybody Finished");
            resetReadyCounter();
            if (currentCourse == (MCourses - 1)) {
                getReadyToPay.up();
            }
            return true;
        }
        return false;
    }

    /**
     * Last Student to arrive informs water he is ready to pay
     *
     * @param StudentId
     * @throws IOException
     */
    public void shouldHaveArrivedEarlier(int StudentId) throws IOException {
        getReadyToPay.down();
        access.down();
        gr.updateCourse(0);

//        System.out.println("Table       Student     Should Have Arrived Earlier " + StudentId);
        bar.PayTheWaiter();
        gr.updateStudentState(Student_State.PTB, StudentId);
        access.up();
        studentWaitingForBill.down();

    }

    /**
     * Last student to arrive pays the bill
     */
    public void honorTheBill() {
        access.down();
//        System.out.println("Table       Student     Honor The Bill");
        access.up();
        waiterWaitingForPayment.up();
        for (int i = 0; i < MaxStudents; i++) {
            waitingForFriends.up();
        }
    }

    /**
     * Student x exits the restaurant
     *
     * @param StudentId
     * @throws IOException
     */
    public void exit(int StudentId) throws IOException {
        waitingForFriends.down();
        access.down();
//        System.out.println("Table       Student     Exit " + StudentId);
        bar.alertTheWaiterExit();
        gr.updateStudentState(Student_State.SGH, StudentId);
        exitCount++;
        if (exitCount == MaxStudents) {
            bar.waiterGoHome();
        }
        access.up();
    }

    /**
     * Waiter checks if all clients have been served
     *
     * @return true or false
     */
    public boolean haveAllClientsBeenServed() {
        access.down();
        if (deliveredCounter == MaxStudents) {
            for (int k = 0; k < MaxStudents; k++) {
                waitingForFriendsToBeServed.up();
            }
            access.up();
            return true;
        }
        access.up();
        return false;
    }

    /**
     * Waiter presents the bill
     *
     * @throws IOException
     */
    public void presentTheBill() throws IOException {
        access.down();
//        System.out.println("Table       Waiter      Present The Bill");
        gr.updateWaiterState(Waiter_State.RTP);
        access.up();
        studentWaitingForBill.up();
        waiterWaitingForPayment.down();

    }

    /**
     * Waiter says goodbye to student
     */
    public void sayGoodbye() {
//        System.out.println("Table       Waiter      Say Goodbye");
    }

    /**
     * Resets the number of students that are ready
     */
    public void resetReadyCounter() {
        readyCounter = 0;
    }

    /**
     * Resets the number of dishes that have been served
     */
    public void resetDeliveredCounter() {
        deliveredCounter = 0;
    }

}
