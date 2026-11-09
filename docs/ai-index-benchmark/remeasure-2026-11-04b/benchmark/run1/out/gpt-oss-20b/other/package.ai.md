### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:55:25Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides GPU‑accelerated byte‑histogram computation, a lock‑free power‑of‑two ring buffer for byte streams, and small utilities for backup rotation and sales reporting.

#### Purpose
- Deliver high‑performance byte histogram generation via OpenCL kernels.
- Offer a lightweight, single‑producer/single‑consumer ring buffer for byte data.
- Supply command‑line tools for directory backup rotation and CSV‑based sales analytics.

#### Responsibilities
- **GPU Histogram** – split byte stream into local partial histograms and reduce to a global 256‑bin result.
- **Ring Buffer** – allocate, write, read, and query a power‑of‑two circular buffer without locks.
- **Utilities** – create timestamped `.tar.gz` archives with retention, parse CSV orders, aggregate monthly/region revenue.

#### Key units
- **OpenCL kernels**: `histogram_partial`, `histogram_reduce`; constant `BINS = 256`.
- **Ring buffer API**: `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.
- **Scripts**: `rotate_backups.sh`, `sales_report.py` (functions `parse_orders`, `revenue_by_month_and_region`, `main`).

#### Data flow
1. **Histogram**: input byte array → `histogram_partial` writes per‑group partials → `histogram_reduce` aggregates → final 256‑bin histogram.
2. **Ring buffer**: `ringbuf_init` sets up buffer → `ringbuf_write` copies data (wrap‑around if needed) → `ringbuf_read` retrieves data → status queries report used/free space.
3. **Utilities**: `rotate_backups.sh` archives source → deletes old archives → `sales_report.py` reads CSVs → aggregates revenue → prints tab‑separated report.

#### Dependencies
- **OpenCL**: built‑in functions (`get_local_id`, `atomic_inc`, etc.).
- **C standard library**: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- **Shell utilities**: `tar`, `date`, `du`, `ls`, `rm`, `mkdir`, `mv`.
- **Python modules**: `argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`.

#### Cross‑cutting
- Power‑of‑two capacity enforcement and modulo arithmetic for ring buffer wrap‑around.
- Atomic increments and barriers for thread‑safe histogram updates.
- Consistent error handling: status enums in C, `ValueError` in Python, `set -euo pipefail` in Bash.
- Single‑producer/single‑consumer model for the ring buffer; no locks required.
