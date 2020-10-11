
class Account {
	int     numTransactions = 0;
	int     balance = 0;

	public synchronized  void deposit(int amount) {
		balance += amount;
		// inc trans
		try {
			Thread.sleep(1000);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		numTransactions++;
		System.out.println("Balance (deposit): " + balance); // prints the balanace after the transactions
	}

	public synchronized  void withdraw(int amount) {
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
		Thread.sleep(sleepDelay); // if sleep is too short the balance will be printed before threads are done
		System.out.println("Balance is " + account.getBalance() + " from " + account.getNumberOfTransactions() + " transactions");
	}
}


// similar example but with two accounts. Whatever goes into one account goes into the other
class SynchExample2 extends Thread {
	Account account1;
	Account account2;
	int id;

	public SynchExample2(int id, Account account1, Account account2) {
		super("Transaction #" + id);
		this.account1 = account1;
		this.account2 = account2;
		this.id = id;
	}

	public void run() {
		System.out.println("Transaction started #" + id);
		for (int i = 1; i <= 3; i++) {
			account2.withdraw(id*i);
			account1.deposit(id*i);
			// deposit done
			System.out.println("Back from deposit/withdraw in thread " + i);
		}
	}

	public static void main(String args[]) throws Exception {
		Account account1 = new Account();
		Account account2 = new Account();

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
			SynchExample2 trans = new SynchExample2(i, account1, account2); // create new thread
			trans.start(); //start new thread
		}
		// All trans done
		Thread.sleep(sleepDelay);
		System.out.println("Balance is " + account1.getBalance() + " from " + account1.getNumberOfTransactions() + " transactions");
	}
}
