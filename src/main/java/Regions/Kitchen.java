package Regions;

import Semaphore.Semaphore;
import Entities_states.Chef_State;
import Entities_states.Waiter_State;
import java.io.IOException;

/**
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public class Kitchen {

    private final Bar bar;
    private boolean HasTheOrderBeenCompleted;
    private final Semaphore access;
    private final Semaphore chefWatchingTheNews;
    private final Semaphore waiterHandTheNoteToTheChef;
    private final Semaphore chefWaitingForDelivery;
    private final Semaphore chefWaitingForPortionDelivery;
    private final Semaphore waitsForOrder;
    private final Semaphore waiterWaitingForPortion;

    private int CourseCounter;
    private int PortionCounter;
    private GeneralRepo gr;

    public Kitchen(Bar bar, GeneralRepo gr) {
        this.bar = bar;    
        this.CourseCounter = 1;
        this.PortionCounter = 0;
        this.access = new Semaphore();
        this.chefWaitingForDelivery = new Semaphore();
        access.up();
        this.chefWatchingTheNews = new Semaphore();
        this.waiterHandTheNoteToTheChef = new Semaphore();
        this.chefWaitingForPortionDelivery = new Semaphore();
        this.waiterWaitingForPortion = new Semaphore();
        this.waitsForOrder = new Semaphore();
        this.gr=gr;
    }


    /**
     * Chef watches the news waiting for orders
     * @throws IOException 
     */
    public void WatchTheNews() throws IOException {
//        System.out.println("Kitchen     Chef        Watch the news");
        gr.updateChefState(Chef_State.WFO);
        chefWatchingTheNews.down();
    }
    
    /**
     * Waiter hands order to the chef
     * @throws IOException 
     */
    public void handTheNoteToTheChef() throws IOException {
        access.down();
//        System.out.println("Kitchen     Waiter      Hand the note to the chef");
        gr.updateWaiterState(Waiter_State.PTO);
        chefWatchingTheNews.up();
        access.up();
    }

    /**
     * Chef starts preparing the first course
     * @throws IOException 
     */
    public void StartPreparation() throws IOException {
        access.down();
//        System.out.println("Kitchen     Chef        Start Course Preparation");
        gr.updateChefState(Chef_State.PTC);
        //increase course counter in repo by 1
        access.up();
    }

    
    /**
     * Chef proceeds to dishing the portions
     * @throws IOException 
     */
    public void ProceedToPresentation() throws IOException {
        access.down();
//        System.out.println("Kitchen     Chef        Proceed to presentation");
        gr.updateChefState(Chef_State.DIP);
        access.up();

    }

    /**
     * Chef alerts the waiter to deliver the dishes 
     * @param deliveredCount
     * @throws IOException 
     */
    public void AlertTheWaiter(int deliveredCount) throws IOException {
        access.down();
//        System.out.println("Kitchen     Chef        Alert the waiter");
        bar.CallTheWaitertoServe();
        if (deliveredCount == 0) {
            bar.waiterInTheBarUp();
//            System.out.println("Kitchen     Chef        Alert the waiter Up");
        }
        gr.updateChefState(Chef_State.DLP);
        access.up();
        waiterWaitingForPortion.up();
    }

    /**
     * Waiter collects cooked portion
     * @throws IOException 
     */
    public void collectPortion() throws IOException {
        waiterWaitingForPortion.down();
        access.down();
//        System.out.println("Kitchen     Waiter      Collect Portion " + PortionCounter);
        gr.updateWaiterState(Waiter_State.WFP);
        PortionCounter++;
        access.up();
    }

    /**
     * Waiter informs chef that he has delivered the dish
     */
    public void chefWaitingForDeliveryUp() {
        chefWaitingForDelivery.up();
    }

    /**
     * Chef checks if all portions have been delivered
     * @param StudentSize
     * @return true of false
     */
    public boolean AllPortionsBeenDelivered(int StudentSize) {
        if (PortionCounter == StudentSize) {
//            System.out.println("Kitchen     Chef        All Portions Delivered");
            return true;
        }
        return false;
    }


    /**
     * Chef starts preparing next portion
     * @throws IOException 
     */
    public void haveNextPortionReady() throws IOException {
        chefWaitingForDelivery.down();
        access.down();
        gr.updateChefState(Chef_State.DIP);
//        System.out.println("Kitchen     Chef        Have Next Portion Ready");
        access.up();

    }

    /**
     * Chef prepares next course and increments courseCounter
     * @param MaxRound
     * @throws IOException 
     */
    public void ContinuePreparation(int MaxRound) throws IOException {
        bar.waitingForStudentsToFinishDown();
        access.down();
//        System.out.println("Kitchen     Chef        Continue Preparation");
        gr.updateChefState(Chef_State.PTC);
        PortionCounter = 0;
        CourseCounter++;
        if (CourseCounter == MaxRound) {
            HasTheOrderBeenCompleted = true;
        }
        gr.updateCourse(CourseCounter);
        access.up();
    }

    /**
     * Chef cleans up and gets ready to leave
     * @throws IOException 
     */
    public void cleanup() throws IOException {
        access.down();
//        System.out.println("Kitchen     Chef        Cleanup");
        gr.updateChefState(Chef_State.CTS);
        access.up();

    }

    /**
     * Chef checks if order is completed 
     * @return true or false
     */
    public boolean HaveTheOrderBeenCompleted() {
        return HasTheOrderBeenCompleted;
    }

}
