### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:48:25Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> A POSIX‑style init wrapper that manages a queue worker, providing start, stop, status, restart, and health‑check commands based on a PID file and heartbeat file.

#### Purpose
- Controls a queue worker process.
- Exposes `start`, `stop`, `restart`, `status`, and `health` actions.

#### Type
- POSIX sh (`#!/bin/sh`).
- No strict mode flags used.

#### Input
- `$1`: command (`start`, `stop`, `restart`, `status`, `health`).
- Environment variables:
  - `PID_FILE` (default `/run/queue-worker.pid`).
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).
- No command‑line options parsed.

#### Output
- Writes PID to `PID_FILE` when starting.
- Removes `PID_FILE` on stop.
- Prints status messages to stdout or stderr.
- Exits with codes:
  - `0` on success.
  - `1` if heartbeat too old.
  - `2` if not running during health check.
  - `3` if status reports stopped.
  - `64` on usage error.

#### Core logic
- `is_running`: checks PID file and verifies process existence.
- `heartbeat_age`: calculates seconds since heartbeat file modification.
- `start_worker`: starts worker with `nohup`, records PID.
- `stop_worker`: kills process and removes PID file.
- Command dispatch via `case` on `$1`.

#### Functions
- `is_running`: check if worker is active.
- `heartbeat_age`: compute heartbeat age.
- `start_worker`: launch worker, write PID.
- `stop_worker`: terminate worker, clean PID.

#### Constants and configuration
- `PID_FILE`=`/run/queue-worker.pid`
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`
- `MAX_HEARTBEAT_AGE`=`120`
- `WORKER_BIN`=`/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `nohup`, `kill`, `cat`, `date`, `stat`, `echo`.
- Worker binary: `/usr/local/bin/queue-worker` (expects `--heartbeat` option).

#### Errors and exit codes
- `exit 64`: usage error.
- `exit 2`: health check fails because worker not running.
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.
- `exit 3`: status reports stopped.
