### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:49:39Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A lightweight, OpenCL‑based image‑processing toolkit that builds byte histograms, supplies a lock‑free ring buffer for streaming data, and offers shell/Python helpers for backup and sales reporting.

#### Purpose
- Deliver efficient byte‑histogram computation via OpenCL kernels.
- Provide a reusable, power‑of‑two ring buffer for single‑producer/single‑consumer streams.
- Offer ancillary scripts for backup rotation and CSV‑based revenue reporting.

#### Responsibilities
- **Histogram Kernels** – two‑pass reduction of byte streams into a 256‑bin histogram.
- **Ring Buffer** – lock‑free circular buffer with allocation, read/write, and space queries.
- **Utility Scripts** – shell script for rotating tar.gz backups; Python module for parsing orders and generating revenue reports.

#### Key units
- `histogram_partial`, `histogram_reduce` (OpenCL kernels) – compute per‑group partials and global histogram.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – core ring‑buffer API.
- `rotate_backups.sh` – backup rotation logic.
- `sales_report.py` – `parse_orders`, `revenue_by_month_and_region`, `Order` dataclass.

#### Data flow
1. **Histogram**: input byte array → `histogram_partial` writes per‑group partials → `histogram_reduce` aggregates into final 256‑bin histogram.
2. **Ring Buffer**: `ringbuf_init` allocates buffer; `ringbuf_write` copies producer data; `ringbuf_read` extracts consumer data; status functions report usage.
3. **Scripts**: `rotate_backups.sh` validates paths, creates tar.gz, prunes old archives; `sales_report.py` reads CSVs, filters by currency, aggregates revenue per month/region, outputs TSV.

#### Dependencies
- OpenCL built‑ins (`atomic_inc`, `barrier`, etc.) for kernels.
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer.
- Shell utilities (`tar`, `date`, `du`, `ls`, `rm`, `mv`) for backup script.
- Python stdlib (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for sales report.

#### Cross-cutting
- **Two‑pass reduction** pattern in kernels; atomic operations for thread safety.
- **Power‑of‑two invariant** in ring buffer; modulo arithmetic for indices.
- **Lock‑free design**: single‑producer/single‑consumer guarantees without mutexes.
- **Consistent error handling**: `ringbuf_status_t` codes, exit status conventions in scripts, clear logging via `echo`/`print`.
