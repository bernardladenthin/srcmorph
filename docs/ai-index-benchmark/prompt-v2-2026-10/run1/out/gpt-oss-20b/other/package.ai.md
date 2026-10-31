### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:43:04Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A GPU‑accelerated histogram engine backed by a lock‑free ring buffer and a set of shell/Python utilities for data backup and sales reporting.

#### Purpose
- Compute 256‑bin byte histograms on OpenCL devices.
- Provide a fast, single‑producer/single‑consumer byte stream buffer.
- Offer command‑line tools for backup rotation and sales analytics.

#### Responsibilities
- **GPU histogram pipeline** – two‑pass OpenCL kernels that build per‑group partial histograms and reduce them to a final 256‑bin result.
- **Ring buffer core** – power‑of‑two circular buffer with atomic head/tail indices, exposing init, free, read, write, and space queries.
- **Utility scripts** – Bash script for timestamped backup rotation; Python module for parsing CSV orders and generating monthly revenue reports.

#### Key units
- `histogram_partial` / `histogram_reduce` kernels (OpenCL) – perform local accumulation and global reduction.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – C API for ring buffer lifecycle and data movement.
- `rotate_backups.sh` – Bash script that creates `<basename>_YYYYMMDDTHHMMSSZ.tar.gz` archives and prunes old ones.
- `sales_report.py` – Python module with `parse_orders`, `revenue_by_month_and_region`, and a CLI `main`.

#### Data flow
1. **Histogram**  
   - Host enqueues `histogram_partial` → per‑group local histograms written to a global `partial` buffer.  
   - Host enqueues `histogram_reduce` → aggregates `partial` into final `histogram`.
2. **Ring buffer**  
   - `ringbuf_init` allocates a power‑of‑two buffer.  
   - Producer calls `ringbuf_write` → updates `head`.  
   - Consumer calls `ringbuf_read` → updates `tail`.  
   - `ringbuf_used` / `ringbuf_free_space` report current usage.
3. **Scripts**  
   - `rotate_backups.sh` tar‑compresses a source directory, timestamps, stores, and deletes excess archives.  
   - `sales_report.py` reads CSV files, filters by currency, aggregates revenue per month/region, and prints a tab‑separated report.

#### Dependencies
- OpenCL 1.2 built‑ins (`get_local_id`, `atomic_inc`, etc.) for histogram kernels.
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer implementation.
- Bash utilities (`tar`, `date`, `du`, `ls`) for backup rotation.
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`, `sys`) for sales reporting.

#### Cross-cutting
- **Two‑pass reduction** pattern in histogram kernels: local accumulation → global aggregation.
- **Atomic operations** for thread‑safe bin updates and ring buffer head/tail updates.
- **Power‑of‑two buffer size** enforced in `ringbuf_init`; invalid sizes return `RINGBUF_EINVAL`.
- **Error handling**: kernels exit on out‑of‑range indices; ring buffer functions return status codes or byte counts; scripts exit on command failure with descriptive stderr messages.
- **UTC timestamps** (`YYYYMMDDTHHMMSSZ`) used consistently in backup filenames.
