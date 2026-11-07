### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:45:42Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> A POSIX‑style wrapper that manages a queue worker, providing start, stop, status, and health checks based on a PID file and heartbeat file age.

#### Purpose
- Controls a queue worker binary.
- Runs directly as a command.

#### Type
- `sh` (POSIX).
- No strict mode flags.

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
- `start_worker`: starts worker with `nohup`, records PID.
- `stop_worker`: kills process and removes PID file.
- Command dispatch via `case` on `$1`:
  - `start`: invoke `start_worker`.
  - `stop`: invoke `stop_worker`.
  - `restart`: stop then start.
  - `status`: report running/stopped, exit 3 if stopped.
  - `health`: verify running and heartbeat age ≤ `MAX_HEARTBEAT_AGE`; warn if older.

#### Functions
- `is_running`: check if worker process exists.
- `heartbeat_age`: calculate heartbeat staleness.
- `start_worker`: launch worker and record PID.
- `stop_worker`: terminate worker and clean PID file.

#### Constants and configuration
- `PID_FILE`=`/run/queue-worker.pid`
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`
- `MAX_HEARTBEAT_AGE`=`120`
- `WORKER_BIN`=`/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `kill`, `nohup`, `date`, `stat`, `cat`.
- Worker binary: `/usr/local/bin/queue-worker` (expects `--heartbeat` option).

#### Errors and exit codes
- `exit 64`: invalid command usage.
- `exit 2`: health check fails because worker not running.
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.
- `exit 3`: status check reports stopped.
