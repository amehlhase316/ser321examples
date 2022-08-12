##### Author: Tim Lindquist (Tim.Lindquist@asu.edu)
        Software Engineering, CIDSE, IAFSE, Arizona State University Polytechnic
* Version: January, 2020

##### Purpose
Sample project showing Java serialization and migration of classes.
This program shows what happens when you use the serialversion UID in the
context of changing a class definition and then subsequently reading a serialized
version of the class that was created by a prior version. To see how this works do
the following:

You should execute the Java program first with:
gradle ClassMigration:runUser 

Take note of the declaration that is printed. You will need to compare it in step 3.

Next run the program (unmodified User.java class) to serialize:
gradle ClassMigration:runFileSerialize --args 'write'
this causes the program to write out a serialized object into user.ser

Next, edit the User class and uncomment line 44:
   //private int age;
Now the class has been migrated. Save the User.java file and then execute:
gradle ClassMigration:runFileSerialize --args 'read'

Observe the results of the execution: It generates an exception because the
serial version of the serialized version of the class being read does NOT match
the serialversion of the new class which is attempting to deserialize.
Now, edit the User class again to uncomment line 45:
   //private static final long serialVersionUID = -3612890521606032036L;
When you do this, assure that the definition matches that you got when running

##### Run
gradle runUser 

Now run the program again with attempting to read:
gradle runFileSerialize --args 'read'

The result should be success. This demonstrates how serialversioning works in java.
The manuals describe what happens in this situtuation, assuming that you declare the
serialversion uid in the class as it evolves. There are compatible and incompatible changes
that can be made for deserialzation purposes.
end
