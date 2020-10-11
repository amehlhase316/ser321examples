/**

 * Purpose:
 * A program to demonstrate threads with a synchronized shared object in java.
 *
 * <p>
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @version April, 2020
 */
public class SynchronizedThreads {
    public static void main(String args[]){
    if (args.length != 2) {
            System.out.println("Expected Arguments: <threads(int)> <sleep(int)>");
              System.exit(0);
     }
    int threads = 5; // default thread count
    int sleepDelay = 5; // default sleep delay
    try {
          // start up the given number of threads in this program
          threads = Integer.parseInt(args[0]);
          sleepDelay = Integer.parseInt(args[1]);
      } catch (NumberFormatException nfe) {
          System.out.println("[threads|sleep] must be integer");
          System.exit(2);
      }
        // create a data object to be shared among the 4 threads.
	ShareableData theDataItem = new ShareableData(25);

	for (int i=1; i <= threads; i++){
	    AThread t = new AThread(i, theDataItem, sleepDelay);
	    t.start();
      }
    }
}

/*
 * Exercise. Question, how do we know that the 4 threads are blocked
 * from executing either access or mutate methods 
 * while another of the 4 threads are executing them?
 * Modify the code below by adding println's and sleep's
 * in such a way that demonstrates that the threads are blocked from 
 * entering the methods while another thread is executing them.
 */
class AThread extends Thread {
    private int id;
    private int sleepDelay;
    private ShareableData theData;
    public AThread(int newId, ShareableData sd, int sleepDelay){
	id = newId;
	theData = sd;
    sleepDelay = sleepDelay;
    }

    public void run(){
	System.out.println("Started thread #" + id);
	try{
	    for (int count = 0; count < 3; count++){
		if(id==1 && count>0) sleep(sleepDelay); //delay the first thread
                if(count<2){
                   theData.access(id, count);
                } else {
                   theData.increment(id,count);
                }
          Thread.yield();
	    }
	}
	catch (Exception e) {e.printStackTrace();}
    }
}

class ShareableData {
    private int myData;

    public ShareableData(int theValue){
        myData = theValue;
    }

    synchronized public void access(int who, int count){
	System.out.println("Shareable data with value " + myData
                         + " accessed by thread " + who + " count is " + count);
    }

    synchronized public void increment(int who, int count){
       myData = myData + 1;
	System.out.println("Shareable data with value " + myData
                        + " changed  by thread " + who + " count is " + count);
    }
}
