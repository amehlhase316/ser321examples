public class Transaction extends Thread {
    Account account;
    int id;
    int numDeposits;

    public Transaction(int id, Account account, int nd) {
        super("Transaction #" + id);
        this.account = account;
        this.id = id;
        this.numDeposits = nd;
    }

    public void run() {
        System.out.println("Transaction started #" + id);
        for (int i = 0; i < numDeposits; i++) {
            account.deposit(id*i);
        }
    }

    public static void main(String args[]) throws InterruptedException {
        Account account = new Account();
        if (args.length != 3) {
          System.out.println("Expected Arguments: <transactions(int)> <sleep(int)> <deposits(int)>");
          System.exit(0);
        }

        int sleepDelay = 10; // default value
        int transactions = 5; // default value
        int numDeposits = 5; // default value

        try {
            transactions = Integer.parseInt(args[0]);
            sleepDelay = Integer.parseInt(args[1]);
            numDeposits = Integer.parseInt(args[2]);
        } catch (NumberFormatException nfe) {
            System.out.println("[transactions|sleep|deposit] must be integer");
            System.exit(0);
        }

        /* don't change i to 0, see line 16 */
        for (int i=1; i <= transactions; i++) {
            Transaction trans = new Transaction(i, account, numDeposits);
            trans.start();
        }
        Thread.sleep(sleepDelay);
        System.out.println("Balance is " + account.getBalance());
    }
}

class Account {
    int     numTransactions = 0;
    int     balance = 0;

    public synchronized void deposit(int amount) {
        balance += amount;
        numTransactions++;
    }

    public synchronized int getBalance() {
        return balance;
    }
}
