### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-10-31T21:26:11Z
- T: 2026-11-01T23:56:18Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> A POSIX init‑style wrapper that manages a queue worker, providing start, stop, status, restart, and health‑check based on PID and heartbeat files.  

#### Purpose
- Controls a queue worker process.  
- Exposes `start`, `stop`, `restart`, `status`, and `health` commands.  

#### Type
- `sh` (POSIX).  
- No strict mode flags used.  

#### Input
- `$1` command (`start|stop|restart|status|health`).  
- Environment variables:  
  - `PID_FILE` (default `/run/queue-worker.pid`).  
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).  
- No options parsed.  

#### Output
- Writes PID to `$PID_FILE`.  
- Removes `$PID_FILE` on stop.  
- Prints status messages to stdout or stderr.  
- Exits with codes:  
  - `0` success,  
  - `1` heartbeat too old,  
  - `2` not running,  
  - `3` stopped status,  
  - `64` usage error.  

#### Core logic
- `is_running`: checks PID file and process liveness.  
- `heartbeat_age`: computes seconds since heartbeat file modification.  
- `start_worker`: starts worker with `nohup`, records PID.  
- `stop_worker`: kills process and removes PID file.  
- Command dispatch via `case` on `$1`.  

#### Functions
- `is_running`: test if worker is active.  
- `heartbeat_age`: calculate heartbeat staleness.  
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
- `exit 2`: health check fails because worker not running.  
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.  
- `exit 3`: status reports stopped.  
- `exit 64`: invalid command usage.  
- No traps or retries implemented.
