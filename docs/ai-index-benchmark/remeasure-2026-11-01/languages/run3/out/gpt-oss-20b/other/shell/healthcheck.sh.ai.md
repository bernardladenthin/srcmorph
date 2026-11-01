### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:57:37Z
- G: 
- A: 0.0.0
- X: file
---
> Wrapper for a queue worker: start, stop, status, and health probe via PID and heartbeat files.

#### Purpose
- Controls a queue worker process.
- Runs directly as a command-line utility.

#### Type
- `sh` (POSIX sh).
- No strict mode flags used.

#### Input
- `$1` command: `start`, `stop`, `restart`, `status`, `health`.
- Environment variables:
  - `PID_FILE` (default `/run/queue-worker.pid`).
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).
- No other options or arguments.

#### Output
- Writes PID to `PID_FILE` when starting.
- Removes `PID_FILE` when stopping.
- Prints status messages (`running`, `stopped`, `OK`, `WARNING`, `CRITICAL`).
- Exits with codes: `0` success, `1` warning, `2` critical, `3` stopped, `64` usage error.

#### Core logic
- `is_running`: checks if PID file exists and process is alive.
- `heartbeat_age`: computes seconds since last heartbeat file modification.
- `start_worker`: starts worker with `nohup`, writes PID.
- `stop_worker`: kills process and removes PID file.
- Command dispatch via `case` on `$1`:
  - `start`: invoke `start_worker`.
  - `stop`: invoke `stop_worker`.
  - `restart`: stop then start.
  - `status`: report running/stopped.
  - `health`: verify running and heartbeat age within `MAX_HEARTBEAT_AGE` (120s).

#### Functions
- `is_running`: check if worker process is alive.
- `heartbeat_age`: calculate heartbeat file age.
- `start_worker`: launch worker, record PID.
- `stop_worker`: terminate worker, clean PID file.

#### Constants and configuration
- `PID_FILE`=`/run/queue-worker.pid`
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`
- `MAX_HEARTBEAT_AGE`=`120`
- `WORKER_BIN`=`/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `kill`, `nohup`, `date`, `stat`, `cat`.
- Worker binary: `/usr/local/bin/queue-worker` (expects `--heartbeat` option).

#### Errors and exit codes
- `exit 64`: usage error (unknown command).
- `exit 2`: health check fails because worker not running.
- `exit 1`: health check fails due to stale heartbeat.
- `exit 3`: status check reports stopped.
- Normal exit `0` on success or when already running/stopped.
