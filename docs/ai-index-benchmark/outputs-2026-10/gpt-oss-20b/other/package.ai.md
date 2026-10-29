### other
- H: 1.0
- C: 142CEE21
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:54:17Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides a lock‑free ring buffer, OpenCL byte‑histogram kernels, and shell/Python utilities for backup and sales reporting.

#### Purpose
- Offer reusable low‑latency data structures and GPU kernels for byte‑stream processing.
- Supply command‑line tools for directory backup rotation and CSV‑based sales aggregation.

#### Responsibilities
- **Ring buffer**: allocate, manage, and transfer byte streams with single‑producer/single‑consumer semantics.
- **OpenCL kernels**: compute 256‑bin histograms in two passes (local accumulation → global reduction).
- **Backup scripts**: create timestamped `.tar.gz` archives, enforce retention limits.
- **Sales reporting**: parse CSV orders, filter by currency, aggregate monthly revenue per region.

#### Key units
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.
- `histogram_partial`, `histogram_reduce` (OpenCL C kernels).
- `rotate_backups.sh` (Bash backup rotation).
- `sales_report.py` with `Order`, `parse_orders`, `revenue_by_month_and_region`, `main`.

#### Data flow
1. **Ring buffer**: `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer) → `ringbuf_free`.
2. **Histogram**: input byte stream → `histogram_partial` writes per‑group partials → `histogram_reduce` produces final 256‑bin histogram.
3. **Backup**: `rotate_backups.sh` validates args → creates archive → deletes excess archives.
4. **Sales report**: `parse_orders` reads CSV → yields `Order` → `revenue_by_month_and_region` aggregates → `main` prints results.

#### Dependencies
- C99 standard headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`).
- OpenCL C 1.2 built‑ins (`get_local_id`, `atomic_inc`, etc.).
- Shell utilities (`tar`, `date`, `du`, `ls`, `basename`, `dirname`, `mkdir`, `rm`, `echo`).
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`).

#### Cross-cutting
- Power‑of‑two size enforcement and wrap‑around logic in ring buffer.
- Two‑pass reduction pattern and atomic operations in OpenCL kernels.
- Consistent error/status codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- Simple logging and exit‑status conventions in scripts.
