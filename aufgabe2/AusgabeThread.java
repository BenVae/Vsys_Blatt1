package aufgabe2;

import java.util.concurrent.TimeUnit;

public class AusgabeThread extends Thread {

    @Override
    public void run() {

        do {
            System.out.print(".");
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (true);
    }
}
