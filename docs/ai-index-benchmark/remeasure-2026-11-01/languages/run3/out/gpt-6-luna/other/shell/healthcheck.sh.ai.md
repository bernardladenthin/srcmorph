### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:55:46Z
- G: 
- A: 0.0.0
- X: file
---
> Starts and manages the queue worker, with status and heartbeat-age health checks.

#### Purpose
- Provides init-style `start`, `stop`, `restart`, `status`, and `health` operations for the queue worker.
- Run directly with an operation as the first argument.

#### Type
- POSIX `sh` (`#!/bin/sh`); no strict-mode settings.

#### Input
- `$1`: operation, matched by a `case` loop: `start`, `stop`, `restart`, `status`, or `health`.
- Environment variables: `PID_FILE`, default `/run/queue-worker.pid`; `HEARTBEAT_FILE`, default `/var/lib/queue-worker/heartbeat`.
- Reads `PID_FILE` to get the worker PID; checks whether `PID_FILE` exists.
- Reads the heartbeat file’s modification time via `stat`; a missing or unreadable heartbeat falls back to timestamp `0`.

#### Output
- Starts `/usr/local/bin/queue-worker` with `--heartbeat "$HEARTBEAT_FILE"`; writes its PID to `PID_FILE`.
- Stops the worker and removes `PID_FILE` after a successful `kill`.
- Prints operation results: `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat Ns old`, or `OK`.
- Prints usage to stderr for an unknown operation.
- Exit statuses: `0` for successful operations and healthy checks; `1` for a stale heartbeat; `2` when health check finds the worker stopped; `3` when `status` finds it stopped; `64` for an unknown operation.

#### Core logic
- `start`: if the PID file identifies a running process, prints `already running`; otherwise launches the worker with `nohup` and writes its PID to `PID_FILE`.
- `stop`: does nothing if the worker is not running; otherwise sends `kill` and removes `PID_FILE` on success.
- `restart`: runs stop, then start.
- `status`: prints `running` if the worker is running; otherwise prints `stopped` and exits `3`.
- `health`: exits `2` with a critical message if the worker is not running; otherwise compares heartbeat age against `MAX_HEARTBEAT_AGE`, exiting `1` with a warning if it is too old, or printing `OK`.

#### Functions
- `is_running`: checks PID file and process.
- `heartbeat_age`: calculates heartbeat age in seconds.
- `start_worker`: launches the worker and records its PID.
- `stop_worker`: stops the worker and removes its PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `cat`, `date`, `kill`, `nohup`, `rm`, `stat`.
- Worker executable: `/usr/local/bin/queue-worker`.

#### Errors and exit codes
- `status` exits `3` when the worker is not running.
- `health` exits `2` when the worker is not running; exits `1` when heartbeat age exceeds `MAX_HEARTBEAT_AGE`.
- Unknown operation prints usage to stderr and exits `64`.
- No traps, retries, or timeouts.
