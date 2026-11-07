### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:01:10Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Manages the queue worker lifecycle and checks whether its heartbeat is recent enough.

#### Purpose
- Starts, stops, restarts, and reports status for the queue worker; `health` checks that it is running and its heartbeat is fresh.
- Run directly as an init-style command wrapper.

#### Type
- POSIX `sh` (`#!/bin/sh`); no strict-mode settings.

#### Input
- First positional argument selects `start`, `stop`, `restart`, `status`, or `health`.
- Environment: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file to check or signal the worker; reads the heartbeat file’s modification time during `health`.

#### Output
- Starts `/usr/local/bin/queue-worker` with `--heartbeat "$HEARTBEAT_FILE"`; records its PID in `PID_FILE`.
- Stops the worker and removes `PID_FILE` after a successful `kill`.
- Prints `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat ...s old`, `OK`, or usage text.
- Exit statuses: `0` for successful commands, running status, or healthy worker; `1` for stale heartbeat; `2` when health check finds the worker stopped; `3` for stopped status; `64` for an unsupported command.

#### Core logic
- `start`: if the PID file identifies a live process, prints `already running`; otherwise launches the worker with `nohup` and writes its PID.
- `stop`: returns successfully if not running; otherwise signals the PID and removes the PID file if `kill` succeeds.
- `restart`: runs stop, then start.
- `status`: prints `running` if live; otherwise prints `stopped` and exits `3`.
- `health`: exits `2` with a critical message if stopped; otherwise computes heartbeat age, exits `1` with a warning if age exceeds 120 seconds, or prints `OK`.
- Unsupported command: prints usage to standard error and exits `64`.

#### Functions
- `is_running`: checks PID file and process liveness.
- `heartbeat_age`: calculates seconds since heartbeat modification.
- `start_worker`: launches worker and records PID.
- `stop_worker`: signals worker and removes PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `cat`, `date`, `stat`, `echo`, `kill`, `nohup`, `rm`.
- Worker executable: `/usr/local/bin/queue-worker`.
- Files: `/run/queue-worker.pid`; `/var/lib/queue-worker/heartbeat`.

#### Errors and exit codes
- `0`: successful command; worker running for `status`; heartbeat within limit for `health`.
- `1`: heartbeat age exceeds `MAX_HEARTBEAT_AGE`.
- `2`: `health` finds no running worker.
- `3`: `status` finds no running worker.
- `64`: unsupported command.
- No traps, retries, or timeouts.
