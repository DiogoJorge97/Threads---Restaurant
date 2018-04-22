/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import Entities_states.Chef_State;
import Regions.Bar;
import Regions.Kitchen;

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
    private Kitchen kitchen;
    private Bar bar;
    private Chef_State state;
    private int MaxRound;
    private int StudentNumber;

    public Chef(Kitchen kitchen, Bar bar,int MaxRound, int StudentNumber) {
        this.kitchen = kitchen;
        this.bar = bar;
        this.MaxRound = MaxRound;
        this.StudentNumber = StudentNumber;
        this.state = Chef_State.WFO;
    }

    @Override
    public void run() {

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

    }
}
