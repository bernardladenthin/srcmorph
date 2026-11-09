### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:04:10Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Manages the queue worker lifecycle and checks whether its process and heartbeat are healthy.

#### Purpose
- Provides start, stop, restart, status, and heartbeat-health operations for the queue worker.
- Run directly with an operation as the first argument; not a sourced library.

#### Type
- POSIX `sh`; no strict-mode settings.

#### Input
- First positional argument: operation `start`, `stop`, `restart`, `status`, or `health`, parsed by a `case` statement.
- Environment variables: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and heartbeat file metadata.

#### Output
- Starts or stops the worker process and creates or removes the PID file.
- Prints status or health messages; usage and critical messages go to standard error or standard output as written.
- Exit statuses: `0` for successful start, stop, restart, running status, or healthy probe; `1` for stale heartbeat; `2` when health check finds the worker stopped; `3` when status finds it stopped; `64` for an unknown operation.
- Other command failures may affect the resulting status.

#### Core logic
- `start`: if the PID file identifies a live process, prints `already running`; otherwise launches the worker with `nohup`, redirects output, and records its PID.
- `stop`: returns immediately if the worker is not running; otherwise sends it a signal and removes the PID file if `kill` succeeds.
- `restart`: runs stop, then start.
- `status`: prints `running` if the PID identifies a live process; otherwise prints `stopped` and exits `3`.
- `health`: exits `2` with `CRITICAL: not running` if the worker is stopped; otherwise compares heartbeat age against 120 seconds, exiting `1` with a warning if stale or printing `OK`.
- Unknown operation prints usage to standard error and exits `64`.

#### Functions
- `is_running`: checks PID file and process.
- `heartbeat_age`: computes heartbeat age in seconds.
- `start_worker`: launches worker and records PID.
- `stop_worker`: terminates worker and removes PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`
- `now=$(date +%s)`
- `beat=$(stat -c %Y "$HEARTBEAT_FILE" 2>/dev/null || echo 0)`
- `age=$(heartbeat_age)`

#### Dependencies
- External commands: `cat`, `date`, `stat`, `echo`, `kill`, `nohup`, `rm`.
- Worker executable: `/usr/local/bin/queue-worker`, invoked with `--heartbeat` and the heartbeat-file path.

#### Errors and exit codes
- `status`: exits `3` when the worker is not running.
- `health`: exits `2` when the worker is not running; exits `1` when heartbeat age exceeds `MAX_HEARTBEAT_AGE`.
- Unknown operation: exits `64`.
- `heartbeat_age` substitutes timestamp `0` if `stat` fails; no traps, retries, or timeouts.
