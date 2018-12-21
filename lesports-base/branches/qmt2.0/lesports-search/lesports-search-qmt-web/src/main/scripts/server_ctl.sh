#!/bin/bash

function usage(){
  echo "Usage: $0 (start|stop|restart) options"
  echo "options are"
  echo "    -p: Required. Port number the server will bind to"
  echo "                  Default port number is 8202"
  echo "    -n: Optional. Set the namespace or package to find the app. "
  echo "                  Default value is com.lesports.user.api.web"
  echo "    -m: Optional. Entry class name without package. Default is Main."
  echo "    -s: Optional. Service Name. Default is lesports-user-api-web."
}

function isAlive(){
    echo `ps --no-headers -p $1 | wc -l`
}

PKG="com.lesports.search.qmt"
NAME="SearchApiServer"
# Default port.
CMD=""
SERVICE_NAME="lesports-search-qmt-web"
# Parse the command line arguments.
while [ $# -gt 0 ]
do
    case "$1" in
        start|stop|restart )
            CMD="$1";;
        -n )
            PKG=$2; shift;;
        -p )
            PORT=$2; shift;;
        -m )
            NAME=$2; shift;;
        -s )
            SERVICE_NAME=$2; shift;;
        *)
            usage;exit 1;;
     esac
     shift
 done
 # Set the default value.
if [ -z "${CMD}" -o ${PORT} -le 0 ]
then
    echo "miss one of (start|stop|restart) or port parmeter."
    usage
    exit 1
fi
LOG_DIR="/letv/logs/${SERVICE_NAME}"
# Make sure the LOG DIR exists.
if [ ! -d "${LOG_DIR}" ]
then
    echo "Log dir ${LOG_DIR} do not exist, create it."
    mkdir -p ${LOG_DIR}
fi
GC_LOG="${LOG_DIR}/gc-${PORT}.log"
STD_LOG="${LOG_DIR}/std-${PORT}.log"
DUMP_LOG="${LOG_DIR}/dump-${PORT}.log"
PID_FILE="${LOG_DIR}/${SERVICE_NAME}-${PORT}.pid"

PHOME=$(dirname `readlink -f "$0"`)
PHOME=$(dirname $PHOME)

IP_ETH0=`ifconfig eth0 | awk '/inet addr/ {print $2}' | awk -F: '{print $2}'`

JAVA_OPTS="-server -d64 ${jvm.args} \
-Xss256k -XX:+UseConcMarkSweepGC \
-XX:+UseParNewGC -XX:CMSFullGCsBeforeCompaction=5 \
-XX:+UseCMSCompactAtFullCollection \
-XX:+PrintGCDetails -Xloggc:${GC_LOG} \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=${DUMP_LOG} \
-javaagent:/letv/apps/pinpoint-agent/pinpoint-bootstrap-1.6.0-SNAPSHOT.jar \
-Dpinpoint.agentId=19381 \
-Dpinpoint.applicationName=search-qmt-press \
-Dlog.path=${STD_LOG}"

function start() {
    java ${JAVA_OPTS} -cp ${PHOME}/classes:${PHOME}/lib/* ${PKG}.${NAME} ${PORT} >> ${STD_LOG} 2>&1 &
    echo $! > ${PID_FILE}
    pid=`cat ${PID_FILE}`
    echo "Starting Java process ${PKG}.${NAME} on port ${PORT} with pid ${pid}: "
    alive=`isAlive ${pid}`
    if [ ${alive} -eq 1 ]
    then
        echo "Succeed."
    else
        echo "Failed."
        exit 1
    fi
    echo "STD LOG : ${STD_LOG}"
    echo "GC LOG  : ${GC_LOG}"
    echo "PID FILE: ${PID_FILE}"
}

function stop() {
    if [ -f "${PID_FILE}" ]
    then
        pid=`cat ${PID_FILE}`
        if [ -n "${pid}" -a ${pid} -gt 0 ]
        then
            alive=`isAlive ${pid}`
            if [ ${alive} -eq 1 ]
            then
                # Print Thread Dump First(HotSpot VM).
                # SIGQUIT
                kill -3 ${pid}
                # Kill the process normally and wait for 3 seconds give the application a chance to quit gracefully.
                # SIGTERM
                kill ${pid} && sleep 3

                alive=`isAlive ${pid}`
                if [ ${alive} -eq 1 ]
                then
                    # Force Kill if the process is still there alive.
                    # SIGKILL

                    kill -9 ${pid}
                fi #
            fi #Process is alive.
        fi # PID exists
        # Remove the pid file.
        /bin/rm ${PID_FILE}
    fi
}

function restart(){
    stop
    #sleep 3 seconds to wait process close sucessfully
    sleep 3
    start
}
# Run the command.
${CMD}
