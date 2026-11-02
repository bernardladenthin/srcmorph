### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:27:19Z
- G: 
- A: 0.0.0
- X: file
---
> Starts, stops, reports status for, and health-checks the queue worker using its PID file and heartbeat age.

#### Purpose
- Provides init-style `queue-worker` lifecycle commands and checks whether its heartbeat is recent.
- Run directly as a command-line script.

#### Type
- POSIX `sh` (`#!/bin/sh`); no strict-mode settings.

#### Input
- First positional argument `$1`: command selected by a `case` loop; accepted values are `start`, `stop`, `restart`, `status`, and `health`.
- Environment variable `PID_FILE`; defaults to `/run/queue-worker.pid`.
- Environment variable `HEARTBEAT_FILE`; defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and heartbeat file metadata.

#### Output
- Prints `already running` for a start request when the worker is running; otherwise prints no success message.
- Prints `running` or `stopped` for `status`.
- Prints `CRITICAL: not running`, `WARNING: heartbeat ${age}s old`, or `OK` for `health`.
- Prints usage to standard error for an unrecognized command.
- Starts or signals the worker process; creates or removes the PID file.
- Explicit exit statuses: `0` for successful status/start/stop paths and healthy health check; `1` for stale heartbeat; `2` when health check finds the worker stopped; `3` when status finds it stopped; `64` for an unrecognized command. Other command failures may affect the resulting status.

#### Core logic
- Checks whether the PID file exists and whether its PID responds to `kill -0`.
- `start`: if running, prints `already running`; otherwise launches the worker with `nohup` and records its background PID.
- `stop`: if running, sends `kill` to the recorded PID and removes the PID file on successful kill; otherwise does nothing.
- `restart`: runs stop, then start.
- `status`: prints `running` if the worker is running; otherwise prints `stopped` and exits `3`.
- `health`: exits `2` with a critical message if stopped; otherwise calculates heartbeat age, warns and exits `1` if age exceeds 120 seconds, or prints `OK`.
- For unknown commands, prints usage to standard error and exits `64`.

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

#### Dependencies
- External commands: `cat`, `date`, `echo`, `kill`, `nohup`, `rm`, `stat`.
- Worker executable: `/usr/local/bin/queue-worker`.
- Worker option: `--heartbeat` with the configured heartbeat file.
- PID file: configured by `PID_FILE`.
- Heartbeat file: configured by `HEARTBEAT_FILE`; its modification time is read with `stat -c %Y`.
