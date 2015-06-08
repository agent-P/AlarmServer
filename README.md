Alarm Server
============

Java Grizzly server that serves a read write connection to Vista 10 and Vista 20 alarm panels through ser2soc. The server and webapp are under development. Currently the webapp contains a simple javascript test fixture to view the broadcast messages from the server and send string commands to the alarm panel. It also contains an LCARS prototype iPhone interface. Only Mode 2 is functional.

Portrait Mode 1
----------------

![Panel screenshot portrait mode1](https://github.com/agent-P/AlarmServer/raw/master/docs/ScreenShotPortraitMode1.png)

Portrait Mode 2
----------------

![Panel screenshot portrait mode1](https://github.com/agent-P/AlarmServer/raw/master/docs/ScreenShotPortraitMode2.png)

Landscape Mode 1
----------------

![Panel screenshot portrait mode1](https://github.com/agent-P/AlarmServer/raw/master/docs/ScreenShotLandscapeMode1.png)

Landscape Mode 2
----------------

![Panel screenshot portrait mode1](https://github.com/agent-P/AlarmServer/raw/master/docs/ScreenShotLandscapeMode2.png)


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
4. Build the project with: <code>mvn install</code>
5. Note the application expects a <code>alarm.conf</code> file to be in <code>/etc</code>, See the example.
5. To start the application: <code>java -jar ./target/websockets-home-automation-2.3.14-jar-with-dependencies.jar</code>


Installing Alarm Server on Raspberry Pi
---------------------------------------

1. Copy alarm-server.sh to /etc/init.d

    $ sudo cp alarm-server.sh /etc/init.d/alarm-server
    $ sudo chmod 755 /etc/init.d/alarm-server
    $ sudo chown root:root /etc/init.d/alarm-server

    note: the <code>.sh</code> file extension is dropped, also, the script expects the application root to be <code>/home/pi/apps/AlarmServer</code>


2. Add the appropriate symbolic links to cause the script to be executed when the system goes down, or comes up. The simplest way of doing this is to use the Debian-specific command <code>update-rc.d</code>:


    $ sudo update-rc.d alarm-server defaults


Operation
---------

TODO
