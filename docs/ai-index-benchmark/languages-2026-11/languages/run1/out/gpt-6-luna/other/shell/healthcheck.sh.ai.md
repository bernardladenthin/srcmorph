### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T01:10:55Z
- G: 
- A: 0.0.0
- X: file
---
> Manages a queue worker’s lifecycle and checks whether its heartbeat is recent.

#### Purpose
- Starts, stops, restarts, reports status for, and health-checks the queue worker.
- Run directly as an init-style command wrapper.

#### Type
- POSIX `sh` via `#!/bin/sh`; no strict-mode settings.

#### Input
- `$1`: command, matched by a `case` loop: `start`, `stop`, `restart`, `status`, or `health`.
- `PID_FILE`: environment override; defaults to `/run/queue-worker.pid`.
- `HEARTBEAT_FILE`: environment override; defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and, for health checks, the heartbeat file’s modification time.

#### Output
- Starts `queue-worker` in the background with output redirected to `/dev/null`; writes its PID to `PID_FILE`.
- Stops the worker and removes `PID_FILE` if `kill` succeeds.
- Prints `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat Ns old`, `OK`, or usage text.
- Exit statuses: `0` for successful commands and healthy checks; `1` for an old heartbeat; `2` when health check finds no running worker; `3` when status finds the worker stopped; `64` for an unrecognized command. Other command failures may affect the resulting status.

#### Core logic
- `start`: if the PID file identifies a running process, print `already running`; otherwise launch the worker with `nohup` and save its PID.
- `stop`: do nothing if the worker is not running; otherwise send `kill` and remove the PID file on success.
- `restart`: stop, then start the worker.
- `status`: print `running` if the worker is running; otherwise print `stopped` and exit `3`.
- `health`: exit `2` with a critical message if the worker is not running; otherwise calculate heartbeat age and exit `1` with a warning if it exceeds 120 seconds, or print `OK`.

#### Functions
- `is_running`: Check PID file and process.
- `heartbeat_age`: Calculate heartbeat-file age.
- `start_worker`: Launch worker and save PID.
- `stop_worker`: Stop worker and remove PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`
- `now=$(date +%s)` — assigned in `heartbeat_age`.
- `beat=$(stat -c %Y "$HEARTBEAT_FILE" 2>/dev/null || echo 0)` — assigned in `heartbeat_age`.
- `age=$(heartbeat_age)` — assigned by the `health` command.

#### Dependencies
- External commands: `cat`, `date`, `echo`, `kill`, `nohup`, `rm`, `stat`.
- Worker executable: `/usr/local/bin/queue-worker`; invoked with `--heartbeat` and the heartbeat-file path.

#### Errors and exit codes
- `0`: successful command; health check passes.
- `1`: heartbeat age exceeds `MAX_HEARTBEAT_AGE`.
- `2`: health check finds the worker is not running.
- `3`: status check finds the worker stopped.
- `64`: unknown command; prints usage to standard error.
- `heartbeat_age` uses `0` if `stat` fails; no traps, retries, or timeouts.
