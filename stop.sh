#!/bin/bash
kill -9 $(cat run.pid) &&
DATE=`date +%Y-%m-%d-%H-%M` &&
mv -f log.log logs/${DATE}_log.log
echo "进程状态:"
echo "$(ps -p $(cat run.pid))"
rm -f run.pid
