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
    /**
     * Condição de terminação do ciclo de vida do chefe.
     */
    private boolean HasTheOrderBeenCompleted;

    /**
     * Ponto de imposição de exclusão mútua no acesso à região crítica.
     */
    private final Semaphore access;

    /**
     * Ponto de sincronização do chefe enquando aguarda uma encomenda.
     */
    private final Semaphore chefWatchingTheNews;
    /**
     * Ponto de sincronização do empregado de mesa enquanto aguarda comunicar o
     * chefe sobre a encomenda.
     */
    private final Semaphore waiterHandTheNoteToTheChef;

    private final Semaphore chefWaitingForDelivery;

    /**
     * Ponto de sincronização do chefe enquanto aguarda a entrega de uma porção.
     *
     */
    private final Semaphore chefWaitingForPortionDelivery;

    private final Semaphore waitsForOrder;

    /**
     * Ponto de sincronização para quando o waiter está a espera de uma porção.
     */
    private final Semaphore waiterWaitingForPortion;

    private int CourseCounter;
    private int PortionCounter;
    private GeneralRepo gr;

    public Kitchen(Bar bar, GeneralRepo gr) {
        this.bar = bar;     //TODO check if it is needed
        this.CourseCounter = 0;
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
     * Operação de aguardar por uma encomenda.
     */
    public void WatchTheNews() throws IOException {
        System.out.println("Kitchen     Chef        Watch the news");
        gr.updateChefState(Chef_State.WFO);
        chefWatchingTheNews.down();
    }

    public void handTheNoteToTheChef() throws IOException {
//        waitsForOrder.down();
        access.down();
        System.out.println("Kitchen     Waiter      Hand the note to the chef");
        gr.updateWaiterState(Waiter_State.PTO);
        chefWatchingTheNews.up();
        access.up();
//        waiterHandTheNoteToTheChef.down();
    }

//    public void waitsForOrderUp() {
//        waitsForOrder.up();
//    }
    public void StartPreparation() throws IOException {
        access.down();
        System.out.println("Kitchen     Chef        Start Course Preparation");
        gr.updateChefState(Chef_State.PTC);
        //increase course counter in repo by 1
        access.up();
    }

    public void ProceedToPresentation() throws IOException {
        access.down();
        System.out.println("Kitchen     Chef        Proceed to presentation");
        gr.updateChefState(Chef_State.DIP);
        access.up();

    }

    /**
     * Operação de alertar o empregado de mesa que uma porção está pronta a
     * entregar.
     */
    public void AlertTheWaiter(int st) throws IOException {
//        chefWaitingForPortionDelivery.down();
        access.down();
        System.out.println("Kitchen     Chef        Alert the waiter");
        bar.CallTheWaitertoServe();
        if (st == 0) {
            bar.waiterInTheBarUp();
            System.out.println("Kitchen     Chef        Alert the waiter Up");
        }
        gr.updateChefState(Chef_State.DLP);
        access.up();
        waiterWaitingForPortion.up();
    }

    public void collectPortion() throws IOException {
        waiterWaitingForPortion.down();
        access.down();
        System.out.println("Kitchen     Waiter      Collect Portion " + PortionCounter);
        gr.updateWaiterState(Waiter_State.WFP);
//        chefWaitingForPortionDelivery.up();
        PortionCounter++;
        access.up();
    }

    public void chefWaitingForDeliveryUp() {
        chefWaitingForDelivery.up();
    }

    /**
     * Operação para confirmar a entrega de todas as porções.
     *
     * @param StudentSize
     * @return
     */
    public boolean AllPortionsBeenDelivered(int StudentSize) {
        if (PortionCounter == StudentSize) {
            System.out.println("Kitchen     Chef        All Portions Delivered");
            return true;
        }
        return false;
    }

    /**
     * Operação para preparação da próxima porção.
     */
    public void haveNextPortionReady() throws IOException {
        chefWaitingForDelivery.down();
        access.down();
        gr.updateChefState(Chef_State.DIP);
        System.out.println("Kitchen     Chef        Have Next Portion Ready");
        access.up();

    }

    /**
     * Operação para ínicio do próximo prato (entrada, principal, sobremesa).
     *
     * @param MaxRound
     */
    public void ContinuePreparation(int MaxRound) throws IOException {
        bar.waitingForStudentsToFinishDown();
        access.down();
        System.out.println("Kitchen     Chef        Continue Preparation");
        gr.updateChefState(Chef_State.PTC);
        PortionCounter = 0;
        CourseCounter++;
        if (CourseCounter == MaxRound - 1) {
            HasTheOrderBeenCompleted = true;
        }
        access.up();
    }

    /**
     * Operação de fecho.
     */
    public void cleanup() throws IOException {
        access.down();
        System.out.println("Kitchen     Chef        Cleanup");
        gr.updateChefState(Chef_State.CTS);
        access.up();

    }

    //Métodos do Waiter
    //Other Methods
    public boolean HaveTheOrderBeenCompleted() {
        return HasTheOrderBeenCompleted;
    }

}
