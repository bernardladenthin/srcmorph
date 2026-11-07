### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:13:02Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Manages the queue worker lifecycle and checks whether its process and heartbeat indicate healthy operation.

#### Purpose
- Starts, stops, restarts, reports status for, and health-checks the queue worker.
- Run directly as an init-style command wrapper.

#### Type
- POSIX `sh`; no strict-mode settings (`set -e`, `set -u`, or `pipefail`).

#### Input
- `$1`: action name: `start`, `stop`, `restart`, `status`, or `health`.
- Environment: `PID_FILE` defaults to `/run/queue-worker.pid`; `HEARTBEAT_FILE` defaults to `/var/lib/queue-worker/heartbeat`.
- Reads the PID file and, for health checks, the heartbeat file’s modification time.

#### Output
- Starts `queue-worker` with output discarded; writes its PID to `PID_FILE`.
- Removes `PID_FILE` after a successful stop signal; does not remove it on other paths.
- Prints `already running`, `running`, `stopped`, `CRITICAL: not running`, `WARNING: heartbeat ...s old`, `OK`, or usage text as applicable.
- `status` exits 3 when stopped; `health` exits 2 when not running and 1 when the heartbeat exceeds the age limit. Invalid actions exit 64.
- Other actions return the status of their final command.

#### Core logic
- `start`: if the PID file identifies a live process, print `already running`; otherwise launch the worker with `nohup` and write its background PID to `PID_FILE`.
- `stop`: if the worker is not running, do nothing; otherwise send it `TERM` and remove `PID_FILE` if the signal succeeds.
- `restart`: run stop, then start.
- `status`: print `running` if the PID identifies a live process; otherwise print `stopped` and exit 3.
- `health`: require a live process, calculate current time minus heartbeat modification time (using 0 if `stat` fails), warn and exit 1 if age exceeds 120 seconds; otherwise print `OK`.
- Unrecognized action: print usage to standard error and exit 64.

#### Functions
- `is_running`: check PID file and process liveness.
- `heartbeat_age`: calculate heartbeat age in seconds.
- `start_worker`: launch worker and record PID.
- `stop_worker`: signal worker and remove PID file.

#### Constants and configuration
- `PID_FILE="${PID_FILE:-/run/queue-worker.pid}"`
- `HEARTBEAT_FILE="${HEARTBEAT_FILE:-/var/lib/queue-worker/heartbeat}"`
- `MAX_HEARTBEAT_AGE=120`
- `WORKER_BIN=/usr/local/bin/queue-worker`

#### Dependencies
- External commands: `cat`, `date`, `kill`, `nohup`, `rm`, `stat`.
- Worker executable: `/usr/local/bin/queue-worker`; launched with `--heartbeat` and the configured heartbeat path.

#### Errors and exit codes
- Exit 0 for successful status, healthy probe, and already-running start; other command paths use their final command’s status.
- Exit 1 when heartbeat age is greater than `MAX_HEARTBEAT_AGE`.
- Exit 2 when health check finds no running worker.
- Exit 3 when status check finds no running worker.
- Exit 64 for an unsupported action.
- No traps, retries, or timeouts.
