# Synchronize #

Two main scenarios included:

1) Only one account. Based on first argument threads are created and three withdraws are done on one account. 

	Run a couple of times, see that the "order" of the order of the transactions is different each time.
	End balance is always the same though. 

	Synchronized since you cannot deposit from the same account at same time in different threads. 

2) Two accounts, withdraw from one and put in the other. Done in same thread. 
