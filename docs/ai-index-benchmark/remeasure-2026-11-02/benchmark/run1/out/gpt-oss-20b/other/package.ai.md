### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:33:08Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A collection of utilities for processing byte streams, managing lock‑free circular buffers, and automating backup and sales reporting tasks.

#### Purpose
- Provide GPU‑accelerated byte histogram computation.
- Offer a lightweight, single‑producer/single‑consumer ring buffer for byte data.
- Automate directory backups and generate monthly sales reports from CSV exports.

#### Responsibilities
- **GPU kernels** – compute 256‑bin histograms via two‑pass OpenCL reduction.
- **Ring buffer** – allocate, read/write, and track capacity for power‑of‑two circular buffers.
- **Scripts** – rotate tar.gz backups and aggregate sales revenue by month/region.

#### Key units
- `histogram_partial`, `histogram_reduce` (OpenCL kernels) – local and global histogram passes.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – core ring buffer API.
- `rotate_backups.sh` – backup rotation script.
- `sales_report.py` – `parse_orders`, `revenue_by_month_and_region`, `main` for CSV processing.

#### Data flow
1. **Histogram**: host → device global buffer → `histogram_partial` (local bins) → `histogram_reduce` (final 256‑bin histogram).
2. **Ring buffer**: `ringbuf_init` → producer `ringbuf_write` → consumer `ringbuf_read` → `ringbuf_used`/`ringbuf_free_space` → `ringbuf_free`.
3. **Scripts**:  
   - Backup: source directory → tar.gz → backup directory → retention cleanup.  
   - Sales: CSV → `parse_orders` → `Order` objects → `revenue_by_month_and_region` → printed TSV.

#### Dependencies
- OpenCL 1.2 built‑ins for kernels.
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer.
- Shell utilities (`tar`, `date`, `du`, `ls`, `rm`, `mv`, `basename`, `dirname`, `cut`) for backup script.
- Python modules (`argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`) for sales report.

#### Cross-cutting
- Two‑pass reduction pattern and atomic increments in kernels.
- Power‑of‑two capacity enforcement and wrap‑around logic in ring buffer.
- Error handling via status enums and exit codes; UTC timestamps for backups; decimal arithmetic for revenue calculations.
