
class Account {
	int numTransactions = 0;
	int balance = 0;

	public synchronized void deposit(int amount) {
		balance += amount;
		// inc trans
		try {
			Thread.sleep(1000);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		numTransactions++;
		System.out.println("Balance (deposit): " + balance + ", num: " + numTransactions); // prints the balanace after the transactions
	}

	public synchronized void withdraw(int amount) {
		balance -= amount;
		// inc trans
		try {
			Thread.sleep(1000);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		numTransactions++;
		System.out.println("Balance (withdraw): " + balance); // prints the balanace after the transactions
	}

	public synchronized int getBalance() {
		return balance;
	}
	public synchronized int getNumberOfTransactions() {
		return numTransactions;
	}

	// method that gets both balance and numTransactions since getting them separately might still seem like an inconsistent state
	public synchronized String getBoth(){ 
		return ("Synch:"+ balance + " " + numTransactions);
	}
}

class SynchExample extends Thread {
	Account account;
	int id;

	public SynchExample(int id, Account account) {
		super("Transaction #" + id);
		this.account = account;
		this.id = id;
	}

	public void run() {
		System.out.println("Transaction started #" + id);
		for (int i = 1; i <= 3; i++) {
			account.deposit(id*i);
			// deposit done
			System.out.println("Back from deposit in thread " + id);
		}
	}

	public static void main(String args[]) throws Exception {
		Account account = new Account();

        if (args.length != 2) {
                System.out.println("Expected Arguments: <transactions(int)> <sleep(int)>");
                  System.exit(0);
         }
        int transactions = 5; // default transaction count
        int sleepDelay = 5; // default sleep delay
        try {
              transactions = Integer.parseInt(args[0]);
              sleepDelay = Integer.parseInt(args[1]);
          } catch (NumberFormatException nfe) {
              System.out.println("[transactions|sleep] must be integer");
              System.exit(2);
          }
        // number of transactions
		for (int i=1; i <= transactions; i++) { 
			SynchExample trans = new SynchExample(i, account); // create new thread
			trans.start(); // start new thread
		}
		// All trans done
		Thread.sleep(sleepDelay);                                                                // if sleep is too short the balance will be printed before threads are done
		System.out.println("Balance is " + account.getBalance() + " from " + account.getNumberOfTransactions() + " transactions");
	}
}

// Example which also prints the inconsistent states when just accessing the account attributes
// showing the consistent state with getBoth()
class SynchExample3 extends Thread {
	Account account;
	int id;

	public SynchExample3(int id, Account account) {
		super("Transaction #" + id);
		this.account = account;
		this.id = id;
	}

	public void run() {
		System.out.println("Transaction started #" + id);
		for (int i = 1; i <= 3; i++) {
			account.deposit(id*i);
			// deposit done
			System.out.println("Back from deposit in thread " + id);
		}
	}

	public static void main(String args[]) throws Exception {
		Account account = new Account();

        if (args.length != 2) {
                System.out.println("Expected Arguments: <transactions(int)> <sleep(int)>");
                  System.exit(0);
         }
        int transactions = 5; // default transaction count
        int sleepDelay = 5; // default sleep delay
        try {
              transactions = Integer.parseInt(args[0]);
              sleepDelay = Integer.parseInt(args[1]);
          } catch (NumberFormatException nfe) {
              System.out.println("[transactions|sleep] must be integer");
              System.exit(2);
          }
        // number of transactions
		for (int i=1; i <= transactions; i++) { 
			SynchExample trans = new SynchExample(i, account); // create new thread
			trans.start(); // start new thread
		}

		// since balance and numTransactions is public we can print these in between and see inconsistent states. Better to have them private and thus only reach them through get/set which are synchronized
		System.out.println(account.balance + " " + account.numTransactions);
		System.out.println(account.getBoth());
		Thread.sleep(sleepDelay);    
		System.out.println(account.balance + " " + account.numTransactions);
		System.out.println(account.getBoth());
		Thread.sleep(sleepDelay);    
		System.out.println(account.balance + " " + account.numTransactions);
		System.out.println(account.getBoth());
		Thread.sleep(sleepDelay);    
		System.out.println(account.balance + " " + account.numTransactions);
		System.out.println(account.getBoth());
		Thread.sleep(sleepDelay);    
		System.out.println(account.balance + " " + account.numTransactions);
		System.out.println(account.getBoth());
		Thread.sleep(sleepDelay);    
		System.out.println(account.balance + " " + account.numTransactions);
		System.out.println(account.getBoth());
		Thread.sleep(sleepDelay);    
		System.out.println(account.balance + " " + account.numTransactions);
		System.out.println(account.getBoth());
		
		// All trans done
		Thread.sleep(sleepDelay);                                                                // if sleep is too short the balance will be printed before threads are done
		System.out.println("Balance is " + account.getBalance() + " from " + account.getNumberOfTransactions() + " transactions");
	}
}
