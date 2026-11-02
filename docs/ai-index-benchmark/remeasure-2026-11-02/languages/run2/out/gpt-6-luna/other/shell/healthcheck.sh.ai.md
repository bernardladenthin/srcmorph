### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:40:20Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Manages the queue worker lifecycle and reports whether its process and heartbeat are healthy.

#### Purpose
- Provides start, stop, restart, status, and heartbeat health checks for the queue worker.
- Run directly as an init-style command-line script.

#### Type
- POSIX `sh` (`#!/bin/sh`); no strict-mode settings.

#### Input
- `$1`: action selected by a `case` loop: `start`, `stop`, `restart`, `status`, or `health`.
- Environment variables: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file at `PID_FILE` and heartbeat file at `HEARTBEAT_FILE`.

#### Output
- Starts or stops the queue-worker process; creates or removes `PID_FILE`.
- Prints lifecycle/status messages and health results to standard output; usage text goes to standard error.
- Exit statuses: `0` for successful actions, running status, and healthy heartbeat; `1` for stale heartbeat; `2` when health check finds no running worker; `3` when status finds the worker stopped; `64` for an unknown action.

#### Core logic
- `start`: if the PID file identifies a running process, prints `already running`; otherwise starts `WORKER_BIN` with `--heartbeat HEARTBEAT_FILE` via `nohup`, suppresses its output, and records its PID.
- `stop`: if no running process is found, does nothing; otherwise sends `TERM` and removes the PID file if `kill` succeeds.
- `restart`: runs stop, then start.
- `status`: prints `running` if the PID file identifies a live process; otherwise prints `stopped` and exits `3`.
- `health`: exits `2` with `CRITICAL: not running` if the worker is not running; otherwise compares heartbeat age to `MAX_HEARTBEAT_AGE`, prints a warning and exits `1` if stale, or prints `OK`.
- Unknown actions print usage and exit `64`.

#### Functions
- `is_running` — checks PID file and process.
- `heartbeat_age` — calculates heartbeat age in seconds.
- `start_worker` — launches worker and records PID.
- `stop_worker` — terminates worker and removes PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`
- `age=$(heartbeat_age)`

#### Dependencies
- External commands: `cat`, `date`, `stat`, `echo`, `kill`, `nohup`, `rm`.
- Worker executable: `/usr/local/bin/queue-worker`; invoked with `--heartbeat` and the configured heartbeat path.
- `stat -c %Y` reads the heartbeat file’s modification time.

#### Errors and exit codes
- `0`: successful action, running status, or heartbeat age at most 120 seconds.
- `1`: heartbeat age exceeds `MAX_HEARTBEAT_AGE`; prints `WARNING`.
- `2`: health check finds no running worker; prints `CRITICAL`.
- `3`: status check finds no running worker.
- `64`: unsupported action; usage is printed to standard error.
- Missing or unreadable heartbeat timestamp falls back to `0`; no traps, retries, or timeouts.
