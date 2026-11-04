### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:29Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> A POSIX init-style wrapper that manages a queue worker process, providing start, stop, status, restart, and health check based on PID and heartbeat files.  

#### Purpose  
- Controls `queue-worker` lifecycle.  
- Exposes health probe for monitoring.  
- Intended to be run directly as a script.  

#### Type  
- `sh` (POSIX).  
- No strict mode flags used.  

#### Input  
- `$1` command: `start`, `stop`, `restart`, `status`, `health`.  
- Environment variables:  
  - `PID_FILE` (default `/run/queue-worker.pid`).  
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).  
- No other options or arguments.  

#### Output  
- Writes PID to `$PID_FILE` when starting.  
- Removes `$PID_FILE` when stopping.  
- Prints status messages (`running`, `stopped`, `OK`, `WARNING`, `CRITICAL`).  
- Exits with codes:  
  - `0` success.  
  - `1` heartbeat too old.  
  - `2` not running.  
  - `3` stopped status.  
  - `64` invalid usage.  

#### Core logic  
- `is_running`: checks PID file and process liveness.  
- `heartbeat_age`: calculates seconds since last heartbeat file modification.  
- `start_worker`: starts `queue-worker` with `--heartbeat`, records PID.  
- `stop_worker`: kills process and removes PID file.  
- Command dispatch via `case` on `$1`.  

#### Functions  
- `is_running`: test if worker is active.  
- `heartbeat_age`: compute age of heartbeat file.  
- `start_worker`: launch worker, write PID.  
- `stop_worker`: terminate worker, clean PID.  

#### Constants and configuration  
- `PID_FILE`=`/run/queue-worker.pid`  
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`  
- `MAX_HEARTBEAT_AGE`=`120`  
- `WORKER_BIN`=`/usr/local/bin/queue-worker`  

#### Dependencies  
- External commands: `nohup`, `kill`, `cat`, `date`, `stat`.  
- `queue-worker` binary at `/usr/local/bin/queue-worker`.  

#### Errors and exit codes  
- `exit 2` if health check finds no running process.  
- `exit 1` if heartbeat age exceeds `MAX_HEARTBEAT_AGE`.  
- `exit 3` if status reports stopped.  
- `exit 64` for invalid command usage.  
- No traps or cleanup beyond PID file removal.
