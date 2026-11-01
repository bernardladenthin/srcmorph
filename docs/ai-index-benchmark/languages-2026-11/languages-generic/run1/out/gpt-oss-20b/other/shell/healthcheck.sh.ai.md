### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:30:20Z
- G: 
- A: 0.0.0
- X: file
---
> Wrapper script for managing a queue worker process and monitoring its health.

#### Purpose
- Start, stop, restart, and report status of a queue worker.
- Provide a health check based on PID file existence and heartbeat file age.

#### Type
- Shell script (`sh`), POSIX init-style wrapper.

#### Input
- `$1`: command (`start`, `stop`, `restart`, `status`, `health`).
- Environment variables: `PID_FILE`, `HEARTBEAT_FILE`, `MAX_HEARTBEAT_AGE`.
- Files: `PID_FILE` (contains worker PID), `HEARTBEAT_FILE` (timestamped by worker).

#### Output
- Console messages: status, health, usage.
- Exit codes: `0` success, `1` warning, `2` critical, `3` stopped, `64` usage error.
- Writes PID to `PID_FILE` on start; removes on stop.

#### Core logic
- `is_running`: checks PID file and verifies process with `kill -0`.
- `heartbeat_age`: computes seconds since last heartbeat file modification.
- `start_worker`: if not running, launches worker with `nohup`, records PID.
- `stop_worker`: sends `kill` to PID, removes PID file.
- Command dispatch: `start`, `stop`, `restart`, `status`, `health`, else usage.

#### Public API
- `start_worker() -> void` – Launch worker if not running.
- `stop_worker() -> void` – Terminate worker if running.
- `is_running() -> bool` – Check if worker process exists.
- `heartbeat_age() -> int` – Seconds since last heartbeat.
- Command-line entry point: `case "$1" in ... esac`.

#### Constants and configuration
- `PID_FILE` default `/run/queue-worker.pid`.
- `HEARTBEAT_FILE` default `/var/lib/queue-worker/heartbeat`.
- `MAX_HEARTBEAT_AGE` default `120`.
- `WORKER_BIN` `/usr/local/bin/queue-worker`.

#### Dependencies
- External commands: `kill`, `nohup`, `date`, `stat`, `cat`, `echo`, `rm`.
- Worker binary: `/usr/local/bin/queue-worker`.

#### Exceptions / Errors
- Exits with code `2` if worker not running during health check.
- Exits with code `1` if heartbeat older than `MAX_HEARTBEAT_AGE`.
- Exits with code `3` if status reports stopped.
- Exits with code `64` for invalid command usage.
