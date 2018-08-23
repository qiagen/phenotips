#!/bin/bash
#
# start/stop up ReportView web application via its associated Tomcat server instance
# $Id$
# Copyright (c) 2005 Ingenuity Systems, Inc., All Rights Reserved.
#

#########################################
# Keep for Merck Solaris environment
#########################################

PATH=$PATH:/opt/sfw/bin
export PATH
PIDFILE=/usr/local/ingenuity/logs/phenotips/phenotips.pid
export PIDFILE

#########################################

PHENOTIPS_HOME=/usr/local/ingenuity/phenotips/phenotips-live
LIB_DIR=$PHENOTIPS_HOME/web/WEB-INF/lib
CLASS_DIR=$PHENOTIPS_HOME/web/WEB-INF/classes
MAXMEM=256M
INSTALLENV=prod
JAVA_VM=-d64
JAVA_HOME=/usr/local/ingenuity/java1_8-live
export JAVA_HOME
PORT=8169
APPNAME=phenotips
PSPATTERN="[0-9] /(opt|usr).*/ingenuity/.*java .*phenotips/gc.out"
DEBUG_OPTS=
function killpid () {
        PID=$1
        echo "kill -TERM $PID"; kill -TERM $PID # Send TERM signal
        for i in {1..10}                                # Wait for process to terminate
        do
                ps -p $PID || break; sleep 4
        done
        if ps -p $PID
        then
                echo "kill -QUIT $PID"; kill -QUIT $PID # Send QUIT signal to make a thread dump
                sleep 2                                 # Needs only 2 seconds
                if ps -p $PID
                then
                        echo "kill -9 $PID"; kill -9 $PID # Send KILL signal
                        while ps -p $PID                        # Wait for kill to finish
                        do
                                sleep 2
                        done
                fi
        fi
}

TEMPMAXMEM=`grep phenotipsapp.maxmem $PHENOTIPS_HOME/web/WEB-INF/classes/phenotipsbuild-config.properties |grep -v ^# | cut -d'=' -f2- `
TEMPMINMEM=`grep phenotipsapp.minmem $PHENOTIPS_HOME/web/WEB-INF/classes/phenotipsbuild-config.properties |grep -v ^# | cut -d'=' -f2- `
TEMPPERMGENMAXMEM=`grep phenotipsapp.permgenmaxmem $PHENOTIPS_HOME/web/WEB-INF/classes/phenotipsbuild-config.properties |grep -v ^# | cut -d'=' -f2- `
INSTALLENV=`grep install.env $PHENOTIPS_HOME/web/WEB-INF/classes/phenotipsbuild-config.properties |grep -v ^# | cut -d'=' -f2- `
ENABLE_ASSERTIONS=`grep enable.assertions $PHENOTIPS_HOME/web/WEB-INF/classes/phenotipsbuild-config.properties |grep -v ^# | cut -d'=' -f2- `

if [ -n "$TEMPMAXMEM" ]
then
        MAXMEM=$TEMPMAXMEM
fi

if [ -n "$TEMPMINMEM" ]
then
        MINMEM=$TEMPMINMEM
else
        MINMEM=$MAXMEM
fi

if [ -n "$TEMPPERMGENMAXMEM" ]
then
        PERMGENMAXMEM="$TEMPPERMGENMAXMEM"
else
        PERMGENMAXMEM=""
fi

EA_OPTS=""
if [ "$ENABLE_ASSERTIONS" == "true" ]
then
   EA_OPTS="-ea"
fi

if [ -d $PHENOTIPS_HOME/tomcat/webapps ]
then
     GC_FIFO=/usr/local/ingenuity/logs/$APPNAME/gc.out

           if [[ "$PERMGENMAXMEM" != "" ]]
           then
	      JAVA_OPTS="$EA_OPTS $JAVA_VM $DEBUG_OPTS -Xms$MINMEM -Xmx$MAXMEM -XX:MaxPermSize=$PERMGENMAXMEM -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC -Xloggc:$GC_FIFO -verbose:gc -XX:+PrintGCDateStamps -Duser.timezone="America/Los_Angeles" -Dsun.io.useCanonCaches=false -Djava.awt.headless=true $USER_REGION_OPT $USER_LANG_OPT"
           fi


	CLASSPATH=/usr/local/ingenuity/phenotips/phenotips-live/web/WEB-INF/classes
	CATALINA_OPTS="-Dcom.sun.management.jmxremote  -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=true -Dcom.sun.management.jmxremote.password.file=/usr/local/ingenuity/phenotips/phenotips-live/tomcat/conf/jmxpass -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true"
	export JAVA_VM CLASSPATH JAVA_OPTS CATALINA_OPTS

	PATH=$PATH:/usr/sbin:/usr/bin:/usr/local/bin
	PHENOTIPS_LOGDIR=/usr/local/ingenuity/logs/phenotips
	RUN_AS="sudo -u build"
	cd /usr/local/ingenuity/logs/phenotips

	case "$1" in
	'start')
        	echo "starting PHENOTIPS web application"
        	$RUN_AS nohup /usr/local/ingenuity/phenotips/phenotips-live/tomcat/bin/ipatomcat.sh /usr/local/ingenuity/phenotips/phenotips-live/tomcat start  &
                if [ $? -eq 0 ]
                then
                        TIMESTAMP=`$PHENOTIPS_HOME/../install/getTimestamp.pl`
                        #logger -p uucp.info -t $APPNAME-$PORT "Ingenuity recorder: application is starting up" &
                        logger -p uucp.info -t $APPNAME-$PORT "$TIMESTAMP Ingenuity recorder: application is starting up" &
                fi


        ;;

	'stop')
#--- Remove crontab entry
        	echo "stopping PHENOTIPS web application"
        	$RUN_AS nohup /usr/local/ingenuity/phenotips/phenotips-live/tomcat/bin/ipatomcat.sh /usr/local/ingenuity/phenotips/phenotips-live/tomcat stop
                if [ $? -eq 0 ]
                then
                        TIMESTAMP=`$PHENOTIPS_HOME/../install/getTimestamp.pl`
                        logger -p uucp.info -t $APPNAME-$PORT "$TIMESTAMP Ingenuity recorder: application is shutting down" &
                fi
		sleep 3

                PIDLIST=`ps auxww | egrep -e "$PSPATTERN" | grep -v grep | awk '{print $2}'`
                NPID=$( echo $PIDLIST | wc -w )
                echo ""
                echo "Found $NPID tomcat processes running ...."
                for pid in $PIDLIST
                do
                        killpid $pid
                done

        ;;


	*)
        	echo "usage: $0 (start|stop)"
        	exit 1
       		;;
	esac

else
	echo "Tomcat not installed. Please check the deployment package and process"
fi
