### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:46:38Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides GPU‑accelerated byte‑histogram computation, a lock‑free ring buffer for byte streams, and shell/Python utilities for backup rotation and sales reporting.

#### Purpose
- Deliver high‑performance byte‑histogram kernels for OpenCL workloads.
- Offer a lightweight, single‑producer/single‑consumer ring buffer for byte data.
- Automate backup maintenance and generate monthly revenue reports from CSV orders.

#### Responsibilities
- **GPU Kernels** – compute 256‑bin histograms via two‑pass reduction (partial + reduce).
- **Ring Buffer** – initialize, write, read, and expose metrics for a power‑of‑two sized byte buffer.
- **Scripts** – rotate backups (`rotate_backups.sh`) and produce revenue summaries (`sales_report.py`).

#### Key units
- `histogram_partial`, `histogram_reduce` (OpenCL kernels) – build and aggregate 256‑bin histograms.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` – C API for a lock‑free ring buffer.
- `rotate_backups.sh` – Bash script for timestamped `.tar.gz` backups and pruning.
- `sales_report.py` – Python module with `parse_orders`, `revenue_by_month_and_region`, and CLI `main`.

#### Data flow
1. **Histogram**: byte stream → `histogram_partial` (per‑group local bins) → `histogram_reduce` (global aggregation) → 256‑bin result.
2. **Ring Buffer**: `ringbuf_init` → `ringbuf_write` (head update) → `ringbuf_read` (tail update) → metrics via `ringbuf_used` / `ringbuf_free_space`.
3. **Scripts**:  
   - Backup: source directory → `tar` → `.tar.gz` → backup directory → prune old archives.  
   - Sales: CSV files → `parse_orders` → `Order` objects → `revenue_by_month_and_region` → tab‑separated output.

#### Dependencies
- **OpenCL** built‑ins (`get_local_id`, `atomic_inc`, etc.) for kernels.
- **C Standard Library** (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer.
- **Shell utilities** (`tar`, `date`, `du`, `ls`, `rm`, `mkdir`, `basename`, `cut`, `echo`, `trap`) for backup script.
- **Python** modules (`argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`) for sales report.

#### Cross‑cutting
- Two‑stage reduction pattern and atomic increments in kernels.
- Power‑of‑two capacity enforcement and modulo arithmetic in ring buffer.
- Consistent error handling: `ringbuf_status_t` codes, Bash `set -euo pipefail`, Python `ValueError` propagation.
- Centralized configuration constants (`KEEP`, `REQUIRED_COLUMNS`) across scripts.
