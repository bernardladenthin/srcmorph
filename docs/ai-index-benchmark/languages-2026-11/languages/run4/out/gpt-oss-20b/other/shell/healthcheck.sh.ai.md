### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:31:27Z
- G: 
- A: 0.0.0
- X: file
---
> Manages a queue worker process and reports its health via PID and heartbeat checks.

#### Purpose
- Controls a queue worker: start, stop, restart, status, health.
- Intended to be run directly as a command-line utility.

#### Type
- POSIX sh (`#!/bin/sh`).
- No strict-mode flags (`set -e`, `set -u`, `set -o pipefail`).

#### Input
- `$1`: `start`, `stop`, `restart`, `status`, `health`.
- Environment variables:
  - `PID_FILE` (default `/run/queue-worker.pid`).
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).
- Constants:
  - `MAX_HEARTBEAT_AGE=120`.
  - `WORKER_BIN=/usr/local/bin/queue-worker`.

#### Output
- Writes PID to `PID_FILE` on start.
- Removes `PID_FILE` on stop.
- Prints status messages (`running`, `stopped`, `OK`, `WARNING`, `CRITICAL`).
- Exit codes: `0` success, `1` warning, `2` critical, `3` stopped, `64` usage error.

#### Core logic
- `is_running`: checks PID file and verifies process.
- `heartbeat_age`: computes age of heartbeat file.
- `start_worker`: starts worker with `nohup`, writes PID.
- `stop_worker`: kills process, removes PID file.
- `case` dispatches based on `$1`:
  - `start`: `start_worker`.
  - `stop`: `stop_worker`.
  - `restart`: `stop_worker`; `start_worker`.
  - `status`: prints `running` or `stopped`; exits `3` if stopped.
  - `health`: verifies running; checks heartbeat age; exits `1` if too old, `2` if not running, otherwise prints `OK`.
  - default: usage message; exits `64`.

#### Functions
- `is_running`: checks if worker process exists.
- `heartbeat_age`: calculates seconds since heartbeat file modification.
- `start_worker`: launches worker, records PID.
- `stop_worker`: terminates worker, cleans PID file.

#### Constants and configuration
- `PID_FILE=/run/queue-worker.pid`
- `HEARTBEAT_FILE=/var/lib/queue-worker/heartbeat`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `kill`, `nohup`, `date`, `stat`, `cat`, `echo`.
- Worker binary: `/usr/local/bin/queue-worker`.

#### Errors and exit codes
- `exit 2`: health check fails because worker not running.
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.
- `exit 3`: status check finds worker stopped.
- `exit 64`: invalid command usage.
- `exit 0`: successful operations.
