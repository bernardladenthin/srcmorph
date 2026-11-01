### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:34:39Z
- G: 
- A: 0.0.0
- X: file
---
> Wrapper for a queue worker: start, stop, status and health probe via PID and heartbeat files.

#### Purpose
- Controls a queue worker process.
- Runs directly as a command-line tool.

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
- Prints status messages to stdout or stderr.
- Writes the worker PID to `PID_FILE`.
- Exits with codes:
  - `0` on success.
  - `1` if heartbeat too old.
  - `2` if not running during health check.
  - `3` if status reports stopped.
  - `64` for invalid usage.

#### Core logic
- `is_running`: checks if PID file exists and process is alive.
- `heartbeat_age`: computes seconds since last heartbeat file modification.
- `start_worker`: starts worker with `nohup`, records PID.
- `stop_worker`: kills process and removes PID file.
- Command dispatch via `case` on `$1`:
  - `start`: invoke `start_worker`.
  - `stop`: invoke `stop_worker`.
  - `restart`: stop then start.
  - `status`: report `running` or `stopped`.
  - `health`: verify running and heartbeat age; report `OK`, `WARNING`, or `CRITICAL`.

#### Functions
- `is_running`: checks process existence.
- `heartbeat_age`: calculates heartbeat age.
- `start_worker`: launches worker.
- `stop_worker`: terminates worker.

#### Constants and configuration
- `PID_FILE`=`/run/queue-worker.pid`
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`
- `MAX_HEARTBEAT_AGE`=`120`
- `WORKER_BIN`=`/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `nohup`, `kill`, `cat`, `date`, `stat`, `echo`.
- Worker binary: `/usr/local/bin/queue-worker` (expects `--heartbeat` option).

#### Errors and exit codes
- `exit 64`: invalid command usage.
- `exit 2`: health check fails because worker not running.
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.
- `exit 3`: status reports stopped.
