### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:04:03Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides a lightweight, lock‑free byte‑stream histogram pipeline and utilities for ring‑buffer handling and backup/reporting tasks.

#### Purpose
- Compute 256‑bin histograms of byte streams on GPU.
- Offer a single‑producer/single‑consumer ring buffer in C.
- Automate directory backups and generate monthly sales reports.

#### Responsibilities
- **GPU Histogram** – split work into local partial histograms and a global reduction pass.
- **Ring Buffer** – allocate, write, read, and query a power‑of‑two circular buffer without locks.
- **Backup & Reporting** – create timestamped tarballs with retention, parse CSV orders, aggregate revenue by month/region.

#### Key units
- `histogram_partial`, `histogram_reduce` (OpenCL kernels) – build and reduce per‑group histograms.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – C API for a lock‑free FIFO.
- `rotate_backups.sh` – Bash script for backup rotation.
- `sales_report.py` – Python module with `parse_orders`, `revenue_by_month_and_region`, `main`.

#### Data flow
1. **Histogram** – `histogram_partial` reads input bytes → local bins → writes partials; `histogram_reduce` sums partials → final 256‑bin histogram.
2. **Ring Buffer** – `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer) → status queries → `ringbuf_free`.
3. **Backup/Report** – `rotate_backups.sh` creates `.tar.gz` → trims old archives; `sales_report.py` parses CSV → aggregates → prints report.

#### Dependencies
- OpenCL built‑ins (`atomic_inc`, `barrier`, etc.) for histogram kernels.
- C standard library (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer.
- Shell utilities (`tar`, `date`, `du`, `ls`, `rm`, `mv`, `basename`, `dirname`) for backup script.
- Python standard library (`argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`) for reporting.

#### Cross-cutting
- Power‑of‑two capacity enforcement and bit‑masking in ring buffer.
- Two‑pass design (local accumulation + global reduction) in histogram kernels.
- Consistent error codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) and input validation across utilities.
