### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:38:50Z
- G: 
- A: 0.0.0
- X: file
---
> Manages the queue worker lifecycle and checks whether its heartbeat is fresh.

#### Purpose
- Starts, stops, restarts, and reports status or health for the queue worker.
- Run directly as an init-style command wrapper.

#### Type
- POSIX `sh`; no strict-mode settings.

#### Input
- `$1` selects `start`, `stop`, `restart`, `status`, or `health`; parsed with a `case` statement.
- Environment: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and heartbeat file; reads heartbeat modification time with `stat`.

#### Output
- Starts `queue-worker` in the background with output discarded; writes its PID to `PID_FILE`.
- Stops the worker and removes `PID_FILE` after a successful `kill`.
- Prints lifecycle or health messages to stdout; prints usage to stderr for an invalid command.
- Health reports `CRITICAL: not running`, `WARNING: heartbeat Ns old`, or `OK`.

#### Core logic
- `start`: if the PID file identifies a running process, prints `already running`; otherwise launches the worker with the heartbeat path and records its PID.
- `stop`: does nothing if the worker is not running; otherwise kills the recorded PID and removes the PID file on success.
- `restart`: runs stop, then start.
- `status`: prints `running` or `stopped`.
- `health`: reports critical if the worker is not running; otherwise compares heartbeat age with the 120-second limit and reports a warning or `OK`.
- Any other command prints usage and exits with status 64.

#### Functions
- `is_running`: Checks PID file and process.
- `heartbeat_age`: Calculates heartbeat file age in seconds.
- `start_worker`: Launches worker and records PID.
- `stop_worker`: Kills worker and removes PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `cat`, `date`, `stat`, `echo`, `kill`, `nohup`, `rm`.
- Worker executable: `/usr/local/bin/queue-worker`.

#### Errors and exit codes
- `status`: exits 3 when the worker is stopped.
- `health`: exits 2 when the worker is not running; exits 1 when heartbeat age exceeds `MAX_HEARTBEAT_AGE`.
- Invalid command: exits 64.
- Other statuses follow the commands and shell control flow; no traps, retries, or timeouts.
