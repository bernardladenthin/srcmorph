### healthcheck.sh
- H: 1.0
- C: FCA7DDB5
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:02:47Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> A POSIX init‑style wrapper that manages a queue worker, providing start, stop, status, and health checks based on a PID file and heartbeat file age.  

#### Purpose
- Controls a queue worker process.  
- Exposes `start`, `stop`, `restart`, `status`, and `health` commands.  

#### Type
- POSIX sh (`#!/bin/sh`).  
- No strict mode flags.  

#### Input
- `$1`: command (`start`, `stop`, `restart`, `status`, `health`).  
- `PID_FILE`: environment variable or default `/run/queue-worker.pid`.  
- `HEARTBEAT_FILE`: environment variable or default `/var/lib/queue-worker/heartbeat`.  
- `MAX_HEARTBEAT_AGE`: default `120` seconds.  
- `WORKER_BIN`: `/usr/local/bin/queue-worker`.  

#### Output
- Writes PID to `$PID_FILE` when starting.  
- Removes `$PID_FILE` when stopping.  
- Prints status messages (`running`, `stopped`, `OK`, `WARNING`, `CRITICAL`).  
- Exit codes: `0` success, `1` warning, `2` critical, `3` stopped, `64` usage error.  

#### Core logic
- `is_running`: checks if PID file exists and process is alive.  
- `heartbeat_age`: calculates seconds since last heartbeat file modification.  
- `start_worker`: starts worker in background with `nohup`, writes PID.  
- `stop_worker`: kills process and removes PID file.  
- Command dispatch via `case` on `$1`.  

#### Functions
- `is_running`: test if worker process is alive.  
- `heartbeat_age`: compute heartbeat file age.  
- `start_worker`: launch worker, record PID.  
- `stop_worker`: terminate worker, clean PID file.  

#### Constants and configuration
- `PID_FILE=/run/queue-worker.pid`  
- `HEARTBEAT_FILE=/var/lib/queue-worker/heartbeat`  
- `MAX_HEARTBEAT_AGE=120`  
- `WORKER_BIN=/usr/local/bin/queue-worker`  

#### Dependencies
- External commands: `kill`, `nohup`, `date`, `stat`.  
- Worker binary: `/usr/local/bin/queue-worker` with `--heartbeat` option.  

#### Errors and exit codes
- `exit 2`: health check fails because worker not running.  
- `exit 1`: heartbeat older than `MAX_HEARTBEAT_AGE`.  
- `exit 3`: status reports stopped.  
- `exit 64`: invalid command usage.  
- No traps or cleanup beyond PID file removal.
