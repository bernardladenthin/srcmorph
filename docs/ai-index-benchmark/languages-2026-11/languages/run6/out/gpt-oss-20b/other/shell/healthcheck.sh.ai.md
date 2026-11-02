### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T06:39:13Z
- G: 
- A: 0.0.0
- X: file
---
> POSIX init-style wrapper for a queue worker: start, stop, status and health probe.

#### Purpose
- Controls a queue worker process.
- Provides health check via heartbeat file age.

#### Type
- `sh` (POSIX sh).
- No strict mode flags.

#### Input
- `$1`: command (`start`, `stop`, `restart`, `status`, `health`).
- Environment variables:
  - `PID_FILE` (default `/run/queue-worker.pid`).
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).
- No options parsed.

#### Output
- Writes PID to `PID_FILE` on start.
- Removes `PID_FILE` on stop.
- Prints status messages (`running`, `stopped`, `OK`, `WARNING`, `CRITICAL`).
- Exits with codes: `0` success, `1` warning, `2` critical, `3` stopped, `64` usage error.

#### Core logic
- `is_running`: checks PID file and process existence.
- `heartbeat_age`: computes seconds since heartbeat file modification.
- `start_worker`: starts worker with `nohup`, writes PID.
- `stop_worker`: kills process, removes PID file.
- Command dispatch via `case` on `$1`:
  - `start`: `start_worker`.
  - `stop`: `stop_worker`.
  - `restart`: `stop_worker`; `start_worker`.
  - `status`: prints `running` or `stopped`; exit 3 if stopped.
  - `health`: verifies running and heartbeat age; warns if older than `MAX_HEARTBEAT_AGE` (120s).

#### Functions
- `is_running`: check if worker is active.
- `heartbeat_age`: calculate heartbeat age.
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
- `exit 64`: usage error.
- `exit 2`: health check fails (not running).
- `exit 1`: heartbeat too old.
- `exit 3`: status reports stopped.
