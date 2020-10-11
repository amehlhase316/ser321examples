public class Deadlock {
	static class Friend {
		private final String name;
		public Friend(String name) {
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
        /* See the README.md for a reference on 'synchronized' methods */
		public synchronized void bow(Friend bower) {
			System.out.format("%s: %s"
					+ "  has bowed to me!%n", 
					this.name, bower.getName());
            System.out.format("%s: waiting to bow back%n", bower.getName());
			bower.bowBack(this);
		}
		public synchronized void bowBack(Friend bower) {
            System.out.format("%s: waiting", this.name);
			System.out.format("%s: %s"
					+ " has bowed back to me!%n",
					this.name, bower.getName());
		}
	}

	public static void main(String[] args) {
		final Friend alphonse =
				new Friend("Alphonse");
		final Friend gaston =
				new Friend("Gaston");
        /* start two threads - both operating on the same objects */
		new Thread(new Runnable() {
			public void run() { alphonse.bow(gaston); }
		}).start();
		new Thread(new Runnable() {
			public void run() { gaston.bow(alphonse); }
		}).start();
	}
}
