### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:55:35Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A lightweight, OpenCL‑accelerated image‑processing toolkit that builds byte‑frequency histograms, supplies a lock‑free circular buffer for streaming data, and offers shell/Python helpers for backup and sales reporting.

#### Purpose
- Deliver GPU‑based histogram computation for byte streams.
- Provide a fast, single‑producer/single‑consumer ring buffer for byte data.
- Supply auxiliary scripts for data backup and sales analytics.

#### Responsibilities
- **GPU Histogram**: two‑pass OpenCL kernels that aggregate per‑group partial histograms and reduce them to a global 256‑bin result.
- **Ring Buffer**: power‑of‑two circular buffer with non‑blocking read/write, capacity queries, and minimal error handling.
- **Utilities**: Bash script for rotating compressed backups; Python module for parsing CSV orders, filtering by currency, and aggregating revenue by month and region.

#### Key units
- `histogram_partial`, `histogram_reduce` (OpenCL kernels) – compute per‑group and global histograms.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` – C API for the lock‑free buffer.
- `rotate_backups.sh` – Bash script for timestamped tar.gz archiving and retention.
- `sales_report.py` – Python module exposing `parse_orders`, `revenue_by_month_and_region`, and a CLI `main`.

#### Data flow
1. **Histogram**: byte array → `histogram_partial` (local bins) → `partial` buffer → `histogram_reduce` → final 256‑bin histogram.
2. **Ring Buffer**: `ringbuf_init` allocates memory → `ringbuf_write` copies data, updates head → `ringbuf_read` copies data, updates tail → `ringbuf_used`/`ringbuf_free_space` report state.
3. **Utilities**: `rotate_backups.sh` takes source dir → creates `tar.gz` → prunes old archives; `sales_report.py` reads CSV → parses `Order` objects → aggregates revenue → prints TSV.

#### Dependencies
- **OpenCL**: built‑ins (`get_local_id`, `atomic_inc`, etc.) for histogram kernels.
- **C Standard Library**: `malloc`, `free`, `memcpy` for ring buffer.
- **Shell Tools**: `tar`, `date`, `du`, `ls`, `mkdir`, `rm`, `mv`, `echo` for backup script.
- **Python Standard Library**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal` for sales reporting.

#### Cross-cutting
- Power‑of‑two sizing and bit‑masking for efficient wrap‑around in the ring buffer.
- Two‑pass aggregation pattern in the histogram kernels.
- Consistent error handling: return codes for C functions, exceptions for Python, exit codes for shell scripts.
- Minimal configuration: backup retention count, report currency defaults.
