package Entities;

import Regions.Bar;
import Regions.Kitchen;
import Regions.Table;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ricardo Antão
 * @author Diogo Jorge
 */
public class Waiter extends Thread {

    private final Kitchen kitchen;
    private final Bar bar;
    private final Table table;

    public Waiter(Kitchen kitchen, Bar bar, Table table) {
        this.kitchen = kitchen;
        this.bar = bar;
        this.table = table;
    }

    @Override
    public void run() {
        try {
            char alt;
            while ((alt = bar.lookAround()) != 'e') {
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
        } catch (IOException ex) {
            Logger.getLogger(Waiter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
