package Regions;

import Semaphore.Semaphore;

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

    public Kitchen(Bar bar) {
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

    }

    /**
     * Operação de aguardar por uma encomenda.
     */
    public void WatchTheNews() {
        System.out.println("Kitchen     Chef        Watch the news");
        chefWatchingTheNews.down();
    }

    public void handTheNoteToTheChef() {
//        waitsForOrder.down();
        access.down();
        System.out.println("Kitchen     Waiter      Hand the note to the chef");
        //state from TTO to PTO
        chefWatchingTheNews.up();
        access.up();
//        waiterHandTheNoteToTheChef.down();
    }

//    public void waitsForOrderUp() {
//        waitsForOrder.up();
//    }

    public void StartPreparation() {
        access.down();
        System.out.println("Kitchen     Chef        Start Course Preparation");
        //state from WFO to PTC
        //increase course counter in repo by 1
        access.up();
    }

    public void ProceedToPresentation() {
        access.down();
        System.out.println("Kitchen     Chef        Proceed to presentation");
        //state from PTC to DTP
        access.up();

    }

    /**
     * Operação de alertar o empregado de mesa que uma porção está pronta a
     * entregar.
     */
    public void AlertTheWaiter(int st) {
//        chefWaitingForPortionDelivery.down();
        access.down();
        System.out.println("Kitchen     Chef        Alert the waiter");
        bar.CallTheWaitertoServe();
        if (st==0){
            bar.waiterInTheBarUp();
            System.out.println("Kitchen     Chef        Alert the waiter Up");
        }
        //state from DTP to DP
        access.up();
        waiterWaitingForPortion.up();
    }

    public void collectPortion() {
        waiterWaitingForPortion.down();
        access.down();
        System.out.println("Kitchen     Waiter      Collect Portion " + PortionCounter);
        //state from AS to WFP
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
    public void haveNextPortionReady() {
        access.down();
        System.out.println("Kitchen     Chef        Have Next Portion Ready");
        //state from DP to DTP
        access.up();
        chefWaitingForDelivery.down();
    }

    /**
     * Operação para ínicio do próximo prato (entrada, principal, sobremesa).
     *
     * @param MaxRound
     */
    public void ContinuePreparation(int MaxRound) {
        bar.waitingForStudentsToFinishDown();
        access.down();
        System.out.println("Kitchen     Chef        Continue Preparation");
        //increase course counter in repo by 1
        //state from DP to PTC
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
    public void cleanup() {
        access.down();
        System.out.println("Kitchen     Chef        Cleanup");
        //state from DP to CS
        access.up();

    }

    //Métodos do Waiter
    //Other Methods
    public boolean HaveTheOrderBeenCompleted() {
        return HasTheOrderBeenCompleted;
    }

}
