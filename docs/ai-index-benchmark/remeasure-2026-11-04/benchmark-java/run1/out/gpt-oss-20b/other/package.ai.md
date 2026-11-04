### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:19:05Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides a reusable byte‑level histogram generator, a lock‑free ring buffer, and small shell/Python utilities for backups and sales reporting.

#### Purpose
- Deliver GPU‑accelerated byte histograms and a lightweight circular buffer for streaming data.
- Offer simple backup and reporting scripts for operational tasks.

#### Responsibilities
- **GPU kernels**: compute 256‑bin histograms via two‑pass OpenCL reduction.
- **Ring buffer**: single‑producer/single‑consumer byte queue with power‑of‑two sizing.
- **Scripts**: automate directory backups and aggregate CSV sales data.

#### Key units
- **`histogram_partial` / `histogram_reduce`** – OpenCL kernels; `BINS` constant.
- **`ringbuf_t` / `ringbuf_status_t`** – core buffer struct and status enum.
- **`ringbuf_init`, `ringbuf_write`, `ringbuf_read`** – API for buffer lifecycle and I/O.
- **`rotate_backups.sh`** – Bash script for timestamped archiving and pruning.
- **`sales_report.py`** – Python module with `parse_orders`, `revenue_by_month_and_region`, and CLI `main`.

#### Data flow
1. **Histogram**: input byte stream → `histogram_partial` (per‑group partials) → `histogram_reduce` (final 256‑bin array).
2. **Ring buffer**: `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer) → status queries → `ringbuf_free`.
3. **Scripts**:  
   - `rotate_backups.sh`: source → tar‑gz → archive → prune old → exit.  
   - `sales_report.py`: CSV files → `parse_orders` → `revenue_by_month_and_region` → printed report.

#### Dependencies
- OpenCL runtime for kernels; standard C library for ring buffer; shell utilities (`tar`, `date`, etc.) for backup; Python stdlib (`argparse`, `csv`, `decimal`) for reporting.

#### Cross-cutting
- Power‑of‑two constraint and lock‑free design in ring buffer.  
- Two‑pass reduction pattern and atomic bin updates in kernels.  
- Consistent error handling via status codes and non‑zero exit codes.  
- Minimal logging (echo/print) and no shared code between scripts.
