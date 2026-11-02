### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:03:49Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides GPU‑accelerated byte‑histogram computation, a lock‑free ring buffer for streaming data, and command‑line utilities for backup rotation and sales reporting.

#### Purpose
- Deliver high‑performance byte‑histogram generation via OpenCL.
- Offer a lightweight, non‑blocking circular buffer for single‑producer/single‑consumer byte streams.
- Supply shell and Python tools for routine backup and revenue analysis tasks.

#### Responsibilities
- **GPU Kernels** – compute 256‑bin histograms in two passes (partial per‑group, global reduction).
- **Ring Buffer** – manage fixed‑size, power‑of‑two circular storage with non‑blocking read/write and usage metrics.
- **Utilities** – automate directory backup rotation and generate monthly sales revenue reports from CSV data.

#### Key units
- `histogram_partial`, `histogram_reduce` (OpenCL kernels) – build and reduce per‑group histograms.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` – core ring‑buffer API.
- `rotate_backups.sh` – Bash script for timestamped tarball creation and retention cleanup.
- `sales_report.py` – Python module exposing `parse_orders`, `revenue_by_month_and_region`, and CLI `main`.

#### Data flow
1. **Histogram**: input byte stream → `histogram_partial` writes per‑group partials → `histogram_reduce` aggregates into final 256‑bin histogram.
2. **Ring Buffer**: `ringbuf_init` allocates buffer → `ringbuf_write` copies producer data → `ringbuf_read` retrieves consumer data → usage queried via `ringbuf_used`/`ringbuf_free_space`.
3. **Utilities**: `rotate_backups.sh` validates paths, creates `*.tar.gz`, moves to backup dir, deletes excess archives; `sales_report.py` parses CSVs, filters orders, aggregates revenue by month/region, outputs TSV.

#### Dependencies
- **OpenCL** built‑ins (`get_local_id`, `atomic_inc`, etc.) for kernels.
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer.
- Shell utilities (`tar`, `date`, `du`, `ls`, `rm`, `mv`, `mkdir`, `basename`, `dirname`) for backup script.
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for sales reporting.

#### Cross-cutting
- Two‑pass reduction pattern and atomic increments in GPU kernels.
- Power‑of‑two sizing and bitwise masking for ring‑buffer wrap‑around.
- Single‑producer/single‑consumer design eliminates locks in ring buffer.
- Robust error handling: ring buffer returns `RINGBUF_*` codes; shell script uses `set -euo pipefail`; Python raises `ValueError` on malformed CSV.
