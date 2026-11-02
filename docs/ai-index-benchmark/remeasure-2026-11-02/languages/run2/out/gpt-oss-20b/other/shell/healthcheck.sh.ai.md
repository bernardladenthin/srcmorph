### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:41:49Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> A POSIX init-style wrapper that manages a queue worker process and reports its health via a heartbeat file.

#### Purpose
- Controls a queue worker: `start`, `stop`, `restart`, `status`, `health`.
- Intended to be run directly as a script.

#### Type
- `sh` (POSIX sh).
- No strict-mode flags.

#### Input
- `$1` command: `start`, `stop`, `restart`, `status`, `health`.
- Environment variables:
  - `PID_FILE` (default `/run/queue-worker.pid`).
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).
- No other options or arguments.

#### Output
- Writes PID to `$PID_FILE` on start.
- Removes `$PID_FILE` on stop.
- Prints status messages (`running`, `stopped`, `OK`, `WARNING`, `CRITICAL`).
- Exits with codes: `0` success, `1` warning, `2` critical, `3` stopped, `64` usage error.

#### Core logic
- `is_running`: checks if PID file exists and process is alive.
- `heartbeat_age`: computes seconds since last heartbeat file modification.
- `start_worker`: starts worker with `nohup`, records PID.
- `stop_worker`: kills process and removes PID file.
- Command dispatch via `case` on `$1`:
  - `start`: `start_worker`.
  - `stop`: `stop_worker`.
  - `restart`: `stop_worker`; `start_worker`.
  - `status`: prints `running` or `stopped`; exits `3` if stopped.
  - `health`: verifies running; checks heartbeat age against `MAX_HEARTBEAT_AGE`; prints `OK`, `WARNING`, or `CRITICAL`; exits accordingly.
  - default: usage message; exit `64`.

#### Functions
- `is_running`: checks if worker is active.
- `heartbeat_age`: calculates heartbeat staleness.
- `start_worker`: launches worker, writes PID.
- `stop_worker`: terminates worker, cleans PID file.

#### Constants and configuration
- `PID_FILE`=`/run/queue-worker.pid`
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`
- `MAX_HEARTBEAT_AGE`=`120`
- `WORKER_BIN`=`/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `nohup`, `kill`, `cat`, `date`, `stat`, `echo`.
- Worker binary: `/usr/local/bin/queue-worker` (expects `--heartbeat` option).

#### Errors and exit codes
- `exit 2`: worker not running during `health`.
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.
- `exit 3`: `status` reports stopped.
- `exit 64`: invalid command usage.
