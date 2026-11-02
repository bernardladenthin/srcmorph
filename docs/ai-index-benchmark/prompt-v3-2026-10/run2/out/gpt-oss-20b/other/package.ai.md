### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:22:31Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A lightweight, multi‑layered toolkit for handling byte streams, from low‑level GPU‑accelerated histograms to lock‑free ring buffers, plus auxiliary shell/Python utilities for backup and sales reporting.  

#### Purpose  
- Provide efficient, thread‑safe byte‑stream processing primitives.  
- Offer auxiliary scripting tools for data backup and revenue analysis.  

#### Responsibilities  
- **GPU kernels**: compute 256‑bin histograms via two‑pass OpenCL kernels.  
- **Ring buffer**: lock‑free, power‑of‑two circular buffer for single‑producer/single‑consumer byte streams.  
- **Scripts**: automate backup rotation and generate monthly sales revenue reports from CSV exports.  

#### Key units  
- `histogram_partial`, `histogram_reduce` (OpenCL kernels) – local accumulation and global reduction of byte histograms.  
- `ringbuf_t`, `ringbuf_status_t` – core data structure and status enum.  
- `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_free` – public API for ring buffer lifecycle and data movement.  
- `rotate_backups.sh` – Bash script for timestamped archive creation and retention.  
- `sales_report.py` – Python module with `parse_orders`, `revenue_by_month_and_region`, and CLI `main`.  

#### Data flow  
1. **Histogram**: `histogram_partial` reads input bytes, updates local bins atomically, writes partial histograms; `histogram_reduce` aggregates partials into final 256‑bin histogram.  
2. **Ring buffer**: `ringbuf_init` allocates buffer; `ringbuf_write` copies data into buffer, wrapping via modulo; `ringbuf_read` extracts data similarly; `ringbuf_used`/`ringbuf_free_space` compute usage; `ringbuf_free` releases memory.  
3. **Scripts**: `rotate_backups.sh` creates a timestamped tar.gz of a source directory, then prunes older archives; `sales_report.py` parses CSVs into `Order` objects, aggregates revenue by month/region, and outputs tab‑separated results.  

#### Dependencies  
- OpenCL 1.2 built‑ins (`get_local_id`, `atomic_inc`, etc.) for kernels.  
- Standard C library (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer.  
- Bash built‑ins and utilities (`tar`, `date`, `du`, `cut`, `ls`) for backup script.  
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for reporting script.  

#### Cross‑cutting  
- **Atomicity & synchronization**: kernels use atomic increments and barriers; ring buffer relies on single‑producer/single‑consumer semantics without locks.  
- **Power‑of‑two sizing**: ring buffer capacity enforcement and modulo arithmetic for wrap‑around.  
- **Error handling**: consistent status codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) and non‑zero exit codes for scripts.  
- **Configuration constants**: `BINS = 256` for histograms; default backup retention (7) and currency (EUR) in scripts.
