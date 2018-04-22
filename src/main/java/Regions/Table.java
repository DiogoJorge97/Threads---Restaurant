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

    /**
     * Ponto de sincronização para quando os students esperam ser sentados pelo
     * waiter.
     */
    private final Semaphore studentsWaitingForSalute;

    /**
     * Ponto de sincronização para quando o student requere o menu.
     */
    private final Semaphore waiterPresentTheMenu;

    /**
     * Ponto de sincronização para que o primeiro student que chegou faça a
     * ordem.
     */
    private final Semaphore studentChatting;

    /**
     * Ponto de sincronização para quando os students estão à espera da comida e
     * para quando terminam a comida.
     */
    private final Semaphore studentswaitingforfriends;

    /**
     * Ponto de sincronização para quando o student que chegou primeiro chama o
     * waiter para fazer a encomenda.
     */
    private final Semaphore waiterBringThePad;

    /**
     * Ponto de sincronização para o student espera por confirmação da ordem por
     * parte do waiter.
     */
    private final Semaphore studentOrderConfirmation;

    /**
     * Ponto de sincronização para quando o último student que chegou (ID = 6)
     * pede a conta.
     */
    private final Semaphore studentWaitingForBill;

    /**
     * Ponto de sincronização para quando o waiter espera pelo pagamento da
     * conta.
     */
    private final Semaphore waiterWaitingForPayment;

    private final Semaphore waitingForFriendsToBeServed;

    private final Semaphore backToChating;

    private final Semaphore studentsWaitingForWaiter;

    private final Semaphore waitingForFriends;

    private final Semaphore getReadyToPay;

    private final Semaphore preparingTheOrder;
    
    private int exitCount;

    /**
     *
     *
     */
    private final Kitchen kitchen;

    private final Semaphore getPadReady;

    private int studentCount, readyCounter, deliveredCounter, MaxStudents, MCourses;
    /**
     * Variavel de apoio para determinar qual dos Students a acordar pelo
     * Waiter.
     */
    
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
        this.exitCount=0;
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
        this.gr=gr;

    }

    public int enter(int id) throws IOException {
        access.down();
        System.out.println("Table       Student     " + studentCount + " Student enters");
        bar.allertWaiterEntering();
        gr.updateStudentState(Student_State.TST, id);
        access.up();
        studentsWaitingForSalute.down();
        return ++studentCount;
    }

    public void saluteTheClient() throws IOException {
        access.down();
        System.out.println("Table       Waiter      Salute The Client");
        gr.updateWaiterState(Waiter_State.PTM);
        studentsWaitingForSalute.up();
        access.up();
        waiterPresentTheMenu.down();
    }

    public void readTheMenu(int id) throws IOException {
        access.down();
        gr.updateStudentState(Student_State.STC, id);
        System.out.println("Table       Student     " + id + " I'm reading");
        waiterPresentTheMenu.up();
        access.up();

    }

    public boolean hasEverybodyChosen() {
        return (readyCounter == MaxStudents - 1);
    }

    public void prepareTheOrder(int id) throws IOException {
        preparingTheOrder.down();
        access.down();
        gr.updateStudentState(Student_State.OTO, id);
        access.up();
    }

    public void informCompanion(int id) throws IOException {
        access.down();
        readyCounter++;
        preparingTheOrder.up();
        gr.updateStudentState(Student_State.CWC, id);
        access.up();
        System.out.println("Table       Student     " + readyCounter + " Students ready");
    }

    public void getThePad() throws IOException {
        access.down();
        System.out.println("Table       Waiter      Get The Pad");
        gr.updateWaiterState(Waiter_State.TTO);
        access.up();
        getPadReady.up();
        studentOrderConfirmation.down();

    }

    public void describeTheOrder() {
        getPadReady.down();
        System.out.println("Table       Student     Student Describes The Order");
//        kitchen.waitsForOrderUp();
//        waiterBringThePad.up();
        studentOrderConfirmation.up();

    }

    public void joinTheTalk() {
        access.down();
        //state from OTO to CWC
//        readyCounter = 0; //reutilize for meal cycles
        access.up();
        System.out.println("Table       Student     Student joins the talk");
        System.out.println("--------------------------------------\n--------------------------------------");
        resetReadyCounter();
        //studentsWaitingForFoodORDoneWithFood[0].down();
    }

    public void deliverPortion() {
        access.down();
        System.out.println("Table       Waiter      Deliver Portion " + deliveredCounter);
        studentChatting.up();
        deliveredCounter++;
        kitchen.chefWaitingForDeliveryUp();
        access.up();
    }

    public void startEating(int id) throws IOException {
        waitingForFriendsToBeServed.down();
        studentChatting.down();
        access.down();
        System.out.println("Table       Student     Starts eating " + id);
        gr.updateStudentState(Student_State.ETM, id);
        access.up();
    }

    public void endEating(int id) throws IOException {
        access.down();
        gr.updateStudentState(Student_State.CWC, id);
        System.out.println("Table       Student     End eating " + id);
        readyCounter++;
        access.up();
    }

    public boolean hasEverybodyFinished(int i) {
        if ((readyCounter == MaxStudents)) {
            System.out.println("Table       Student     Everybody Finished");
            resetReadyCounter();
            if (i == (MCourses - 1)) {
                getReadyToPay.up();
            }
            return true;
        }
        return false;
    }

    public void shouldHaveArrivedEarlier(int id) throws IOException {
        getReadyToPay.down();
        access.down();
        System.out.println("Table       Student     Should Have Arrived Earlier " + id);
        bar.PayTheWaiter();
        gr.updateStudentState(Student_State.PTB, id);
        access.up();
        studentWaitingForBill.down();

    }

    public void honorTheBill() {
        access.down();
        System.out.println("Table       Student     Honor The Bill");
        access.up();
        waiterWaitingForPayment.up();
        for (int i = 0; i < MaxStudents; i++) {
            waitingForFriends.up();
        }
    }

    public void exit(int id) throws IOException {
        waitingForFriends.down();
        access.down();
        System.out.println("Table       Student     Exit " + id);
        bar.allertTheWaiterExit();
        gr.updateStudentState(Student_State.SGH, id);
        exitCount++;
        if (exitCount==MaxStudents){
            bar.waiterGoHome();
        }
        access.up();
    }

    //Waiter Methods
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

    public void presentTheBill() throws IOException {
        access.down();
        System.out.println("Table       Waiter      Present The Bill");
        gr.updateWaiterState(Waiter_State.RTP);
        access.up();
        studentWaitingForBill.up();
        waiterWaitingForPayment.down();
        
    }

    public void sayGoodbye() {
        System.out.println("Table       Waiter      Say Goodbye");
    }

    //Variables Methods
    public void resetReadyCounter() {
        readyCounter = 0;
    }

    public void resetDeliveredCounter() {
        deliveredCounter = 0;
    }

}
