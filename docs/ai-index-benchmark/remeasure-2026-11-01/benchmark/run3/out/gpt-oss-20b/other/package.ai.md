### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:08:40Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A lightweight image‑processing and data‑transfer toolkit that builds byte histograms on GPU, offers a lock‑free ring buffer for streaming, and supplies shell/Python helpers for backup and sales reporting.

#### Purpose
- GPU‑accelerated byte histogram construction for image or arbitrary data streams.
- Thread‑safe, power‑of‑two circular buffer for single‑producer/single‑consumer byte streams.
- Command‑line utilities for directory backup rotation and monthly sales revenue reporting.

#### Responsibilities
- **GPU kernels** – compute per‑group partial histograms and reduce them to a global 256‑bin result.
- **Ring buffer** – allocate, manage, read/write, and query a lock‑free circular buffer.
- **Scripts** – automate backup creation/rotation and generate revenue reports from CSV orders.

#### Key units
- `histogram_partial` / `histogram_reduce` kernels (OpenCL) – two‑pass reduction for 256‑bin histograms.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` – C API for a power‑of‑two ring buffer.
- `rotate_backups.sh` – Bash script for timestamped tar‑gzip archives with retention policy.
- `sales_report.py` – Python module exposing `parse_orders`, `revenue_by_month_and_region`, `main`; uses `Order` dataclass.

#### Data flow
1. **Histogram**: byte stream → `histogram_partial` (per‑group) → `histogram_reduce` (global) → 256‑bin histogram.
2. **Ring buffer**: init → write (head advance) → read (tail advance) → query used/free space → free.
3. **Backup**: source dir → tar‑gzip → timestamped archive → retention cleanup.
4. **Sales report**: CSV files → parse orders → filter/aggregate by month & region → TSV output.

#### Dependencies
- OpenCL built‑ins (`get_local_id`, `atomic_inc`, etc.) for kernels.
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer.
- Bash utilities (`tar`, `date`, `du`, `ls`, `rm`, `mv`, `mkdir`, `cut`) for backup script.
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for sales report.

#### Cross-cutting
- Two‑pass reduction pattern and atomic bin updates in kernels.
- Power‑of‑two size enforcement and wrap‑around handling in ring buffer.
- Strict error handling: return codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`), exit statuses, and `ValueError` for malformed CSV.
- Thread‑free design for ring buffer; lock‑free single‑producer/single‑consumer usage.
- Safe shell execution (`set -euo pipefail`) and traps in backup script.
