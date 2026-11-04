### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:43:41Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> A POSIX init-style wrapper that manages a queue worker, providing start, stop, status, restart, and health check based on PID and heartbeat files.  

#### Purpose  
- Controls a queue worker process.  
- Exposes `start`, `stop`, `restart`, `status`, and `health` commands.  

#### Type  
- `sh` (POSIX).  
- No strict mode flags used.  

#### Input  
- `$1` command (`start`, `stop`, `restart`, `status`, `health`).  
- Environment variables:  
  - `PID_FILE` (default `/run/queue-worker.pid`).  
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).  
- No command‑line options.  

#### Output  
- Writes PID to `$PID_FILE`.  
- Updates `$HEARTBEAT_FILE` via worker.  
- Prints status messages (`running`, `stopped`, `OK`, `WARNING`, `CRITICAL`).  
- Exit codes:  
  - `0` success.  
  - `1` heartbeat too old.  
  - `2` worker not running.  
  - `3` status stopped.  
  - `64` invalid usage.  

#### Core logic  
- `is_running`: checks PID file and process liveness.  
- `heartbeat_age`: computes seconds since last heartbeat file modification.  
- `start_worker`: starts worker with `nohup`, records PID.  
- `stop_worker`: kills process, removes PID file.  
- Command dispatch via `case` on `$1`.  

#### Functions  
- `is_running`: test if worker process exists.  
- `heartbeat_age`: calculate heartbeat age in seconds.  
- `start_worker`: launch worker, write PID.  
- `stop_worker`: terminate worker, delete PID.  

#### Constants and configuration  
- `PID_FILE`=`/run/queue-worker.pid`  
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`  
- `MAX_HEARTBEAT_AGE`=`120`  
- `WORKER_BIN`=`/usr/local/bin/queue-worker`  

#### Dependencies  
- External commands: `nohup`, `kill`, `cat`, `date`, `stat`.  
- Worker binary: `/usr/local/bin/queue-worker` (expects `--heartbeat` option).  

#### Errors and exit codes  
- `exit 2` if worker not running during `health`.  
- `exit 1` if heartbeat age exceeds `MAX_HEARTBEAT_AGE`.  
- `exit 3` if `status` finds no running process.  
- `exit 64` for unknown command.  
- No traps or cleanup beyond PID file removal.
