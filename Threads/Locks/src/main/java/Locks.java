import java.util.concurrent.locks.*;

class LockThread extends Thread {
    protected int id;
    protected int sleepDelay;
    protected int loopCount;
    protected Lock mutex;

    public LockThread (int id, Lock mutex, int sd, int lc) {
        this.id = id;
        this.mutex = mutex;
        this.loopCount = lc;
        this.sleepDelay = sd;
    }
    public void run() {
        for (int loop=0; loop < loopCount; loop++) {
            mutex.lock();
            try {
                System.out.println("Thread" + id + " has lock");
                Thread.sleep(sleepDelay);
             } catch (InterruptedException e) {
             } finally {
                System.out.println("Thread" + id + " releasing lock");
                mutex.unlock();
	    }
	}
    }
}

public class Locks {
    public static void main(String args[]) throws Exception {
        Lock mutex = new ReentrantLock();
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

        for (int i=0; i < numWorkers; i++) {
            (new LockThread(i, mutex, sleepDelay, loopCount)).start();
        }
    }
}

