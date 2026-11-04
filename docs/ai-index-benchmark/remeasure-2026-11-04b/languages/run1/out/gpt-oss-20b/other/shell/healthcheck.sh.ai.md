### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:14:55Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> A POSIX‑style wrapper to control a queue worker and report its health via PID and heartbeat files.

#### Purpose
- Manages a queue worker process: `start`, `stop`, `restart`, `status`, `health`.
- Intended to be run directly as a command.

#### Type
- `sh` (POSIX sh) with no strict mode flags.

#### Input
- `$1` command (`start`, `stop`, `restart`, `status`, `health`).
- Environment variables:
  - `PID_FILE` (default `/run/queue-worker.pid`).
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).
- No options parsed; uses a `case` on `$1`.

#### Output
- Writes PID to `PID_FILE` when starting.
- Removes `PID_FILE` when stopping.
- Prints status messages to stdout or stderr.
- Exits with codes: `0` (OK), `1` (WARNING), `2` (CRITICAL), `3` (stopped), `64` (usage error).

#### Core logic
- `is_running`: checks existence of `PID_FILE` and liveness of PID.
- `heartbeat_age`: computes seconds since last modification of `HEARTBEAT_FILE`.
- `start_worker`: starts `WORKER_BIN` with `--heartbeat`, records PID.
- `stop_worker`: kills PID and removes `PID_FILE`.
- `case` dispatch:
  - `start`: invoke `start_worker`.
  - `stop`: invoke `stop_worker`.
  - `restart`: stop then start.
  - `status`: report `running` or `stopped`.
  - `health`: verify running and heartbeat age against `MAX_HEARTBEAT_AGE` (120 s).

#### Functions
- `is_running`: check if worker process is alive.
- `heartbeat_age`: calculate heartbeat staleness.
- `start_worker`: launch worker and record PID.
- `stop_worker`: terminate worker and clean PID file.

#### Constants and configuration
- `PID_FILE`=`/run/queue-worker.pid`
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`
- `MAX_HEARTBEAT_AGE`=`120`
- `WORKER_BIN`=`/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `nohup`, `kill`, `cat`, `date`, `stat`, `echo`.
- `WORKER_BIN` executable (`/usr/local/bin/queue-worker`) which accepts `--heartbeat`.

#### Errors and exit codes
- `exit 64`: usage error for unknown command.
- `exit 2`: worker not running during `health`.
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.
- `exit 3`: `status` reports stopped.
