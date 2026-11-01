### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:40:34Z
- G: 
- A: 0.0.0
- X: file
---
> Wrapper script to control a queue worker and monitor its health via PID and heartbeat files.

#### Purpose
- Manage lifecycle of a queue worker (start, stop, restart, status).
- Provide health check based on heartbeat file age.

#### Type
- Shell script (`sh`), POSIX init-style wrapper.

#### Input
- `$1`: command (`start`, `stop`, `restart`, `status`, `health`).
- Environment variables: `PID_FILE`, `HEARTBEAT_FILE`.
- Files: `PID_FILE` (contains worker PID), `HEARTBEAT_FILE` (timestamped by worker).

#### Output
- Console messages: status, health, usage.
- Exit codes: `0` success, `1` warning, `2` critical, `3` stopped, `64` usage error.
- Creates/removes `PID_FILE`; writes worker PID; updates `HEARTBEAT_FILE` via worker.

#### Core logic
- `is_running`: checks if PID file exists and process is alive.
- `heartbeat_age`: computes seconds since last heartbeat file modification.
- `start_worker`: starts worker with `nohup`, records PID.
- `stop_worker`: kills process and removes PID file.
- Command dispatch: `start`, `stop`, `restart`, `status`, `health`, else usage.

#### Public API
- `start_worker()`: start worker if not running.
- `stop_worker()`: stop worker if running.
- `is_running()`: test if worker process exists.
- `heartbeat_age()`: return age of heartbeat file.
- Command handling via `case "$1"`.

#### Constants and configuration
- `PID_FILE` default `/run/queue-worker.pid`.
- `HEARTBEAT_FILE` default `/var/lib/queue-worker/heartbeat`.
- `MAX_HEARTBEAT_AGE` `120` seconds.
- `WORKER_BIN` `/usr/local/bin/queue-worker`.

#### Dependencies
- External commands: `kill`, `nohup`, `date`, `stat`, `cat`, `echo`, `rm`.
- Worker binary: `/usr/local/bin/queue-worker`.

#### Exceptions / Errors
- `is_running` suppresses errors with `2>/dev/null`.
- `heartbeat_age` falls back to `0` if heartbeat file missing.
- Exit codes signal health status: `0` OK, `1` WARNING, `2` CRITICAL, `3` stopped, `64` usage error.
