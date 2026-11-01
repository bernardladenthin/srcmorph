### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:28:07Z
- G: 
- A: 0.0.0
- X: file
---
> Starts and manages a queue worker, reporting its process status and whether its heartbeat is recent.

#### Purpose
- Provides init-style start, stop, restart, status, and health-probe commands for `queue-worker`.
- Checks worker liveness using a PID file and heartbeat freshness using the heartbeat file’s modification time.

#### Type
- POSIX shell script (`#!/bin/sh`).

#### Input
- First argument, `$1`: command name.
- `PID_FILE`: environment override for the worker PID file.
- `HEARTBEAT_FILE`: environment override for the worker heartbeat file.
- Reads the PID file and heartbeat file metadata.

#### Output
- Writes worker PID to `PID_FILE`; removes it after a successful stop.
- Prints status or health messages to stdout; prints usage to stderr.
- Launches `WORKER_BIN` with `--heartbeat "$HEARTBEAT_FILE"` in the background.
- Uses exit codes: `0` for success, `1` for stale heartbeat, `2` for not running, `3` for stopped status, `64` for invalid command.

#### Core logic
- `is_running` checks that `PID_FILE` exists and that `kill -0` succeeds for its contents.
- `heartbeat_age` calculates current epoch time minus the heartbeat file’s modification time; if `stat` fails, uses timestamp `0`.
- `start_worker` reports `already running` if the worker is live; otherwise launches it with `nohup` and records its PID.
- `stop_worker` does nothing if the worker is not live; otherwise sends `kill` and removes `PID_FILE` on success.
- Dispatches `$1` to `start`, `stop`, `restart` (stop then start), `status`, or `health`.
- `status` prints `running` or prints `stopped` and exits `3`.
- `health` reports `CRITICAL: not running` and exits `2` if the worker is down; otherwise warns and exits `1` when heartbeat age exceeds the limit, or prints `OK`.

#### Public API
- `start` → starts the queue worker.
- `stop` → stops the queue worker.
- `restart` → stops then starts the queue worker.
- `status` → reports worker process status.
- `health` → checks process and heartbeat freshness.
- `start_worker()` → launches worker if stopped.
- `stop_worker()` → terminates worker if running.
- `is_running()` → checks PID-file process liveness.
- `heartbeat_age()` → returns heartbeat age in seconds.
- Script entry point: dispatches `$1` command.

#### Constants and configuration
- `PID_FILE` — environment override; default `/run/queue-worker.pid`.
- `HEARTBEAT_FILE` — environment override; default `/var/lib/queue-worker/heartbeat`.
- `MAX_HEARTBEAT_AGE` — `120` seconds.
- `WORKER_BIN` — `/usr/local/bin/queue-worker`.
- Command names: `start`, `stop`, `restart`, `status`, `health`.

#### Dependencies
- POSIX utilities: `cat`, `date`, `echo`, `kill`, `nohup`, `rm`, `stat`.
- Worker executable: `/usr/local/bin/queue-worker`.

#### Exceptions / Errors
- Invalid command prints `usage: $0 {start|stop|restart|status|health}` to stderr and exits `64`.
- `health` exits `2` with `CRITICAL: not running` when the PID check fails.
- `health` exits `1` with a heartbeat-age warning when age exceeds `120` seconds.
- `status` exits `3` when the worker is stopped.
- Failed `kill` prevents `stop_worker` from removing the PID file.
