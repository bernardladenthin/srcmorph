### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:38:49Z
- G: 
- A: 0.0.0
- X: file
---
> Starts and controls a queue worker, reporting its process status and whether its heartbeat is fresh.

#### Purpose
- Provides init-style start, stop, restart, status, and health-check commands for `queue-worker`.
- Health checks require a live PID and a heartbeat no older than the configured limit.

#### Type
- POSIX shell script (`#!/bin/sh`).

#### Input
- First positional argument `$1`: command `start`, `stop`, `restart`, `status`, or `health`.
- Environment variables `PID_FILE` and `HEARTBEAT_FILE`, if set.
- Reads the PID file and heartbeat file metadata.

#### Output
- Starts `queue-worker` in the background and writes its PID to `PID_FILE`.
- Prints status or health messages; returns command-specific exit codes.

#### Core logic
- `is_running` checks that `PID_FILE` exists and its PID responds to `kill -0`.
- `start_worker` reports `already running` if active; otherwise launches the worker with `nohup` and records `$!`.
- `stop_worker` does nothing if inactive; otherwise sends `kill` and removes the PID file on success.
- `restart` runs stop, then start.
- `status` prints `running` or `stopped`.
- `health` reports CRITICAL if the worker is not running; otherwise compares heartbeat age with the maximum, reports WARNING if stale, or prints `OK`.
- `heartbeat_age` subtracts heartbeat modification time from the current epoch time; missing or unreadable heartbeat metadata falls back to timestamp `0`.

#### Public API
- `start` → starts worker; prints `already running` if active.
- `stop` → stops worker and removes PID file on successful kill.
- `restart` → stops, then starts worker.
- `status` → reports whether worker is running.
- `health` → checks process and heartbeat freshness.
- `is_running()` → shell status; checks PID file and process.
- `heartbeat_age()` → prints age in seconds.
- `start_worker()` → launches worker and records PID.
- `stop_worker()` → kills worker and removes PID file.
- `main` → no named function; top-level `case "$1"` dispatches commands.

#### Constants and configuration
- `PID_FILE` — environment override; default `/run/queue-worker.pid`.
- `HEARTBEAT_FILE` — environment override; default `/var/lib/queue-worker/heartbeat`.
- `MAX_HEARTBEAT_AGE` — `120` seconds.
- `WORKER_BIN` — `/usr/local/bin/queue-worker`.
- Health exit codes: `0` healthy, `1` stale heartbeat, `2` not running.
- Status exit code: `3` when stopped.
- Invalid-command exit code: `64`.

#### Dependencies
- POSIX shell utilities: `cat`, `date`, `stat`, `echo`, `kill`, `nohup`, `rm`.
- Worker executable: `/usr/local/bin/queue-worker`, invoked with `--heartbeat "$HEARTBEAT_FILE"`.

#### Exceptions / Errors
- Invalid or missing command prints usage to stderr and exits `64`.
- `status` exits `3` when the worker is stopped.
- `health` exits `2` when the worker is not running and `1` when heartbeat age exceeds `120` seconds.
- Failed `kill` in `stop_worker` prevents PID-file removal.
