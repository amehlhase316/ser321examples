import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class Worker implements Runnable {
    protected int id;
    protected int sleepDelay;
    protected int loopCount;
    
    public Worker (int assignedID, int sd, int lc) {
        id = assignedID;
        sleepDelay = sd;
        loopCount = lc;
    }

    public void run() {
        for (int loop=0; loop < loopCount; loop++) {
            System.out.println("Hello from " + id  + " loop=" + loop);
            try {
        		Thread.sleep(sleepDelay);
        	} catch (Throwable t) {
        		t.printStackTrace();
        	}
        }
    }
}

public class ThreadPoolExample {
    public static void main(String args[]) throws Exception {
        if (args.length != 3) {
          System.out.println("Expected Arguments: <workers(int)> <sleep(int)> <loop count(int)>");
          System.exit(0);
        }
        int sleepDelay = 10; // default value
        int numWorkers = 25; // default value
        int loopCount = 5; // default value
        try {
            numWorkers = Integer.parseInt(args[0]);
            sleepDelay = Integer.parseInt(args[1]);
            loopCount = Integer.parseInt(args[2]);
        } catch (NumberFormatException nfe) {
            System.out.println("[workers|sleep|loop count] must be integer");
            System.exit(0);
        }
        if (numWorkers < 6) {
          numWorkers = 6;
        }
        int poolSize = numWorkers - 5;

        // lower thread pool than numWorkers;
        Executor pool = Executors.newFixedThreadPool(poolSize);

        for (int i=0; i < numWorkers; i++) {
            pool.execute(new Worker(i, sleepDelay, loopCount)); 
        }
    }
}
