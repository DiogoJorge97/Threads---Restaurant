package Regions;

import Semaphore.Semaphore;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

    /**
     *
     *
     */
    private final Kitchen kitchen;

    private Semaphore getPadReady;

    private int studentCount, readyCounter, deliveredCounter, MaxStudents, MCourses;
    /**
     * Variavel de apoio para determinar qual dos Students a acordar pelo
     * Waiter.
     */
    private LinkedList<Integer> whoToWake;

    public Table(int MaxStudents, int MCourses, Bar bar, Kitchen kitchen) {
        this.bar = bar;
        this.kitchen = kitchen;
        this.MaxStudents = MaxStudents;
        this.MCourses = MCourses;
        this.waitingForFriends = new Semaphore();
        this.studentCount = 0;
        this.readyCounter = 0;
        this.waitingForFriendsToBeServed = new Semaphore();
        this.deliveredCounter = 0;
        this.whoToWake = new LinkedList();
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

    }

    public int enter() {
        access.down();
        System.out.println("Table       Student     " + studentCount + " Student enters");
        bar.allertWaiterEntering();
        //log state TST
        access.up();
        studentsWaitingForSalute.down();
        return ++studentCount;
    }

    public void saluteTheClient() {
        access.down();
        System.out.println("Table       Waiter      Salute The Client");
        //state from ATS to PTM
        studentsWaitingForSalute.up();
        access.up();
        waiterPresentTheMenu.down();
    }

    public void readTheMenu(int id) {
        access.down();
        //log state TTO
        System.out.println("Table       Student     " + id + " I'm reading");
        waiterPresentTheMenu.up();
        access.up();

    }

    public boolean hasEverybodyChosen() {
        return (readyCounter == MaxStudents - 1);
    }

    public void prepareTheOrder() {
        preparingTheOrder.down();
        access.down();
        //if state != STC then state = OTO
        access.up();
    }

    public void informCompanion() {
        access.down();
        readyCounter++;
        preparingTheOrder.up();
        access.up();
        System.out.println("Table       Student     " + readyCounter + " Students ready");
//        studentChatting.down();
    }

    public void getThePad() {
        access.down();
        System.out.println("Table       Waiter      Get The Pad");
        //state from ATS to TTO
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
        //studentsWaitingForWaiter.up();
        deliveredCounter++;
        kitchen.chefWaitingForDeliveryUp();
        access.up();
    }

    public void startEating(int id) {
        //TODO dar down aos stdents para esperarem por o waiter
        //studentsWaitingForWaiter.down();
        waitingForFriendsToBeServed.down();
        studentChatting.down();
        access.down();
        System.out.println("Table       Student     Starts eating " + id);
        //state from CWC to ETM
        access.up();
    }

    public void endEating(int StudentID) {
        access.down();
        //state from ETM to CWC
        System.out.println("Table       Student     End eating " + StudentID);
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

    public void shouldHaveArrivedEarlier(int id) {
        getReadyToPay.down();
        access.down();
        System.out.println("Table       Student     Should Have Arrived Earlier " + id);
        bar.PayTheWaiter();
        //state from CWC to PTB
        access.up();
        studentWaitingForBill.down();

    }

    public void honorTheBill() {
        access.down();
        System.out.println("Table       Student     Honor The Bill");
        access.up();
        waiterWaitingForPayment.up();
        for (int i = 0; i < MaxStudents-1; i++) {
            waitingForFriends.up();
        }
    }

    public void exit(int id) {
        waitingForFriends.down();
        access.down();
        System.out.println("Table       Student     Exit " + id);
        bar.allertTheWaiterExit();
        //state from CWC to GH
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

    public void presentTheBill() {
        access.down();
        System.out.println("Table       Waiter      Present The Bill");
        //state from PTB to RP
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
