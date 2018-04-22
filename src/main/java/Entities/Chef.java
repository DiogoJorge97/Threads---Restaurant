package Entities;

import Regions.Kitchen;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public class Chef extends Thread {

    private final Kitchen kitchen;
    private final int MaxRound;
    private final int StudentNumber;

    public Chef(Kitchen kitchen, int MaxRound, int StudentNumber) {
        this.kitchen = kitchen;
        this.MaxRound = MaxRound;
        this.StudentNumber = StudentNumber;
    }

    @Override
    public void run() {

        try {
            kitchen.WatchTheNews();
            kitchen.StartPreparation();
            for (int rounds = 0; rounds < MaxRound; rounds++) {
                kitchen.ProceedToPresentation();
                for (int st = 0; st < StudentNumber; st++) {
                    kitchen.AlertTheWaiter(st);
                    if (!kitchen.AllPortionsBeenDelivered(StudentNumber)) {
                        kitchen.haveNextPortionReady();
                    }
                }
                if (!kitchen.HaveTheOrderBeenCompleted()) {
                    kitchen.ContinuePreparation(MaxRound);
                }
            }
            kitchen.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Chef.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
