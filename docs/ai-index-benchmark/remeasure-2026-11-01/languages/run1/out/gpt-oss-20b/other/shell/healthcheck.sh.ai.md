### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:18:02Z
- G: 
- A: 0.0.0
- X: file
---
> **POSIX init-style wrapper for a queue worker with health probe.**

#### Purpose
- Controls a queue worker: `start`, `stop`, `restart`, `status`, `health`.
- Intended to be executed directly as a script.

#### Type
- `sh` (POSIX shell).
- No strict mode flags used.

#### Input
- `$1`: command (`start`, `stop`, `restart`, `status`, `health`).
- Environment variables:
  - `PID_FILE` (default `/run/queue-worker.pid`).
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).
- No other options or arguments.

#### Output
- Writes PID to `PID_FILE` when starting.
- Removes `PID_FILE` when stopping.
- Prints status messages to stdout or stderr.
- Exits with codes:
  - `0` on success.
  - `1` if heartbeat too old.
  - `2` if not running during health check.
  - `3` if status reports stopped.
  - `64` for usage error.

#### Core logic
- `is_running`: checks PID file and process existence.
- `heartbeat_age`: computes seconds since last heartbeat file modification.
- `start_worker`: starts worker with `nohup`, records PID.
- `stop_worker`: kills process, removes PID file.
- Command dispatch via `case` on `$1`:
  - `start`: `start_worker`.
  - `stop`: `stop_worker`.
  - `restart`: `stop_worker; start_worker`.
  - `status`: prints `running` or `stopped`; exits `3` if stopped.
  - `health`: verifies running and heartbeat age; exits `1` or `2` on failure, `0` otherwise.
  - default: usage message, exit `64`.

#### Functions
- `is_running`: check if worker process exists.
- `heartbeat_age`: calculate age of heartbeat file.
- `start_worker`: launch worker, write PID.
- `stop_worker`: terminate worker, delete PID file.

#### Constants and configuration
- `PID_FILE`=`/run/queue-worker.pid`
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`
- `MAX_HEARTBEAT_AGE`=`120`
- `WORKER_BIN`=`/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `nohup`, `kill`, `cat`, `date`, `stat`, `echo`.
- Worker binary: `/usr/local/bin/queue-worker` invoked with `--heartbeat "$HEARTBEAT_FILE"`.

#### Errors and exit codes
- `exit 2`: health check fails because worker not running.
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.
- `exit 3`: status reports stopped.
- `exit 64`: invalid command usage.
