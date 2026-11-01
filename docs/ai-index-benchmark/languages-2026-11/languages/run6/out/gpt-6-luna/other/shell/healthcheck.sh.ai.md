### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:37:40Z
- G: 
- A: 0.0.0
- X: file
---
> Manages the queue worker lifecycle and checks whether its heartbeat is fresh.

#### Purpose
- Provides init-style `start`, `stop`, `restart`, `status`, and `health` commands for the queue worker.
- Run directly; not a sourced library.

#### Type
- POSIX `sh` (`#!/bin/sh`); no strict-mode settings.

#### Input
- First positional argument selects the command: `start`, `stop`, `restart`, `status`, or `health`; parsed with a `case` statement.
- Environment variables: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and heartbeat file metadata.

#### Output
- Writes the worker PID to `PID_FILE`; removes it on successful stop.
- Starts `WORKER_BIN` with `--heartbeat "$HEARTBEAT_FILE"`; redirects its output to `/dev/null`.
- Prints `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: <age>s old`, `OK`, or usage text.
- Exit statuses: `0` for successful start, stop, restart, healthy probe, or running status; `1` for stale heartbeat; `2` when health check finds the worker stopped; `3` when status finds it stopped; `64` for an unknown command.

#### Core logic
- `start`: if the PID file identifies a live process, print `already running`; otherwise launch the worker with `nohup` and save its PID.
- `stop`: if no live worker is found, do nothing; otherwise kill the PID and remove the PID file if the kill succeeds.
- `restart`: run stop, then start.
- `status`: print `running` when the PID file identifies a live process; otherwise print `stopped` and exit `3`.
- `health`: exit `2` with a critical message if the worker is not running; otherwise compare heartbeat age with `MAX_HEARTBEAT_AGE`, exit `1` with a warning if too old, or print `OK`.

#### Functions
- `is_running` — check PID file and process.
- `heartbeat_age` — calculate heartbeat age in seconds.
- `start_worker` — launch worker and save PID.
- `stop_worker` — kill worker and remove PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`
- `now=$(date +%s)`
- `beat=$(stat -c %Y "$HEARTBEAT_FILE" 2>/dev/null || echo 0)`
- `age=$(heartbeat_age)`

#### Dependencies
- External commands: `cat`, `date`, `echo`, `kill`, `nohup`, `rm`, `stat`.
- Worker executable: `/usr/local/bin/queue-worker`; invoked with the `--heartbeat` option and the configured heartbeat-file path.

#### Errors and exit codes
- Exit `1` when heartbeat age exceeds 120 seconds; a missing or unreadable heartbeat file falls back to timestamp `0`.
- Exit `2` when health check finds no running worker.
- Exit `3` when status finds no running worker.
- Exit `64` for an unsupported command; usage text goes to standard error.
- No traps, retries, or timeouts.
