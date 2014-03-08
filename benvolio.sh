#!/bin/sh

PID=benvolio.pid

COMMAND=`cat <<EOF
  java -Xms256m -Xmx1g \
  -jar target/benvolio-0.0.1-jar-with-dependencies.jar \
  "$@"
EOF
`

start() {
    if [ -f $PID ]
    then
        echo
        echo "Already started. PID: [$( cat $PID )]"
    else
        echo "==== Starting Benvolio Server ===="
        touch $PID
        if nohup $COMMAND 1>stdout.log 2>stderr.log &
        then echo $! >$PID
             echo "Done."
        else echo "Error... "
             /bin/rm $PID
        fi
    fi
}

stop() {
    echo "==== Stopping Benvolio Server ===="

    if [ -f $PID ]
    then
        if kill $( cat $PID )
        then echo "Done."
        fi
        /bin/rm $PID
    else
        echo "No pid file. Already stopped?"
    fi
}

case "$1" in
    'start')
            start
            ;;
    'stop')
            stop
            ;;
    'restart')
            stop ; echo "Sleeping..."; sleep 1 ;
            start
            ;;
    *)
            echo
            echo "Usage: $0 { start | stop | restart }"
            echo
            exit 1
            ;;
esac

exit 0
