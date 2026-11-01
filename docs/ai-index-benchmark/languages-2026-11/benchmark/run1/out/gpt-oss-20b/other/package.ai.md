### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:22:02Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides a lightweight, lock‑free ring buffer for byte streams, a two‑pass OpenCL histogram kernel, and shell/Python utilities for backup rotation and sales reporting.

#### Purpose
- Offer a reusable byte‑level circular buffer for single‑producer/single‑consumer scenarios.
- Compute 256‑bin histograms of arbitrary byte data on GPU via OpenCL.
- Automate backup retention and generate monthly revenue reports from CSV orders.

#### Responsibilities
- **Ring Buffer** – allocation, enqueue/dequeue, space queries, power‑of‑two enforcement.
- **OpenCL Histogram** – per‑group partial accumulation, global reduction, atomic bin updates.
- **Backup & Reporting Scripts** – timestamped archive creation, retention policy, CSV parsing, revenue aggregation.

#### Key units
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.
- `histogram_partial`, `histogram_reduce`, `#define BINS 256`.
- `rotate_backups.sh`, `sales_report.py`, `parse_orders`, `revenue_by_month_and_region`.

#### Data flow
1. **Ring Buffer**: `ringbuf_init` → `ringbuf_write`/`ringbuf_read` → `ringbuf_used`/`ringbuf_free_space`.
2. **Histogram**: byte array → `histogram_partial` writes per‑group partials → `histogram_reduce` aggregates into final 256‑bin histogram.
3. **Scripts**:  
   - Backup: source dir → `tar` → temp archive → final archive → delete old archives.  
   - Sales: CSV → `parse_orders` → `revenue_by_month_and_region` → tab‑separated output.

#### Dependencies
- C: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.  
- OpenCL 1.2 built‑ins (`get_local_id`, `atomic_inc`, etc.).  
- Bash utilities (`tar`, `date`, `du`, `ls`).  
- Python: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross‑cutting
- Power‑of‑two capacity checks and modulo arithmetic for wrap‑around.  
- Two‑pass reduction pattern with atomic operations in OpenCL.  
- Explicit status codes and error handling in ring buffer API.  
- UTC timestamps and cleanup traps in backup script; input validation and exit codes in both scripts.
