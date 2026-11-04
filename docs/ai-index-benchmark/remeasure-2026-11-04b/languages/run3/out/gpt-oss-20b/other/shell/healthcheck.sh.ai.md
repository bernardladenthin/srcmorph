### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:28Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> A POSIX init-style wrapper that manages a queue worker process, providing start, stop, status, restart, and health check based on PID and heartbeat files.  

#### Purpose  
- Controls a queue worker binary.  
- Exposed as a command-line tool (`start|stop|restart|status|health`).  

#### Type  
- `sh` (POSIX sh).  
- No strict mode flags used.  

#### Input  
- `$1` command (`start`, `stop`, `restart`, `status`, `health`).  
- Environment variables:  
  - `PID_FILE` (default `/run/queue-worker.pid`).  
  - `HEARTBEAT_FILE` (default `/var/lib/queue-worker/heartbeat`).  
- No other options or arguments.  

#### Output  
- Writes PID to `$PID_FILE` when starting.  
- Removes `$PID_FILE` when stopping.  
- Prints status messages (`running`, `stopped`, `OK`, `WARNING`, `CRITICAL`).  
- Exit codes:  
  - `0` success.  
  - `1` heartbeat too old.  
  - `2` not running.  
  - `3` stopped status.  
  - `64` invalid usage.  

#### Core logic  
- `is_running`: checks PID file and process liveness.  
- `heartbeat_age`: calculates seconds since heartbeat file modification.  
- `start_worker`: starts worker with `nohup`, records PID.  
- `stop_worker`: kills process, removes PID file.  
- Command dispatch via `case` on `$1`.  

#### Functions  
- `is_running`: test if worker process exists.  
- `heartbeat_age`: compute heartbeat staleness.  
- `start_worker`: launch worker, write PID.  
- `stop_worker`: terminate worker, clean PID.  

#### Constants and configuration  
- `PID_FILE`=`/run/queue-worker.pid`  
- `HEARTBEAT_FILE`=`/var/lib/queue-worker/heartbeat`  
- `MAX_HEARTBEAT_AGE`=`120`  
- `WORKER_BIN`=`/usr/local/bin/queue-worker`  

#### Dependencies  
- External commands: `kill`, `nohup`, `date`, `stat`, `cat`.  
- Worker binary: `/usr/local/bin/queue-worker` with `--heartbeat` option.  

#### Errors and exit codes  
- `exit 64`: usage error.  
- `exit 2`: health check fails because worker not running.  
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.  
- `exit 3`: status reports stopped.  
- `nohup` and `kill` errors suppressed to `/dev/null`.
