### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:32:47Z
- G: 
- A: 0.0.0
- X: file
---
> Starts, stops, reports status for, and health-checks a queue worker using its PID file and heartbeat age.

#### Purpose
- Manage the queue worker through init-style start, stop, restart, and status commands.
- Check whether the worker is running and its heartbeat is fresh.

#### Type
- POSIX shell script (`/bin/sh`); command-line wrapper.

#### Input
- First argument `$1`: command (`start`, `stop`, `restart`, `status`, or `health`).
- `PID_FILE`: environment override; defaults to `/run/queue-worker.pid`.
- `HEARTBEAT_FILE`: environment override; defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID from `PID_FILE` and the modification time of `HEARTBEAT_FILE`.

#### Output
- Starts `/usr/local/bin/queue-worker` with `--heartbeat "$HEARTBEAT_FILE"`; records its PID in `PID_FILE`.
- Prints status or health messages: `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat …s old`, or `OK`.
- Removes `PID_FILE` after a successful stop signal.

#### Core logic
- `is_running` requires an existing PID file and a successful `kill -0` check for its recorded PID.
- `start_worker` reports `already running` if active; otherwise launches the worker with `nohup` in the background and writes `$!` to the PID file.
- `stop_worker` does nothing if not running; otherwise sends `kill` and removes the PID file only if `kill` succeeds.
- `restart` runs stop then start.
- `status` prints `running` or prints `stopped` and exits with status 3.
- `health` exits 2 with `CRITICAL: not running` if inactive; otherwise compares heartbeat age against the limit, exiting 1 with a warning when too old, or printing `OK`.
- A missing or unreadable heartbeat timestamp falls back to `0`, making its calculated age `now - 0`.

#### Public API
- `start` — start the worker unless already running.
- `stop` — stop the worker and remove its PID file on success.
- `restart` — stop, then start the worker.
- `status` — report whether the worker is running.
- `health` — check worker process and heartbeat freshness.
- `main` — dispatch `$1` to the command handlers; invalid commands print usage and exit 64.
- Internal functions: `is_running`, `heartbeat_age`, `start_worker`, `stop_worker`.

#### Constants and configuration
- `PID_FILE` — environment override; default `/run/queue-worker.pid`.
- `HEARTBEAT_FILE` — environment override; default `/var/lib/queue-worker/heartbeat`.
- `MAX_HEARTBEAT_AGE` — `120` seconds.
- `WORKER_BIN` — `/usr/local/bin/queue-worker`.
- Exit statuses: `1` — stale heartbeat; `2` — worker not running; `3` — status reports stopped; `64` — invalid command.

#### Dependencies
- POSIX utilities: `cat`, `date`, `stat`, `echo`, `kill`, `rm`, `nohup`.
- Worker executable: `/usr/local/bin/queue-worker`.

#### Exceptions / Errors
- Invalid command prints `usage: $0 {start|stop|restart|status|health}` to stderr and exits 64.
- `status` exits 3 when stopped; `health` exits 2 when stopped or 1 when heartbeat age exceeds 120 seconds.
- `kill` errors during the running check are suppressed; a failed stop signal prevents PID-file removal.
