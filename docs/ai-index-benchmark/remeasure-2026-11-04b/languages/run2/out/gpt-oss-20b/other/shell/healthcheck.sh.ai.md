### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:18:08Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> A POSIX‑style init wrapper that starts, stops, checks status, and probes the health of a queue worker by inspecting its PID and heartbeat files.  

#### Purpose
- Manage a queue worker process.  
- Provide health checks for monitoring systems.  

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
- Exit codes:  
  - `0` success.  
  - `1` heartbeat too old.  
  - `2` not running.  
  - `3` stopped status.  
  - `64` usage error.  

#### Core logic
- `is_running`: checks if PID file exists and process is alive.  
- `heartbeat_age`: computes seconds since last heartbeat file modification.  
- `start_worker`: starts worker with `nohup`, records PID.  
- `stop_worker`: kills process and removes PID file.  
- Command dispatch: `start`, `stop`, `restart`, `status`, `health`.  

#### Functions
- `is_running`: test if worker is active.  
- `heartbeat_age`: calculate heartbeat age.  
- `start_worker`: launch worker, write PID.  
- `stop_worker`: terminate worker, clean PID.  

#### Constants and configuration
- `PID_FILE`=`/run/queue-worker.pid`  
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`  
- `MAX_HEARTBEAT_AGE`=`120`  
- `WORKER_BIN`=`/usr/local/bin/queue-worker`  

#### Dependencies
- External commands: `kill`, `nohup`, `date`, `stat`.  
- Worker binary: `/usr/local/bin/queue-worker` with `--heartbeat` option.  

#### Errors and exit codes
- `exit 2` if not running during `health`.  
- `exit 1` if heartbeat age exceeds `MAX_HEARTBEAT_AGE`.  
- `exit 3` if status reports stopped.  
- `exit 64` for invalid command usage.  
- No traps or cleanup beyond PID file removal.
