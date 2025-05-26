class Worker425 implements Runnable {
    protected int id;
    protected int sleepDelay;
    
    public Worker425 (int assignedID, int sd) {
        id = assignedID;
        sleepDelay = sd;
    }

    public void run() {
        for (int loop=0; loop < 5; loop++) {
            System.out.println("Hello from " + id  + " loop=" + loop);
            try {
        		Thread.sleep(sleepDelay);
        	} catch (Throwable t) {
        		t.printStackTrace();
        	}
        }
    }
}

class FirstThread {
    public static void main(String args[]) {
      if (args.length != 2) {
              System.out.println("Expected Arguments: <repeat(int)> <sleep(int)>");
                System.exit(0);
       }
      int times = 5; // default repeat count
      int sleepDelay = 5; // default sleep delay
      try {
            times = Integer.parseInt(args[0]);
            sleepDelay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[repeat|sleep] must be integer");
            System.exit(2);
        }
        
        for (int loop = 0; loop < times; loop++) {
            Runnable worker = new Worker425(loop, sleepDelay*loop);
            Thread task = new Thread(worker, "Task#"+loop);
            task.start();
        }
    }
}
