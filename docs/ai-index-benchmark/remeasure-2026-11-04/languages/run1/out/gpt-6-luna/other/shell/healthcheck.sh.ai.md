### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:58:03Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Manages the queue worker lifecycle and checks whether its heartbeat is fresh.

#### Purpose
- Starts, stops, restarts, and reports status or heartbeat health for the queue worker.
- Directly invoked with a command argument; not a sourced library.

#### Type
- POSIX `sh`; no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `$1`: lifecycle or probe command: `start`, `stop`, `restart`, `status`, or `health`.
- Environment variables: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and heartbeat file metadata; starts `/usr/local/bin/queue-worker`.

#### Output
- Starts the worker with output redirected to `/dev/null`; writes its PID to `PID_FILE`.
- Removes `PID_FILE` after a successful stop signal.
- Prints `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat ...s old`, `OK`, or usage text.
- Exit statuses: `0` for successful commands and healthy status; `1` for stale heartbeat; `2` when health check finds the worker stopped; `3` when status finds it stopped; `64` for an invalid command.

#### Core logic
- `start`: if the PID identifies a running process, print `already running`; otherwise launch the worker through `nohup` with `--heartbeat HEARTBEAT_FILE` and save its background PID.
- `stop`: if running, signal the PID and remove the PID file on successful signal; otherwise do nothing.
- `restart`: stop, then start.
- `status`: print `running` if the PID identifies a running process; otherwise print `stopped` and exit `3`.
- `health`: exit `2` with a critical message if stopped; otherwise calculate heartbeat age and warn/exit `1` if over 120 seconds, or print `OK`.
- Any other command prints usage to standard error and exits `64`.

#### Functions
- `is_running`: checks PID file and process existence.
- `heartbeat_age`: calculates heartbeat file age in seconds.
- `start_worker`: launches worker unless already running.
- `stop_worker`: signals worker and removes PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `cat`, `date`, `stat`, `echo`, `kill`, `nohup`, `rm`.
- Worker executable: `/usr/local/bin/queue-worker`, invoked with `--heartbeat` and the heartbeat file path.
- Files: `/run/queue-worker.pid`; `/var/lib/queue-worker/heartbeat`.

#### Errors and exit codes
- `0`: successful command; health check reports `OK` when heartbeat age is at most 120 seconds.
- `1`: health check reports heartbeat older than 120 seconds.
- `2`: health check finds no running worker.
- `3`: `status` finds no running worker.
- `64`: unrecognized command.
- No traps, retries, or timeouts.
