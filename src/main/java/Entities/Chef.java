/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import Entities_states.Chef_State;
import Regions.Bar;
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

    /*
        Chef actions' regions:
        Waiting for order - kitchen
        Preparing the course - kitchen
        Dishing the portions - kitchen
        Delivering the portions - kitchen
    
     */
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
//              if (!kitchen.AllPortionsBeenDelivered(StudentNumber)) {
                        kitchen.haveNextPortionReady();
                    }
                }
                if (!kitchen.HaveTheOrderBeenCompleted()) {
                    kitchen.ContinuePreparation(MaxRound);
//                kitchen.ContinuePreparation();
                }
            }
            kitchen.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Chef.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
