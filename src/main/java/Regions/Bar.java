package Regions;

import Semaphore.Semaphore;

/**
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public class Bar {

    private final Semaphore access;
    private char situation;

    /**
     * Ponto de sincronização em que o waiter espera por uma situação no bar.
     */
    private final Semaphore waiterInTheBar;

    private final Semaphore waitingForStudentsToFinish;

    public Bar(int StudentSize) {
        this.waiterInTheBar = new Semaphore();
        this.waitingForStudentsToFinish = new Semaphore();
        this.access = new Semaphore();
        access.up();
    }

    public void waiterInTheBarUp() {
        waiterInTheBar.up();
    }

    public char lookAround() {
        waiterInTheBar.down();
        access.down();
        System.out.println("Bar         Waiter      Looks around: " + situation);
        //state ATS
        access.up();
        return situation;
    }

    public void changeSituation(char newsituation) {
        situation = newsituation;
        System.out.println("Bar         Waiter      Changing situation to: " + newsituation);
    }

    //Student Methods
    public void callTheWaiter() {
        access.down();
        System.out.println("Bar         Student     Call the waiter");
        changeSituation('o');
        waiterInTheBar.up();
        access.up();

    }

    public void returnToTheBar() {
        access.down();
        System.out.println("Bar         Waiter      Return to the bar");
        System.out.println("-----------------------------------------");
        //state to ATS
        access.up();
    }

    //Waiter Methods
    public void prepareTheBill() {
        //state ATS to PTB
        System.out.println("Bar         Waiter      Prepare the bill");
    }

    public void SignalTheWaiter() {
        access.down();
        System.out.println("Bar         Student     Signal the waiter");
        changeSituation('c');
        //waiterInTheBar.up();
        //TODO check up
        waitingForStudentsToFinish.up();
        access.up();
    }

    public void PayTheWaiter() {
        access.down();
        System.out.println("Bar         Student     Pay the waiter");
        changeSituation('p');
        waiterInTheBar.up();
        access.up();
    }

    public void waitingForStudentsToFinishDown() {
        waitingForStudentsToFinish.down();
    }

    public void allertWaiterEntering() {
        access.down();
        System.out.println("Bar         Student     Alert the waiter entering");
        changeSituation('n');
        waiterInTheBar.up();
        access.up();
    }

    void CallTheWaitertoServe() {
        access.down();
        changeSituation('c');
//        waiterInTheBar.up();      
//TODO check if needed up
        access.up();
    }

    void allertTheWaiterExit() {
        access.down();
        changeSituation('g');
        waiterInTheBar.up();
        access.up();
    }

}
