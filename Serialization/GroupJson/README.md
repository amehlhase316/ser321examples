##### Author: Tim Lindquist (Tim.Lindquist@asu.edu)
        Software Engineering, CIDSE, IAFSE, Arizona State University Polytechnic
* Version: January 2020

##### Purpose
Sample project showing Java use of Json for simple
serialization/deserialization. The Java program creates a group
of users and then serializes it to Json, writing the Json to a text file.
The Java program also serializes and de-serializes using Java's built-in
serialization facilities. Note also the use of serialVersionUID in the
Java Group and User classes to demonstrate its use.

The Java program uses the Json reference implementation from Douglas Crockford
which you can download from:

https://github.com/douglascrockford/JSON-java

The classes from this library are included in the lib directory of this project
in the jar file: json.jar. Nevertheless, you should download the library and
generate the javadocs for the classes, which will be useful in utilizing
Json with your Java programs.

##### Run
You should execute the Java program with:
gradle run

