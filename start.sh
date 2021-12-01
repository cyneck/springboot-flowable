#!/bin/bash
nohup java -jar workflow-service-0.0.1-SNAPSHOT.jar \
    --spring.config.location=application.yml \
    --server.port=8081 \
     -Xms2048m \
     -Xmx2048m \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=dumpSnap.hprof \
     >> log.log 2>&1 & echo $! > run.pid
echo "进程状态:"
echo "$(ps -p $(cat run.pid))"
tail -f log.log
