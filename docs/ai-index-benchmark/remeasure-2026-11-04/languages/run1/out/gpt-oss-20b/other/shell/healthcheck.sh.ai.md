### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:59:42Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> A POSIX init-style wrapper that manages a queue worker, providing start, stop, status, restart, and health checks based on a PID file and heartbeat file age.

#### Purpose
- Controls a queue worker process.
- Exposes init-style commands: `start`, `stop`, `restart`, `status`, `health`.

#### Type
- POSIX sh (`#!/bin/sh`).
- No strict mode flags used.

#### Input
- `$1`: command (`start|stop|restart|status|health`).
- Environment variables:
  - `PID_FILE` (default `/run/queue-worker.pid`).
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).
- No additional options or arguments.

#### Output
- Writes PID to `$PID_FILE` on start.
- Removes `$PID_FILE` on stop.
- Prints status messages (`running`, `stopped`, `OK`, `WARNING`, `CRITICAL`).
- Exits with codes: `0` success, `1` warning, `2` critical, `3` stopped, `64` usage error.

#### Core logic
- `is_running`: checks if PID file exists and process is alive.
- `heartbeat_age`: computes seconds since last heartbeat file modification.
- `start_worker`: starts worker with `nohup`, records PID.
- `stop_worker`: kills process and removes PID file.
- Command dispatch via `case` on `$1`:
  - `start`: invoke `start_worker`.
  - `stop`: invoke `stop_worker`.
  - `restart`: stop then start.
  - `status`: report running/stopped, exit 3 if stopped.
  - `health`: verify running and heartbeat age ≤ `MAX_HEARTBEAT_AGE` (120s); warn if older.
  - default: usage message, exit 64.

#### Functions
- `is_running`: checks process existence.
- `heartbeat_age`: calculates heartbeat age.
- `start_worker`: launches worker, records PID.
- `stop_worker`: terminates worker, cleans PID file.

#### Constants and configuration
- `PID_FILE`=`/run/queue-worker.pid`
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`
- `MAX_HEARTBEAT_AGE`=`120`
- `WORKER_BIN`=`/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `kill`, `nohup`, `date`, `stat`, `cat`.
- Worker binary: `/usr/local/bin/queue-worker` (expects `--heartbeat` option).

#### Errors and exit codes
- `exit 2`: worker not running during health check.
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.
- `exit 3`: status reports stopped.
- `exit 64`: invalid command usage.
