### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:42:21Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides a unified toolkit for byte‑level data processing, including GPU‑accelerated histograms, a lock‑free ring buffer, and utility scripts for backup and sales reporting.

#### Purpose
- Deliver high‑performance byte‑histogram computation via OpenCL.
- Offer a reusable, lock‑free circular buffer for single‑producer/single‑consumer byte streams.
- Supply shell and Python utilities for backup rotation and sales data aggregation.

#### Responsibilities
- **GPU Histogram Engine** – two‑pass reduction kernels for 256‑bin byte histograms.
- **Ring Buffer API** – power‑of‑two circular buffer with init, free, read, write, and capacity queries.
- **Utility Scripts** – Bash backup rotation and Python monthly sales reporting.

#### Key units
- `histogram_partial` / `histogram_reduce` kernels (OpenCL) – build and aggregate per‑group histograms.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – core ring buffer types and operations.
- `rotate_backups.sh` – backup creation and pruning script.
- `sales_report.py` – CSV parser, `revenue_by_month_and_region`, CLI entry point.

#### Data flow
1. **Histogram**: byte stream → `histogram_partial` (per‑group local bins) → `histogram_reduce` (global sum) → final 256‑bin histogram.
2. **Ring Buffer**: producer writes via `ringbuf_write`; consumer reads via `ringbuf_read`; indices wrap modulo power‑of‑two capacity; metrics via `ringbuf_used` / `ringbuf_free_space`.
3. **Utilities**: source directory → `tar` archive → backup directory; old archives removed. CSV files → `parse_orders` → aggregated revenue → printed report.

#### Dependencies
- OpenCL built‑ins (`get_local_id`, `atomic_inc`, `barrier`, etc.) for histogram kernels.
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer implementation.
- Bash utilities (`tar`, `date`, `du`, `ls`, `rm`, `mv`) for backup script.
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for sales report.

#### Cross-cutting
- **Power‑of‑two constraint** in ring buffer capacity checks.
- **Lock‑free design**: single‑producer/single‑consumer model, no mutexes.
- **Atomic operations** and **barriers** for intra‑group synchronization in OpenCL kernels.
- **Consistent error handling**: `ringbuf_status_t` codes, Bash `set -euo pipefail`, Python `ValueError` for CSV parsing.
- **UTC timestamps** for backup naming and report dates.
