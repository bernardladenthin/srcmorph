### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T14:40:24Z
- G: 
- A: 0.0.0
- X: file
---
> POSIX init-style wrapper that starts, stops, checks status, and probes health of a queue worker via PID and heartbeat files.

#### Purpose
- Control a queue worker process.
- Provide health check for monitoring systems.

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
- `heartbeat_age`: calculates seconds since heartbeat file modification.
- `start_worker`: starts worker with `nohup`, writes PID.
- `stop_worker`: kills process and removes PID file.
- `case` dispatches commands:
  - `start`: invoke `start_worker`.
  - `stop`: invoke `stop_worker`.
  - `restart`: stop then start.
  - `status`: echo `running` or `stopped`; exit `3` if stopped.
  - `health`: verify running; check heartbeat age against `MAX_HEARTBEAT_AGE`; output `OK`, `WARNING`, or `CRITICAL`.

#### Functions
- `is_running`: checks if worker process is alive.
- `heartbeat_age`: returns age of heartbeat file in seconds.
- `start_worker`: starts worker, writes PID.
- `stop_worker`: stops worker, removes PID file.

#### Constants and configuration
- `PID_FILE`=`/run/queue-worker.pid`
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`
- `MAX_HEARTBEAT_AGE`=`120`
- `WORKER_BIN`=`/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `nohup`, `kill`, `cat`, `date`, `stat`, `echo`, `rm`.
- Worker binary: `/usr/local/bin/queue-worker` (expects `--heartbeat` option).

#### Errors and exit codes
- `exit 2`: worker not running during `health`.
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.
- `exit 3`: status reports stopped.
- `exit 64`: invalid command usage.
