/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import Entities_states.Waiter_State;
import Regions.Bar;
import Regions.Kitchen;
import Regions.Table;

/**
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public class Waiter extends Thread {

    /*
        Waiter actions' regions
        Appraising situation - bar
        Presenting the menu - table
        Taking the order - table
        Placing the order - kitchen
        Waiting for portion - kitchen
        Processing the bill - bar
        Receiving payment - table
     */
    private Waiter_State state;
    private Kitchen kitchen;
    private Bar bar;
    private Table table;

    public Waiter(Kitchen kitchen, Bar bar, Table table) {
        this.kitchen = kitchen;
        this.bar = bar;
        this.table = table;
    }

    @Override
    public void run() {
        //Teacher Method
        //alt1 = n
        //alt2 = o
        //alt3 = e
        //alt4 = p
        //alt5 = g
        //alt6 = E
        char alt;
        while ((alt = bar.lookAround()) != 'E') {
            switch (alt) {
                case 'n':
                    table.saluteTheClient();
                    break;
                case 'o':
                    table.getThePad();
                    kitchen.handTheNoteToTheChef();
                    break;
                case 'c':
                    do {
                        kitchen.collectPortion();
                        table.deliverPortion();
                    } while (!table.haveAllClientsBeenServed());
                    table.resetDeliveredCounter();
                    break;
                case 'p':
                    bar.prepareTheBill();
                    table.presentTheBill();
                    break;
                case 'g':
                    table.sayGoodbye();
                    break;
            }
            if (alt != 'g') {
                bar.returnToTheBar();
            }
        }

    }
}
