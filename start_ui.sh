#!/bin/bash

APP_NAME=key-core-web/example/key-core-web.jar

pid=`ps -ef|grep $APP_NAME | grep -v grep | awk '{print $2}'`
kill -9 $pid
echo "$pid killed"

sleep 2

if test -e $APP_NAME
then
echo 'starting'

java -jar $APP_NAME 

