### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:15:49Z
- G: 
- A: 0.0.0
- X: file
---
> Manages the queue worker lifecycle and checks whether its heartbeat is fresh.

#### Purpose
- Starts, stops, restarts, and reports the status or health of the queue worker.
- Run directly as an init-style command wrapper.

#### Type
- POSIX `sh` (`#!/bin/sh`); no strict-mode settings.

#### Input
- `$1`: command selected by a `case` loop; accepted values are `start`, `stop`, `restart`, `status`, and `health`.
- Environment variables: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and heartbeat file; `stat` reads the heartbeat file’s modification time.

#### Output
- Starts `queue-worker` in the background, writes its PID to `PID_FILE`, and removes `PID_FILE` after a successful stop.
- Prints `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat …s old`, `OK`, or usage text.
- Exit statuses: `0` for successful commands and healthy status; `1` for stale heartbeat; `2` when health check finds the worker stopped; `3` when `status` finds it stopped; `64` for an unrecognized command.
- No traps or cleanup handlers.

#### Core logic
- `start`: if the PID file identifies a live process, print `already running`; otherwise launch `queue-worker` with `nohup`, pass the heartbeat path, suppress its output, and write its PID.
- `stop`: if the worker is running, send it `TERM` and remove the PID file if `kill` succeeds.
- `restart`: run stop, then start.
- `status`: print `running` if the worker is running; otherwise print `stopped` and exit `3`.
- `health`: exit `2` with a critical message if the worker is not running; otherwise compare heartbeat age to 120 seconds, exit `1` with a warning if older, or print `OK`.

#### Functions
- `is_running`: Check PID file and process.
- `heartbeat_age`: Calculate heartbeat age in seconds.
- `start_worker`: Launch worker and record PID.
- `stop_worker`: Terminate worker and remove PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `cat`, `date`, `echo`, `kill`, `nohup`, `rm`, `stat`.
- Worker executable: `/usr/local/bin/queue-worker`, invoked with `--heartbeat` and the heartbeat-file path.
