# AlarmServer
Alarm Server
============

Java Grizzly server that serves a read write connection to Vista 10 and Vista 20 alarm panels through ser2soc. The server and webapp are under development. Currently the webapp is a simple javascript test fixture to view the broadcast messages from the server and send string commands to the alarm panel.

License
-------

The completed server and webapp will be distributed under the terms of the [GPLv2](http://www.gnu.org/licenses/gpl-2.0.html).<br/>
The text of the license will be included in the file LICENSE in the root of the project.


Getting help
------------

The code has verbose javadoc comments. They have been designed to create formal documentation when processed.

Building Alarm Server
---------------------

A maven pom file is provide to build and package the server.

1. Note all dependencies for this project are specified in the maven pom file.
2. Clone this repository to any system with java and maven installed.<br/>
3. cd to the root directory of the project.<br/>
4. Build the project with: <code>mV install</code>
5. To start the application: <code>java -jar ./target/websockets-home-automation-2.3.14-jar-with-dependencies.jar</code>


Operation
---------

TODO
