### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:34:38Z
- G: 
- A: 0.0.0
- X: file
---
> POSIX wrapper to start, stop, check status, and health‑probe a queue worker process.

#### Purpose
- Controls a queue worker daemon.
- Provides health checks via heartbeat file age.

#### Type
- Shell script (`sh`).

#### Input
- `$1`: command (`start`, `stop`, `restart`, `status`, `health`).
- `$PID_FILE`: path to PID file (default `/run/queue-worker.pid`).
- `$HEARTBEAT_FILE`: path to heartbeat file (default `/var/lib/queue-worker/heartbeat`).
- `$WORKER_BIN`: path to worker binary (`/usr/local/bin/queue-worker`).

#### Output
- Echoes status messages (`running`, `stopped`, `OK`, `WARNING`, `CRITICAL`).
- Exits with codes: `0` success, `1` warning, `2` critical, `3` stopped, `64` usage error.

#### Core logic
- `is_running`: checks if PID file exists and process is alive.
- `heartbeat_age`: computes seconds since heartbeat file modification.
- `start_worker`: starts worker in background, writes PID.
- `stop_worker`: kills worker, removes PID file.
- Command dispatch: `start`, `stop`, `restart`, `status`, `health`, or usage error.

#### Public API
- `start_worker()`: starts worker if not running.
- `stop_worker()`: stops worker if running.
- `is_running()`: returns true if worker alive.
- `heartbeat_age()`: returns age of heartbeat file.
- `case "$1"`: command dispatcher.

#### Constants and configuration
- `PID_FILE`: default `/run/queue-worker.pid`.
- `HEARTBEAT_FILE`: default `/var/lib/queue-worker/heartbeat`.
- `MAX_HEARTBEAT_AGE`: `120` seconds.
- `WORKER_BIN`: `/usr/local/bin/queue-worker`.

#### Dependencies
- `kill`, `nohup`, `date`, `stat`, `cat`, `echo`, `rm`, `sleep` (via `nohup`).

#### Exceptions / Errors
- Exits with `2` if worker not running during health check.
- Exits with `1` if heartbeat older than `MAX_HEARTBEAT_AGE`.
- Exits with `3` if status reports stopped.
- Exits with `64` on unknown command.
