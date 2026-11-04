### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:31Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> A POSIX‑style wrapper that manages a queue worker process, providing start, stop, status, restart, and health‑check commands based on a PID file and a heartbeat file.

#### Purpose
- Controls a queue worker binary (`/usr/local/bin/queue-worker`).
- Intended to be executed directly as a service script.

#### Type
- `sh` (POSIX sh).
- No strict mode flags set.

#### Input
- `$1` command: `start`, `stop`, `restart`, `status`, `health`.
- Environment variables:
  - `PID_FILE` (default `/run/queue-worker.pid`).
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).
- No command‑line options parsed.

#### Output
- Writes the worker PID to `$PID_FILE` on start.
- Removes `$PID_FILE` on stop.
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
  - `health`: verify running and heartbeat age ≤ `MAX_HEARTBEAT_AGE` (120 s); warn if older.

#### Functions
- `is_running`: check if worker process is active.
- `heartbeat_age`: calculate age of heartbeat file.
- `start_worker`: launch worker, record PID.
- `stop_worker`: terminate worker, clean PID file.

#### Constants and configuration
- `PID_FILE`=`/run/queue-worker.pid`
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`
- `MAX_HEARTBEAT_AGE`=`120`
- `WORKER_BIN`=`/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `kill`, `nohup`, `date`, `stat`, `cat`, `echo`.
- Worker binary: `/usr/local/bin/queue-worker` (expects `--heartbeat` option).

#### Errors and exit codes
- Exit `64` on invalid command.
- Exit `2` if health check finds worker not running.
- Exit `1` if heartbeat age exceeds `MAX_HEARTBEAT_AGE`.
- Exit `3` if status reports stopped.
