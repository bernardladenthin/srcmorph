#!/bin/sh
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0
#
# POSIX init-style wrapper around a queue worker: start, stop, status and a health probe that
# checks the pid file and the worker's heartbeat file age.

PID_FILE="${PID_FILE:-/run/queue-worker.pid}"
HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"
MAX_HEARTBEAT_AGE=120
WORKER_BIN=/usr/local/bin/queue-worker

is_running() {
    [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null
}

heartbeat_age() {
    now=$(date +%s)
    beat=$(stat -c %Y "$HEARTBEAT_FILE" 2>/dev/null || echo 0)
    echo $((now - beat))
}

start_worker() {
    if is_running; then
        echo "already running"
        return 0
    fi
    nohup "$WORKER_BIN" --heartbeat "$HEARTBEAT_FILE" >/dev/null 2>&1 &
    echo $! > "$PID_FILE"
}

stop_worker() {
    is_running || return 0
    kill "$(cat "$PID_FILE")" && rm -f "$PID_FILE"
}

case "$1" in
    start) start_worker ;;
    stop) stop_worker ;;
    restart) stop_worker; start_worker ;;
    status)
        if is_running; then echo "running"; else echo "stopped"; exit 3; fi
        ;;
    health)
        is_running || { echo "CRITICAL: not running"; exit 2; }
        age=$(heartbeat_age)
        if [ "$age" -gt "$MAX_HEARTBEAT_AGE" ]; then
            echo "WARNING: heartbeat ${age}s old"
            exit 1
        fi
        echo "OK"
        ;;
    *)
        echo "usage: $0 {start|stop|restart|status|health}" >&2
        exit 64
        ;;
esac
