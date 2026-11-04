### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:34:45Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides a lightweight, high‑performance byte‑stream processing toolkit: a GPU‑accelerated histogram builder, a lock‑free ring buffer, and command‑line utilities for data backup and revenue reporting.  

#### Purpose
- Deliver efficient byte‑level data structures and algorithms for embedded or high‑throughput systems.  
- Offer reusable utilities for backup and reporting in operational scripts.  

#### Responsibilities
- **GPU histogram computation** – split work into per‑group partial histograms and a global reduction.  
- **Lock‑free ring buffer** – single‑producer/single‑consumer byte stream with power‑of‑two sizing.  
- **Backup & reporting scripts** – automated tarball rotation and CSV‑based revenue aggregation.  

#### Key units
- **OpenCL kernels**  
  - `histogram_partial` – builds per‑group 256‑bin histograms.  
  - `histogram_reduce` – aggregates partials into final 256‑bin histogram.  
  - `#define BINS 256` – bin count constant.  
- **Ring buffer API**  
  - `ringbuf_t` – buffer struct (ptr, capacity, head, tail).  
  - `ringbuf_status_t` – status enum (`OK`, `EINVAL`, `ENOMEM`).  
  - `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.  
- **Scripts**  
  - `rotate_backups.sh` – tar.gz backup with retention.  
  - `sales_report.py` – `parse_orders`, `revenue_by_month_and_region`, `main`.  

#### Data flow
1. **Histogram** – host enqueues `histogram_partial` → per‑group local histograms written to global `partial` buffer → host enqueues `histogram_reduce` → final 256‑bin histogram produced.  
2. **Ring buffer** – `ringbuf_init` allocates array → `ringbuf_write` copies producer data, advances head → `ringbuf_read` copies consumer data, advances tail → metrics via `ringbuf_used`/`ringbuf_free_space`.  
3. **Scripts** – `rotate_backups.sh` creates timestamped tarball, moves to backup dir, prunes old archives; `sales_report.py` parses CSV, filters by currency, aggregates revenue by month/region, outputs TSV.  

#### Dependencies
- **OpenCL** – 1.2 built‑ins (`get_local_id`, `atomic_inc`, etc.).  
- **C standard library** – `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.  
- **Shell utilities** – `tar`, `date`, `du`, `ls`, `mkdir`, `mv`, `rm`.  
- **Python modules** – `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.  

#### Cross-cutting
- Power‑of‑two sizing and bitwise masking in ring buffer for efficient wrap‑around.  
- Single‑threaded producer/consumer assumption; no locks.  
- Atomic increments in kernels for thread‑safe bin updates.  
- Consistent error codes (`ringbuf_status_t`, script exit codes).  
- Minimal, clear APIs and command‑line interfaces.
