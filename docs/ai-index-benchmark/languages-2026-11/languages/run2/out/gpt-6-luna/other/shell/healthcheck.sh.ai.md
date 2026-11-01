### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:32:58Z
- G: 
- A: 0.0.0
- X: file
---
> Starts, stops, and monitors the queue worker, including checking whether its heartbeat is stale.

#### Purpose
- Provides init-style `start`, `stop`, `restart`, `status`, and `health` operations for the queue worker.
- Run directly as a command; not a sourced library.

#### Type
- POSIX `sh`; no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- First positional argument selects `start`, `stop`, `restart`, `status`, or `health`; parsed by a `case` statement.
- Environment variables: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and heartbeat file.

#### Output
- Starts the worker with output redirected to `/dev/null`; writes its PID to `PID_FILE`.
- Removes `PID_FILE` after a successful stop signal.
- Prints operation results: `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat Ns old`, `OK`, or usage text.
- Worker is configured to write its heartbeat to `HEARTBEAT_FILE`.
- Exit statuses: `0` on successful operations and healthy status; `1` for stale heartbeat; `2` when health check finds the worker stopped; `3` when status finds it stopped; `64` for an unrecognized command.

#### Core logic
- Checks whether the worker is running by testing for `PID_FILE` and sending signal 0 to its recorded PID.
- `start`: reports `already running` if active; otherwise launches `WORKER_BIN` with `nohup` and `--heartbeat`, then records `$!` in `PID_FILE`.
- `stop`: does nothing if not running; otherwise sends the default termination signal and removes `PID_FILE` if `kill` succeeds.
- `restart`: runs stop, then start.
- `status`: prints `running` if active; otherwise prints `stopped` and exits with status 3.
- `health`: exits with status 2 and prints a critical message if stopped; otherwise computes heartbeat age and warns with status 1 if older than `MAX_HEARTBEAT_AGE`, or prints `OK`.
- For other arguments, prints usage to stderr and exits with status 64.

#### Functions
- `is_running` — checks PID file and process.
- `heartbeat_age` — calculates heartbeat age in seconds.
- `start_worker` — launches worker and records PID.
- `stop_worker` — terminates worker and removes PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"` — PID file path, overridable by environment.
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"` — heartbeat path, overridable by environment.
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`
- `age=$(heartbeat_age)` — heartbeat age used by the health check.

#### Dependencies
- Worker executable: `/usr/local/bin/queue-worker`.
- External commands: `cat`, `date`, `stat`, `nohup`, `kill`, `rm`.
- Uses `/run/queue-worker.pid` and `/var/lib/queue-worker/heartbeat` by default.

#### Errors and exit codes
- `0` — successful start, stop, restart, or healthy status; start also returns success if already running.
- `1` — health check finds heartbeat older than 120 seconds.
- `2` — health check finds worker not running.
- `3` — status check finds worker stopped.
- `64` — unsupported command.
- No traps, retries, or timeouts.
