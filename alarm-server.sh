### BEGIN INIT INFO
# Provides:          alarm-server
# Required-Start:    $remote_fs $syslog $local_fs $network ser2sock
# Required-Stop:     $remote_fs $syslog $local_fs $network
# Should-Start:      $portmap
# Should-Stop:       $portmap
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: alarm service initscript
# Description: Provide for running java grizzly alarm service as a daemon.
### END INIT INFO

#!/bin/sh
SERVICE_NAME=alarm-service
PATH_TO_JAR=/home/pi/apps/AlarmServer/target/websockets-home-automation-2.3.14-jar-with-dependencies.jar
PID_PATH_NAME=/tmp/MyService-pid
case $1 in
    start)
        echo "Starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
            nohup java -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &
            echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill $PID;
            echo "$SERVICE_NAME stopped ...";
            rm $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."
            nohup java -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &
            echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
esac