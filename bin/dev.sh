#!/bin/bash

bin_dir=$(dirname "$0")

start() {
  case $1 in
  consumer)
    java -jar "$bin_dir"/../consumer/target/consumer-1.0.0-SNAPSHOT-fat.jar -cluster
    ;;
  provider)
    java -jar "$bin_dir"/../provider/target/provider-1.0.0-SNAPSHOT-fat.jar -cluster
    ;;
  *)
    echo "Unknown module: $1"
    exit 1
    ;;
  esac
}

stop() {
  jcmd | grep "$1-1.0.0-SNAPSHOT-fat.jar" | awk '{print $1}' | xargs -I {} kill -9 {}
}

stopAll() {
  stop consumer
  stop provider
}

case $1 in
start)
  start "$2"
  ;;
stop)
  stop "$2"
  ;;
restart)
  stop "$2"
  start "$2"
  ;;
down)
  stopAll
  ;;
*)
  echo "Usage: $0 {start <module>|stop <module>|restart <module>|down}"
  ;;
esac
