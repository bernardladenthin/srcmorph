### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:58:34Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A lightweight, cross‑platform toolkit for efficient byte‑stream processing, circular buffering, and automated data‑management scripts.

#### Purpose
- GPU‑accelerated byte histogram computation.
- Lock‑free ring buffer for single‑producer/single‑consumer byte streams.
- Automated backup rotation and sales‑report generation.

#### Responsibilities
- **GPU kernels**: build per‑group partial histograms and reduce them to a final 256‑bin result.
- **Ring buffer**: initialize, enqueue, dequeue, and query buffer state without locks.
- **Scripts**: rotate directory backups and aggregate monthly revenue from CSV orders.

#### Key units
- `histogram_partial` / `histogram_reduce` (OpenCL kernels) – per‑group accumulation and global reduction.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` – core ring‑buffer API.
- `rotate_backups.sh` – backup rotation logic.
- `sales_report.py` – `Order` dataclass, `parse_orders`, `revenue_by_month_and_region`, `main` CLI.

#### Data flow
1. **Histogram**: input byte stream → `histogram_partial` (local bins) → `partial` buffer → `histogram_reduce` → final 256‑bin histogram.
2. **Ring buffer**: `ringbuf_init` → data array → `ringbuf_write` (head update) → `ringbuf_read` (tail update) → `ringbuf_used` / `ringbuf_free_space` for state.
3. **Scripts**: `rotate_backups.sh` archives source → cleans old archives; `sales_report.py` parses CSV → filters by currency → aggregates by month/region → prints report.

#### Dependencies
- OpenCL 1.2 built‑ins for kernels.
- Standard C headers (`stdlib.h`, `string.h`, `stddef.h`, `stdint.h`) for ring buffer.
- Bash utilities (`tar`, `date`, `du`, `ls`, `basename`, `dirname`, `mkdir`, `rm`, `echo`) for backup script.
- Python standard library (`argparse`, `csv`, `sys`, `collections`, `dataclasses`, `datetime`, `decimal`) for report script.

#### Cross‑cutting
- Power‑of‑two buffer sizing and modulo arithmetic for wrap‑around.
- Atomic increments and local memory usage in kernels to minimize global traffic.
- UTC timestamps for naming archives and reporting dates.
- Robust error handling: Bash `set -euo pipefail`; Python `ValueError` propagation.
