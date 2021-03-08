This program demonstrate how a deadlock can be created with synchronized methods:

 - https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html
- https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html

The key to why it locks can be found in this bullet point from the Tutorial:

- "When a thread invokes a synchronized method, it automatically acquires the intrinsic lock for that method's object and releases it when the method returns. The lock release occurs even if the return was caused by an uncaught exception."

Since both the `bow()` and `bowback()` method are syncronized methods, they cannot 
both be called on the same object at the same time, whichever is called first must
complete prior to the other executing.

The key to solving this is using a sycnronized statement rather than a synchronized
method. With this approach a seperate lock object can be shared and keep a deadlock
from occuring by not allowing the second bower to start before the first has finished.

A more sophisticated locking scheme can be accomplished with explicit Lock objects
and is described here:

- https://docs.oracle.com/javase/tutorial/essential/concurrency/newlocks.html



