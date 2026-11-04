### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:19:54Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Manages the queue worker lifecycle and checks whether its process and heartbeat are healthy.

#### Purpose
- Starts, stops, restarts, and reports the status or heartbeat health of the queue worker.
- Run directly as an init-style command wrapper.

#### Type
- POSIX `sh`; no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `$1`: command selected by a `case` loop: `start`, `stop`, `restart`, `status`, or `health`.
- Environment variable `PID_FILE`, default `/run/queue-worker.pid`.
- Environment variable `HEARTBEAT_FILE`, default `/var/lib/queue-worker/heartbeat`.
- Reads the PID file with `cat`; checks the heartbeat file timestamp with `stat`.

#### Output
- Starts `queue-worker` in the background, redirects its output to `/dev/null`, and writes its PID to `PID_FILE`.
- Stops the worker and removes `PID_FILE` if `kill` succeeds.
- Prints `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat …s old`, `OK`, or usage text as applicable.
- Exit statuses: `0` for successful start, stop, restart, running status, or healthy probe; `1` for stale heartbeat; `2` when the health probe finds the worker stopped; `3` when status finds it stopped; `64` for an unrecognized command. Other command failures may propagate.

#### Core logic
- `start`: if the PID file identifies a live process, print `already running`; otherwise launch the worker with the heartbeat path and save its PID.
- `stop`: if no live process is detected, return successfully; otherwise kill the PID and remove the PID file on success.
- `restart`: stop, then start the worker.
- `status`: print `running` when the PID is live; otherwise print `stopped` and exit `3`.
- `health`: exit `2` with a critical message if the PID is not live; otherwise compare heartbeat age against 120 seconds, exit `1` with a warning if stale, or print `OK`.

#### Functions
- `is_running` — checks PID file and process liveness.
- `heartbeat_age` — calculates heartbeat age in seconds.
- `start_worker` — launches the worker and records its PID.
- `stop_worker` — kills the worker and removes its PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `nohup`, `kill`, `cat`, `date`, `stat`, `rm`.
- Worker executable: `/usr/local/bin/queue-worker`, invoked with `--heartbeat` and the configured heartbeat path.
- Files: PID file at `PID_FILE`; heartbeat file at `HEARTBEAT_FILE`.

#### Errors and exit codes
- Health check exits `2` if the worker is not running, or `1` if heartbeat age exceeds `MAX_HEARTBEAT_AGE`.
- Status exits `3` if the worker is not running.
- Invalid command exits `64` and prints usage to standard error.
- No traps, retries, or timeouts.
